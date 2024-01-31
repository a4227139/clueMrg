package com.wa.cluemrg.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.deepoove.poi.XWPFTemplate;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.*;
import com.wa.cluemrg.entity.TtClue;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.TtClueService;
import com.wa.cluemrg.service.CallLogService;
import com.wa.cluemrg.service.PhoneImeiService;
import com.wa.cluemrg.util.JurisdictionUtil;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
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
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Log4j2
@RestController
@RequestMapping("/ttClue")
@Secured("ROLE_LEVEL1")
public class TtClueController {

    @Autowired
    TtClueService ttClueService;
    @Autowired
    PhoneImeiService phoneImeiService;
    @Autowired
    CallLogService callLogService;

    private final ResourceLoader resourceLoader;

    @Autowired
    public TtClueController(ResourceLoader resourceLoader) {
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

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.TtClue>
     */
    @PostMapping("/getClueList")
    public JsGridVO getClueList(@RequestBody PageBO<TtClue> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)&&StringUtils.isEmpty(sortOrder)){
            PageHelper.orderBy("CLUE_TIME DESC ,CLUE_ID DESC");
        }else {
            PageHelper.orderBy(sortField+" "+sortOrder);
        }


        List<TtClue> list = ttClueService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<TtClue> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return ttClueService.getLatestDay();
    }

    @PostMapping("/insertClue")
    @Secured("ROLE_LEVEL2")
    public int insertClue(@RequestBody TtClue ttClue) {
        return ttClueService.insert(ttClue);
    }

    @PutMapping("/updateClue")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestBody TtClue ttClue) {
        return ttClueService.update(ttClue);
    }

    @DeleteMapping("/deleteClue")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestParam String clueId) {
        return ttClueService.delete(clueId);
    }

    @PostMapping("/upload")
    @Secured("ROLE_LEVEL2")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return ttClueService.dealUpload(file);
    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        Resource excelFile = new ClassPathResource("/templates/templateTT.xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getFilename() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }


    @GetMapping("/exportZip")
    @ResponseBody
    public void exportZip(@RequestParam("phones") String phones, HttpServletResponse response) throws Exception {
        ByteArrayOutputStream zipStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(zipStream)) {
            generateExcel(phones,zipOut);
            generateWord(phones,zipOut);
        }
        // Configure response headers
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(currentJurisdiction,"utf-8")+".zip");
        response.setContentType("application/zip");

        // Write the ZIP content to the response output stream
        response.getOutputStream().write(zipStream.toByteArray());
    }

    SimpleDateFormat formatYMDHS = new SimpleDateFormat("yyyyMMddHHmmSS");
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    String currentJurisdiction;
    String currentJzPerson;
    String currentFzPerson;
    String currentPhones;
    private void generateExcel(String phones,ZipOutputStream zipOut) throws Exception {
        String separators = "[,，\\s]+";
        String[] phoneArray = phones.split(separators);
        TtClue param =new TtClue();
        param.setPhones(phoneArray);
        List<TtClue> ttClueList = ttClueService.selectAll(param);
        if (ttClueList==null||ttClueList.size()==0){
            throw new Exception("phone："+phoneArray);
        }
        currentPhones= String.join("、",phoneArray);
        currentJurisdiction=ttClueList.get(0).getJurisdiction();
        currentJzPerson=ttClueList.get(0).getJzPerson();
        currentFzPerson=ttClueList.get(0).getFzPerson();
        for(TtClue ttClue:ttClueList){
            if (!currentJurisdiction.equals(ttClue.getJurisdiction())){
                throw new Exception("phone："+phoneArray+"辖区不一致");
            }
        }
        //导出线索表
        String ttFileName=currentJurisdiction+"-"+formatYMDHS.format(new Date())+".xlsx";
        String ttExcelFileName = ROOT_APPLICATION_PATH+separator+"ttclue"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+ separator+ ttFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+separator+"ttclue"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+separator);
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        List<String> excludeColumnFieldNames = new ArrayList<>();
        excludeColumnFieldNames.add("phones");
        EasyExcel.write(ttExcelFileName, TtClueExportBo.class).withTemplate(this.getClass().getClassLoader().getResourceAsStream("templates/templateTT.xlsx"))
                .needHead(false).excludeColumnFieldNames(excludeColumnFieldNames).sheet().doWrite(ttClueList);
        addToZip(zipOut, ttExcelFileName, ttFileName);
        //导出话单
        for(TtClue ttClue:ttClueList) {
            String callLogFileName = ttClue.getPhone() + ".xlsx";
            String callLogExcelFileName = ROOT_APPLICATION_PATH+separator+"ttclue"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+ separator + callLogFileName;
            CallLogBo callLog = new CallLogBo();
            callLog.setPhone(ttClue.getPhone());
            PageHelper.startPage(1, 10000);
            List<CallLogBo> callLogList = callLogService.exportAll(callLog);
            EasyExcel.write(callLogExcelFileName, CallLogExportBo.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet("话单").doWrite(callLogList);
            addToZip(zipOut, callLogExcelFileName, callLogFileName);
            ttClue.setIssueTime(format2.format(new Date()));
            ttClueService.update(ttClue);
        }
    }


    private void generateWord(String phones,ZipOutputStream zipOut) throws Exception {

        String fileName = "线索通报.docx";
        TtClueBo ttClueBo = turnToTtClueBo(phones);
        XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateGoip.docx")).render(ttClueBo);

        String wordFileName = ROOT_APPLICATION_PATH+separator+"ttclue"+separator+formatYMD.format(new Date())+ separator +currentJurisdiction+ separator+ fileName;
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
    private TtClueBo turnToTtClueBo(String phones){
        TtClueBo ttClueBo = new TtClueBo();
        String subOffice= JurisdictionUtil.getJurisdictionFullName(currentJurisdiction);
        ttClueBo.setSubOffice(subOffice);
        ttClueBo.setPhone(currentPhones);
        ttClueBo.setDateFormatToday(format.format(new Date()));
        return ttClueBo;
    }
}

