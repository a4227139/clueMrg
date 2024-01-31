package com.wa.cluemrg.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.GangBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.Gang;
import com.wa.cluemrg.entity.Graph;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.GangService;
import com.wa.cluemrg.util.JurisdictionUtil;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@RestController
@RequestMapping("/gang")
@Secured("ROLE_LEVEL1")
public class GangController {

    @Autowired
    private GangService gangService;
    String separators = "[,，\\s]+";

    private final ResourceLoader resourceLoader;

    @Autowired
    public GangController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    String ROOT_APPLICATION_PATH;
    String CLASS_PATH;
    String separator = File.separator;
    @PostConstruct
    public void init() throws IOException {
        //ROOT_APPLICATION_PATH=resourceLoader.getResource("").getFile().getAbsolutePath();
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        ROOT_APPLICATION_PATH= applicationHome.getDir().getParentFile()
                .getParentFile().getAbsolutePath();
        Resource resource = resourceLoader.getResource("classpath:");
        CLASS_PATH = resource.getURI().toString().replaceAll("file:","");
    }

    @PostMapping("/getGangList")
    public JsGridVO getGangList(@RequestBody PageBO<GangBo> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        Set<GangBo> resultSet = new HashSet<>();
        GangBo gangBo = pageBo.getData();
        String[] phoneArray;
        List<GangBo> list;
        List<GangBo> resultList = new ArrayList<>();
        if (!StringUtils.isEmpty(gangBo.getPhones())){
            phoneArray=gangBo.getPhones().split(separators);
            for (String phone : phoneArray){
                gangBo.setPhone(phone);
                list = gangService.exportAll(gangBo);
                resultSet.addAll(list);
            }
        }else {
            resultList = gangService.exportAll(pageBo.getData());
        }

        resultList.addAll(resultSet);
        PageInfo page = new PageInfo(resultList);
        JsGridVO<GangBo> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    @Secured("ROLE_LEVEL2")
    public int insert(@RequestBody Gang gang) {
        return gangService.insert(gang);
    }

    @PutMapping("/update")
    @Secured("ROLE_LEVEL2")
    public int update(@RequestBody Gang gang) {
        return gangService.update(gang);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_LEVEL2")
    public int delete(@RequestParam int id) {
        return gangService.delete(id);
    }

    @GetMapping("/getGang")
    public Gang getGang(@RequestParam("phone") String phone){
        Gang gang = gangService.genarateGang(phone);
        return gang;
    }


    @GetMapping("/getGraph")
    public Graph getGraph(@RequestParam("data") String data, @RequestParam("type") String type,
                          @RequestParam("winWidth") int winWidth, @RequestParam("winHeight") int winHeight){
        return gangService.getGraph(data,type,winWidth,winHeight);
    }

    @GetMapping("exportExcel")
    public void exportExcel(GangBo gangBo,HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("GOIP团伙", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            Set<GangBo> resultSet = new HashSet<>();
            String[] phoneArray=new String[]{};
            if (!StringUtils.isEmpty(gangBo.getPhones())){
                phoneArray=gangBo.getPhones().split(separators);
                for (String phone : phoneArray){
                    gangBo.setPhone(phone);
                    List<GangBo> list = gangService.exportAll(gangBo);
                    resultSet.addAll(list);
                }
            }else {
                resultSet.addAll(gangService.exportAll(gangBo));
            }
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), GangBo.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(Boolean.FALSE).sheet("团伙")
                    .doWrite(resultSet);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    String currentJurisdiction;
    SimpleDateFormat formatYMDHS = new SimpleDateFormat("yyyyMMddHHmmSS");
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    @GetMapping("/exportZip")
    @ResponseBody
    public void exportZip(@RequestParam("id") int id,@RequestParam("jurisdiction") String jurisdiction, HttpServletResponse response) throws Exception {
        Gang gang = gangService.get(id);
        gang.setIssueTime(LocalDateTime.now());
        gang.setJurisdiction(jurisdiction);
        currentJurisdiction=jurisdiction;
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            GangBo gangBo = new GangBo();
            BeanUtils.copyProperties(gang,gangBo);
            generateExcel(gangBo,zipOut);
            generateWord(gang,zipOut);
        }

        // Configure response headers
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(currentJurisdiction,"utf-8")+".zip");
        response.setContentType("application/zip");
        // Write the ZIP content to the response output stream
        response.getOutputStream().write(zipStream.toByteArray());
        gangService.update(gang);
    }

    private void generateExcel(GangBo gangBo, ZipOutputStream zipOut) throws IOException {
        //导出线索表
        String fileName = "GOIP团伙.xlsx";
        //创建文件夹
        String directory = ROOT_APPLICATION_PATH+separator+"gang"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+ separator;
        File file = new File(directory);
        file.mkdirs();
        String excelFileName = directory+ fileName;
        List<GangBo> list =new ArrayList<>();
        list.add(gangBo);
        EasyExcel.write(excelFileName, GangBo.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .autoCloseStream(Boolean.FALSE).sheet("团伙元素")
                .doWrite(list);
        addToZip(zipOut, excelFileName, fileName);
    }

    private void generateWord(Gang gang,ZipOutputStream zipOut) throws Exception {

        String fileName = "GOIP团伙线索通报.docx";
        GangBo gangBo = turnToTtGangBo(gang);
        Configure config = Configure.builder().useSpringEL().build();
        XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateGang.docx"),config).render(gangBo);
        // 创建文件夹
        String directory = ROOT_APPLICATION_PATH+separator+"gang"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+ separator;
        File file = new File(directory);
        file.mkdirs();
        String wordFileName = directory+ fileName;
        template.writeToFile(wordFileName);
        addToZip(zipOut, wordFileName, fileName);
    }


    private void addToZip(ZipOutputStream zipOut, String filePath, String entryName) throws IOException {
        byte[] buffer = new byte[1024];
        try (FileInputStream fis = new FileInputStream(filePath)) {
            zipOut.putNextEntry(new ZipEntry(entryName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zipOut.write(buffer, 0, length);
            }
            zipOut.closeEntry();
        }
    }


    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
    private GangBo turnToTtGangBo(Gang gang) {
        GangBo gangBo = new GangBo();
        String subOffice= JurisdictionUtil.getJurisdictionFullName(currentJurisdiction);
        BeanUtils.copyProperties(gang,gangBo);
        gangBo.setSubOffice(subOffice);
        gangBo.setDateFormatToday(format.format(new Date()));
        return gangBo;
    }

}

