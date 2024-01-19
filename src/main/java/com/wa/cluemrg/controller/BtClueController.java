package com.wa.cluemrg.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.util.PoitlIOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.BtClueBo;
import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.bo.CallLogExportBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.BtClueService;
import com.wa.cluemrg.service.CallLogService;
import com.wa.cluemrg.service.PhoneImeiService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@RestController
@RequestMapping("/btClue")
@Secured("ROLE_LEVEL1")
public class BtClueController {

    @Autowired
    BtClueService btClueService;
    @Autowired
    PhoneImeiService phoneImeiService;
    @Autowired
    CallLogService callLogService;

    private final ResourceLoader resourceLoader;

    @Autowired
    public BtClueController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    String ROOT_APPLICATION_PATH;
    String CLASS_PATH;
    String pathSeparator = File.pathSeparator;
    @PostConstruct
    public void init() throws IOException {
        //ROOT_APPLICATION_PATH=resourceLoader.getResource("").getFile().getAbsolutePath();
        ApplicationHome applicationHome = new ApplicationHome(this.getClass());
        ROOT_APPLICATION_PATH= applicationHome.getDir().getParentFile()
                .getParentFile().getAbsolutePath();
        Resource resource = resourceLoader.getResource("classpath:");
        CLASS_PATH = resource.getURI().toString().replaceAll("file:","");
    }

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.BtClue>
     */
    @PostMapping("/getClueList")
    public JsGridVO getClueList(@RequestBody PageBO<BtClue> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="CLUE_ID";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<BtClue> list = btClueService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BtClue> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return btClueService.getLatestDay();
    }

    @PostMapping("/insertClue")
    @Secured("ROLE_LEVEL2")
    public int insertClue(@RequestBody BtClue btClue) {
        return btClueService.insert(btClue);
    }

