package com.wa.cluemrg.controller;


import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.AlarmReceipt;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.AlarmReceiptService;
import com.wa.cluemrg.util.DateUtil;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Log4j2
@RestController
@RequestMapping("/alarmReceipt")
@Secured("ROLE_LEVEL1")
public class AlarmReceiptController {

    @Autowired
    AlarmReceiptService alarmReceiptService;
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    private final ResourceLoader resourceLoader;

    @Autowired
    public AlarmReceiptController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    String ROOT_APPLICATION_PATH;
    String CLASS_PATH;
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
     * @return com.response.Response<com.wa.cluemrg.entity.AlarmReceipt>
     */
    @PostMapping("/getAlarmList")
    public JsGridVO getAlarmList(@RequestBody PageBO<AlarmReceipt> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="ALARM_TIME";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<AlarmReceipt> list = alarmReceiptService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<AlarmReceipt> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return alarmReceiptService.getLatestDay();
    }

    @PostMapping("/insertAlarm")
    @Secured("ROLE_LEVEL2")
    public int insertAlarm(@RequestBody AlarmReceipt alarmReceipt) {
        return alarmReceiptService.insert(alarmReceipt);
    }

    @PutMapping("/updateAlarm")
    @Secured("ROLE_LEVEL2")
    public int updateAlarm(@RequestBody AlarmReceipt alarmReceipt) {
        return alarmReceiptService.update(alarmReceipt);
    }

    @DeleteMapping("/deleteAlarm")
    @Secured("ROLE_LEVEL2")
    public int updateAlarm(@RequestParam String clueId) {
        return alarmReceiptService.delete(clueId);
    }

    @PostMapping("/upload")
    @Secured("ROLE_LEVEL2")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return alarmReceiptService.dealUpload(file);
    }

    @GetMapping("/downloadTemplate")
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        Resource excelFile = new ClassPathResource("/templates/alarmReceiptTemplate.xlsx");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getFilename() + "\"");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    @GetMapping("exportExcel")
    public void exportExcel(@RequestParam("dateStart")  String dateStart,
                            @RequestParam("dateEnd") String dateEnd,
                            HttpServletResponse response) throws IOException {

        try {
            LocalDate date = LocalDate.now().minusDays(1);
            if (StringUtils.isEmpty(dateStart)&&StringUtils.isEmpty(dateEnd)){
                date = LocalDate.now().minusDays(1);
            }else if (!StringUtils.isEmpty(dateStart)){
                date = LocalDate.parse(dateStart, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }else if (!StringUtils.isEmpty(dateEnd)){
                date = LocalDate.parse(dateEnd,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            Workbook workbook = generateExcelWorkbook(date);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("72时电诈类刑事警情统计表（"+date+"）", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
            workbook.close();
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            log.error("下载文件失败",e);
            response.getWriter().println(JSON.toJSONString(map));
        }
    }


    private Workbook generateExcelWorkbook(LocalDate date) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //导出线索表
        String caseExcelFileName="72时电诈类刑事警情统计表（"+date+"）.xlsx";
        String fullCaseExcelFileName = ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" + caseExcelFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" );
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        //获取数据
        Map<Integer,List<AlarmReceipt>> map = new HashMap<>();
        for (int i=0;i<=2;i++){
            LocalDate currentDate = date.minusDays(i);
            Date date1 = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date eDate = DateUtil.formatDateToEnd(date1);
            Date sDate = DateUtil.formatDateToStart(date1);
            AlarmReceipt alarmReceipt = new AlarmReceipt();
            alarmReceipt.setAlarmTimeEnd(eDate);
            alarmReceipt.setAlarmTimeStart(sDate);
            List<AlarmReceipt> alarmReceiptList = alarmReceiptService.selectAll(alarmReceipt);
            map.put(i,alarmReceiptList);
        }
        InputStream inputStream =  this.getClass().getClassLoader().getResourceAsStream("templates/alarmReceiptTemplate.xlsx");
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("tempTemplate", ".xlsx");
            // 将资源复制到临时文件
            copyInputStreamToFile(inputStream, tempFile);
            // 使用 FileInputStream 包装临时文件
            FileInputStream fis = new FileInputStream(tempFile);
            Workbook workbook = new XSSFWorkbook(fis);
            // Create a CellStyle for border and text wrapping
            CellStyle style = workbook.createCellStyle();
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setWrapText(true);

            DecimalFormat decimalFormat = new DecimalFormat("#.##");
            for (int i=0;i<3;i++){
                Sheet sheet = workbook.getSheetAt(i);
                Row row = sheet.getRow(0);
                Cell cell = row.getCell(0);
                cell.setCellValue((24+24*i)+"小时柳州市电诈接处警警情信息列表("+date.minusDays(i)+")");// Modify the cell value
                int r = 2;
                for(AlarmReceipt alarmReceipt:map.get(i)){
                    int columnIndex=0;
                    row = sheet.createRow(r);
                    for(int j=0;j<18;j++){
                        row.createCell(j).setCellStyle(style);
                    }
                    row.getCell(columnIndex++).setCellValue(r-1);
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getDepartment());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getPcs());
                    row.getCell(columnIndex++).setCellValue(simpleDateFormat.format(alarmReceipt.getAlarmTime()));
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getContent());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getType());
                    row.getCell(columnIndex++).setCellValue(decimalFormat.format(alarmReceipt.getLossMoney()));
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getStopPayment());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getOneLevel());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getSecondLevel());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getWithdraw());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getDrainage());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getCapture());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getRecover());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getJointCase());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getOutside());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getApp());
                    row.getCell(columnIndex++).setCellValue(alarmReceipt.getAdvocate());
                    r++;
                }
            }
            // 设置计算模式为自动计算
            workbook.setForceFormulaRecalculation(true);
            // Save the modified workbook to a new file
            try (FileOutputStream fos = new FileOutputStream(fullCaseExcelFileName)) {
                workbook.write(fos);
            }
            log.info("Excel template modified successfully.");
            FileInputStream fisResult = new FileInputStream(fullCaseExcelFileName);
            Workbook workbookResult = new XSSFWorkbook(fisResult);
            return workbookResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
