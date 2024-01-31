package com.wa.cluemrg.controller;

import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.Effect;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.EffectService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.commons.io.FileUtils.copyInputStreamToFile;

@Log4j2
@RestController
@RequestMapping("/effect")
@Secured("ROLE_LEVEL1")
public class EffectController {

    @Autowired
    EffectService effectService;
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");
    private final ResourceLoader resourceLoader;

    @Autowired
    public EffectController(ResourceLoader resourceLoader) {
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

    @PostMapping("/getEffectList")
    public JsGridVO getEffectList(@RequestBody PageBO<Effect> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="DATE";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<Effect> list = effectService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<Effect> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    /*@GetMapping("/getLatestDay")
    public String getLatestDay() {
        return effectService.getLatestDay();
    }*/

    @PostMapping("/insertEffect")
    public int insertEffect(@RequestBody Effect effect) {
        return effectService.insert(effect);
    }

    @PutMapping("/updateEffect")
    public int updateEffect(@RequestBody Effect effect) {
        return effectService.update(effect);
    }

    @DeleteMapping("/deleteEffect")
    public int updateEffect(@RequestParam int seq) {
        return effectService.delete(seq);
    }


    @GetMapping("/downloadTemplate")
    public ResponseEntity<Resource> downloadExcel() throws IOException {
        Resource excelFile = new ClassPathResource("/templates/effectTemplate.xlsx");

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
                date = LocalDate.parse(dateStart,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }else if (!StringUtils.isEmpty(dateEnd)){
                date = LocalDate.parse(dateEnd,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            Workbook workbook = generateExcelWorkbook(date);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("72时打击情况统计表（"+date+"）", "UTF-8").replaceAll("\\+", "%20");
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
        //导出线索表
        String caseExcelFileName="72时打击情况统计表（"+date+"）.xlsx";
        String fullCaseExcelFileName = ROOT_APPLICATION_PATH+separator+"effect"+separator+formatYMD.format(new Date())+ separator + caseExcelFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+separator+"effect"+separator+formatYMD.format(new Date())+ separator );
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        //获取数据
        Effect effect = new Effect();
        LocalDate endDate = date.minusDays(2);
        // Convert LocalDate to Date using the system default time zone
        Date eDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date sDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        effect.setDateEnd(eDate);
        effect.setDateStart(sDate);
        List<Effect> effectList = effectService.selectAll(effect);
        InputStream inputStream =  this.getClass().getClassLoader().getResourceAsStream("templates/effectTemplate.xlsx");
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("tempTemplate", ".xlsx");
            // 将资源复制到临时文件
            copyInputStreamToFile(inputStream, tempFile);
            // 使用 FileInputStream 包装临时文件
            FileInputStream fis = new FileInputStream(tempFile);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(2); // Assuming you want to modify the second row
            Cell cell1 = row.getCell(1); // Assuming you want to modify the second cell in that row
            cell1.setCellValue(date);// Modify the cell value
            Cell cell2 = row.getCell(3);
            cell2.setCellValue(date.minusDays(1));
            Cell cell3 = row.getCell(5);
            cell3.setCellValue(date.minusDays(2));
            Map<String,List<Integer>> cellIndexMap = new HashMap<>();
            //填充指标
            for (Effect item:effectList) {
                String department = item.getDepartment();
                List<Integer> indexList = cellIndexMap.get(department);
                if (indexList == null) {
                    indexList = new ArrayList<>();
                }
                indexList.add(item.getDetention());
                indexList.add(item.getSue());
                cellIndexMap.put(department, indexList);
            }
            for (Map.Entry<String,List<Integer>> entry:cellIndexMap.entrySet()){
                int rowIndex=0;
                if (entry.getKey().contains("城中")){
                    rowIndex=4;
                }else if (entry.getKey().contains("鱼峰")){
                    rowIndex=5;
                }else if (entry.getKey().contains("柳南")){
                    rowIndex=6;
                }else if (entry.getKey().contains("柳北")){
                    rowIndex=7;
                }else if (entry.getKey().contains("柳江")){
                    rowIndex=8;
                }else if (entry.getKey().contains("柳东")){
                    rowIndex=9;
                }else if (entry.getKey().contains("柳城")){
                    rowIndex=10;
                }else if (entry.getKey().contains("鹿寨")){
                    rowIndex=11;
                }else if (entry.getKey().contains("融安")){
                    rowIndex=12;
                }else if (entry.getKey().contains("融水")){
                    rowIndex=13;
                }else if (entry.getKey().contains("三江")){
                    rowIndex=14;
                }
                row = sheet.getRow(rowIndex);
                List<Integer> indexList = entry.getValue();
                for (int i=1;i<=indexList.size();i++){
                    row.getCell(i).setCellValue(indexList.get(i-1));
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