    @PutMapping("/updateClue")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestBody BtClue btClue) {
        return btClueService.update(btClue);
    }

    @DeleteMapping("/deleteClue")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestParam String clueId) {
        return btClueService.delete(clueId);
    }

    @PostMapping("/upload")
    @Secured("ROLE_LEVEL2")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return btClueService.dealUpload(file);
    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        Resource excelFile = new ClassPathResource("/templates/templateBT.xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getFilename() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    @GetMapping("/exportWord")
    public void exportWord(@RequestParam("clueId") String clueId,HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(clueId+"线索通报", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".docx");
            BtClue btClue = btClueService.select(clueId);
            btClue.setIssueTime(format2.format(new Date()));
            BtClueBo btClueBo = turnToBtClueBo(btClue);
            OutputStream out = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateBT.docx")).render(btClueBo);
            template.write(bos);
            bos.flush();
            out.flush();
            PoitlIOUtils.closeQuietlyMulti(template, bos, out);
            btClueService.update(btClue);
        } catch (Exception e) {
            log.error("word生成失败",e);
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

    @GetMapping("/exportSDWord")
    public void exportSDWord(@RequestParam("clueId") String clueId,HttpServletRequest request, HttpServletResponse response) throws IOException{
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("关于"+clueId+"的三定报告", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".docx");
            BtClue btClue = btClueService.select(clueId);
            BtClueBo btClueBo = turnToBtClueBo(btClue);
            OutputStream out = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateSD.docx")).render(btClueBo);
            template.write(bos);
            bos.flush();
            out.flush();
            PoitlIOUtils.closeQuietlyMulti(template, bos, out);
        } catch (Exception e) {
            log.error("word生成失败",e);
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

    @GetMapping("/exportZip")
    @ResponseBody
    public void exportZip(@RequestParam("clueId") String clueId, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            generateExcel(clueId,zipOut);
            generateWord(clueId,zipOut);
        }
        // Configure response headers
        response.setHeader("Content-Disposition", "attachment; filename="+clueId+".zip");
        response.setContentType("application/zip");

        // Write the ZIP content to the response output stream
        response.getOutputStream().write(zipStream.toByteArray());
    }


    private void generateExcel(String clueId,ZipOutputStream zipOut) throws Exception {
        BtClue btClue = btClueService.select(clueId);
        if (btClue==null){
            throw new Exception("找不到clueId："+clueId);
        }
        //导出线索表
        String btFileName=clueId+".xlsx";
        String btExcelFileName = ROOT_APPLICATION_PATH+pathSeparator+"btclue"+pathSeparator +clueId+ pathSeparator+ btFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+pathSeparator+"btclue"+pathSeparator +clueId+ pathSeparator);
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        List<BtClue> list = new ArrayList<>();
        list.add(btClue);
        EasyExcel.write(btExcelFileName, BtClue.class).withTemplate(this.getClass().getClassLoader().getResourceAsStream("templates/templateBTExport.xlsx")).needHead(false).sheet().doWrite(list);
        addToZip(zipOut, btExcelFileName, btFileName);
        //导出话单
        String callLogFileName=btClue.getPhone()+".xlsx";
        String callLogExcelFileName = ROOT_APPLICATION_PATH+pathSeparator+"btclue"+pathSeparator +clueId+ pathSeparator+ callLogFileName;
        CallLogBo callLog = new CallLogBo();
        callLog.setPhone(btClue.getPhone());
        PageHelper.startPage(1,10000);
        List<CallLogBo> callLogList = callLogService.exportAll(callLog);
        EasyExcel.write(callLogExcelFileName, CallLogExportBo.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("话单").doWrite(callLogList);
        addToZip(zipOut, callLogExcelFileName, callLogFileName);
    }


    private void generateWord(String clueId,ZipOutputStream zipOut) throws Exception {
        BtClue btClue = btClueService.select(clueId);
        if (btClue==null){
            throw new Exception("找不到clueId："+clueId);
        }
        btClue.setIssueTime(format2.format(new Date()));
        String fileName = clueId+"线索通报.docx";
        BtClueBo btClueBo = turnToBtClueBo(btClue);
        XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateBT.docx")).render(btClueBo);

        String wordFileName = ROOT_APPLICATION_PATH+pathSeparator+"btclue"+pathSeparator+clueId+pathSeparator+ fileName;
        template.writeToFile(wordFileName);

        addToZip(zipOut, wordFileName, fileName);
        btClueService.update(btClue);
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
    private BtClueBo turnToBtClueBo(BtClue btClue){
        BtClueBo btClueBo = new BtClueBo();
        BeanUtils.copyProperties(btClue,btClueBo);
        String jurisdiction = btClueBo.getJurisdiction();
        String subOffice=jurisdiction;
        switch (jurisdiction) {
            case "城中":
                subOffice="柳州市公安局城中分局";
                break;
            case "鱼峰":
                subOffice="柳州市公安局鱼峰分局";
                break;
            case "柳北":
                subOffice="柳州市公安局柳北分局";
                break;
            case "柳南":
                subOffice="柳州市公安局柳南分局";
                break;
            case "柳江":
                subOffice="柳州市公安局柳江分局";
                break;
            case "柳东":
                subOffice="柳州市公安局柳东分局";
                break;
            case "柳城":
                subOffice="柳城县公安局";
                break;
            case "鹿寨":
                subOffice="鹿寨县公安局";
                break;
            case "融安":
                subOffice="融安县公安局";
                break;
            case "融水":
                subOffice="融水苗族自治县公安局";
                break;
            case "三江":
                subOffice="三江侗族自治县公安局";
                break;
            default:
                break;
        }
        btClueBo.setSubOffice(subOffice);
        String clueId = btClueBo.getClueId();
        String dateFormat = clueId.substring(1,5) + "年" + clueId.substring(5,7) + "月" + clueId.substring(7,9) + "日";
        btClueBo.setDateFormat(dateFormat);
        btClueBo.setDateFormatToday(format.format(new Date()));
        PhoneImei phoneImei = new PhoneImei();
        phoneImei.setPhone(btClue.getPhone());
        List<PhoneImei> list = phoneImeiService.selectAll(phoneImei);
        String imei = "";
        for(PhoneImei item:list){
            imei+=item.getImei()+",";
        }
        if (!StringUtils.isEmpty(imei)){
            btClueBo.setImei(imei.substring(0,imei.length()-1));
        }
        return btClueBo;
    }
}

