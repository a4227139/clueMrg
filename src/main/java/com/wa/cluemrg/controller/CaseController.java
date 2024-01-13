package com.wa.cluemrg.controller;


import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.util.PoitlIOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.*;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.AlarmReceiptService;
import com.wa.cluemrg.service.CaseService;
import com.wa.cluemrg.service.EffectService;
import com.wa.cluemrg.service.VictimService;
import com.wa.cluemrg.util.DateUtil;
import com.wa.cluemrg.util.JurisdictionUtil;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.wa.cluemrg.entity.ImportantAlarmReceiptIndex;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/case")
@Secured("ROLE_LEVEL1")
public class CaseController {

    @Autowired
    CaseService caseService;
    @Autowired
    AlarmReceiptService alarmReceiptService;
    @Autowired
    VictimService victimService;
    @Autowired
    EffectService effectService;
    @Value("${base.host}")
    String host;
    @Value("${base.port}")
    String port;
    @Value("${base.jzurl}")
    String jzurl;
    @Value("${base.jzsolveurl}")
    String jzsolveurl;

    private final ResourceLoader resourceLoader;
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyyMMdd");

    @Autowired
    public CaseController(ResourceLoader resourceLoader) {
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
     * @return com.response.Response<com.wa.cluemrg.entity.Case>
     */
    @PostMapping("/getCaseList")
    public JsGridVO getCaseList(@RequestBody PageBO<Case> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="SEQ";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="ASC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<Case> list = caseService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<Case> vo = new JsGridVO(page);
        list=page.getList();
        int seq=(page.getPageNum()-1)*page.getPageSize()+1;
        for (Case caseObj:list){
            caseObj.setSeq(seq+"");
            seq++;
        }
        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("exportExcel")
    public void exportExcel(@RequestParam("dateStart")  String dateStart,
                            @RequestParam("dateEnd") String dateEnd,
                            HttpServletResponse response) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isEmpty(dateStart)){
                dateStart="2024-01-01";
            }
            if (StringUtils.isEmpty(dateEnd)) {
                dateEnd = dateFormat.format(new Date());
            }
            Workbook workbook = generateExcelWorkbook(dateStart,dateEnd);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("2023年全市电诈案件情况统计表", "UTF-8").replaceAll("\\+", "%20");
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
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    private Workbook generateExcelWorkbook(String dateStart,String dateEnd) throws ParseException {
        //dateStart="2024-01-01";
        //dateEnd="2024-01-11";
        //导出线索表
        String caseExcelFileName="2024年全市电诈案件情况统计表"+"（"+dateStart+"到"+dateEnd+"）.xlsx";
        String fullCaseExcelFileName = ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" + caseExcelFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" );
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        //获取指标
        List<CaseIndex> caseIndexList = getCaseIndex(dateStart,dateEnd,"");
        InputStream inputStream =  this.getClass().getClassLoader().getResourceAsStream("templates/caseTemplate.xlsx");
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("tempTemplate", ".xlsx");
            // 将资源复制到临时文件
            copyInputStreamToFile(inputStream, tempFile);
            // 使用 FileInputStream 包装临时文件
            FileInputStream fis = new FileInputStream(tempFile);
            Workbook workbook = new XSSFWorkbook(fis);

            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0); // Assuming you want to modify the second row
            Cell cell = row.getCell(0); // Assuming you want to modify the second cell in that row
            // Modify the cell value
            cell.setCellValue(caseExcelFileName.replace(".xlsx",""));
            //填充指标
            for (int i=1;i<caseIndexList.size();i++){
                CaseIndex caseIndex = caseIndexList.get(i);
                row = sheet.getRow(i+2);
                row.getCell(1).setCellValue(caseIndex.getCount());
                row.getCell(2).setCellValue(caseIndex.getCountHistory());
                //row.getCell(3).setCellValue(caseIndex.getYCountRatio());
               // row.getCell(4).setCellValue(caseIndex.getCountPerWFormat());
                try {
                    row.getCell(5).setCellValue(Integer.parseInt(caseIndex.getLossMoneyFormat()));
                    row.getCell(6).setCellValue(Integer.parseInt(caseIndex.getLossMoneyHistoryFormat()));
                }catch (Exception e){
                    log.info("损失金额转换失败",e);
                    row.getCell(5).setCellValue(caseIndex.getLossMoneyFormat());
                    row.getCell(6).setCellValue(caseIndex.getLossMoneyHistoryFormat());
                }
                //row.getCell(7).setCellValue(caseIndex.getYLossMoneyRatio());
                //row.getCell(8).setCellValue(caseIndex.getAverageLossMoneyFormat());
                row.getCell(9).setCellValue(caseIndex.getSolveCount());
                //row.getCell(10).setCellValue(caseIndex.getSolveRateFormat());
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

    private static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        }
    }

    @GetMapping("/exportWord")
    public void exportWord(@RequestParam("dateStart") String dateStart,
                           @RequestParam("dateEnd") String dateEnd,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException{
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(dateEnd+"柳州市公安局每日电诈警情研判报告", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".docx");
            if (StringUtils.isEmpty(dateStart)){
                dateStart="2023-01-01";
            }
            if (StringUtils.isEmpty(dateEnd)) {
                dateEnd = dateFormat.format(new Date());
            }
            //获取指标
            List<CaseIndex> caseIndexList = getCaseIndex(dateStart,dateEnd,"");
            //填充文档
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("list", caseIndexList);

            // 创建一个新列表存储排序后的指标
            List<CaseIndex> caseIndexListSortBySolveRate = new ArrayList<>();
            caseIndexListSortBySolveRate.addAll(caseIndexList);
            caseIndexListSortBySolveRate.remove(0);
            // 创建一个自定义的Comparator，按照破案率升序排序
            Comparator<CaseIndex> solveRateComparator = Comparator.comparingDouble(CaseIndex::getSolveRate);
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortBySolveRate, solveRateComparator);
            dataMap.put("caseIndexListSortBySolveRate", caseIndexListSortBySolveRate);

            // 创建一个新列表存储排序后的指标
            List<CaseIndex> caseIndexListSortByCountPerW = new ArrayList<>();
            caseIndexListSortByCountPerW.addAll(caseIndexList);
            caseIndexListSortByCountPerW.remove(0);
            // 创建一个自定义的Comparator，按照破案序排序
            Comparator<CaseIndex> countPerWComparator = Comparator.comparingDouble(CaseIndex::getCountPerW).reversed();
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortByCountPerW, countPerWComparator);
            dataMap.put("caseIndexListSortByCountPerW", caseIndexListSortByCountPerW);

            // 创建一个新列表存储排序后的指标
            List<CaseIndex> caseIndexListSortByAverageLossMoney = new ArrayList<>();
            caseIndexListSortByAverageLossMoney.addAll(caseIndexList);
            caseIndexListSortByAverageLossMoney.remove(0);
            // 创建一个自定义的Comparator，按照破案序排序
            Comparator<CaseIndex> averageLossMoneyComparator = Comparator.comparingDouble(CaseIndex::getAverageLossMoney).reversed();
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortByAverageLossMoney, averageLossMoneyComparator);
            dataMap.put("caseIndexListSortByAverageLossMoney", caseIndexListSortByAverageLossMoney);

            //获取警情指标
            List<AlarmReceiptIndex> alarmReceiptIndexList = getAlarmReceiptIndex(dateStart,dateEnd,"");
            dataMap.put("alarmReceiptIndexList", alarmReceiptIndexList);

            //获取受害人指标
            VictimIndex victimIndex = getVictimIndex("",dateEnd,"");
            dataMap.put("victimIndex", victimIndex);

            //获取战果指标
            List<EffectIndex> effectIndexList = getEffectIndex(dateStart,dateEnd,"");
            dataMap.put("effectIndexList", effectIndexList);
            dataMap.put("allEffectSituation", allEffectSituation);
            //获取重大警情
            List<ImportantAlarmReceiptIndex> importAlarmReceiptIndixList = getImportantAlarmReceiptIndex(dateStart,dateEnd,"");
            dataMap.put("importAlarmReceiptIndixList", importAlarmReceiptIndixList);

            OutputStream out = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            Configure config = Configure.builder().useSpringEL().build();
            XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateRYP.docx"),config).render(dataMap);
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

    private List<ImportantAlarmReceiptIndex> getImportantAlarmReceiptIndex(String dateStart, String dateEnd, String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(dateEnd)||
                dateEnd.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {//如果是今天也要变昨天
            dateEnd = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        Date date = simpleDateFormat.parse(dateEnd);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(DateUtil.formatDateToStart(date));
        alarmReceipt.setAlarmTimeEnd(DateUtil.formatDateToEnd(date));
        List<AlarmReceipt> list = alarmReceiptService.getImportAlarmReceipt(alarmReceipt);
        Map<String,List<AlarmReceipt>> map = new HashMap<>();
        for (AlarmReceipt item : list){
            if (map.get(item.getDepartment())==null){
                List<AlarmReceipt> list1 = new ArrayList<>();
                item.setStopPayment("案件进展："+item.getStopPayment());
                list1.add(item);
                map.put(item.getDepartment(),list1);
            }else {
                item.setStopPayment("案件进展："+item.getStopPayment());
                map.get(item.getDepartment()).add(item);
            }
        }
        List<ImportantAlarmReceiptIndex> importantAlarmReceiptIndexList = new ArrayList<>();
        for (Map.Entry<String,List<AlarmReceipt>> entry : map.entrySet()){
            ImportantAlarmReceiptIndex importantAlarmReceiptIndex = new ImportantAlarmReceiptIndex();
            importantAlarmReceiptIndex.setDepartment(entry.getKey());
            importantAlarmReceiptIndex.setList(entry.getValue());
            importantAlarmReceiptIndexList.add(importantAlarmReceiptIndex);
        }
        return importantAlarmReceiptIndexList;
    }

    @GetMapping("/getCaseIndex")
    public List<CaseIndex> getCaseIndex(@RequestParam("dateStart")  String dateStart,
                                        @RequestParam("dateEnd") String dateEnd,
                                        @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        Case param = new Case();
        if (StringUtils.isEmpty(dateStart)){
            dateStart="2024-01-01";
        }
        param.setRegisterDateStart(dateFormat.parse(dateStart));
        if (StringUtils.isEmpty(dateEnd)) {
            dateEnd = dateFormat.format(new Date());
        }
        param.setRegisterDateEnd(dateFormat.parse(dateEnd));
        List<Case> list = caseService.selectAll(param);

        Case paramSolve = new Case();
        paramSolve.setSolveDateStart(dateFormat.parse(dateStart));
        paramSolve.setSolveDateEnd(dateFormat.parse(dateEnd));
        List<Case> listSolve = caseService.selectAllSolve(paramSolve);

        //历史案件不准确
        Case paramHistory = new Case();
        String lastYearDateStart=(Integer.parseInt(dateStart.substring(0,4))-1)+dateStart.substring(4);
        String lastYearDateEnd=(Integer.parseInt(dateEnd.substring(0,4))-1)+dateEnd.substring(4);
        paramHistory.setRegisterDateStart(dateFormat.parse(lastYearDateStart));
        paramHistory.setRegisterDateEnd(dateFormat.parse(lastYearDateEnd));
        List<Case> listHistory = caseService.selectAllHistory(paramHistory);

        List<CaseIndex> caseIndexList = dealCaseIndex(list,listSolve,listHistory,dateStart,dateEnd);
        return caseIndexList;
    }

    @GetMapping("/getVictimIndex")
    public VictimIndex getVictimIndex(@RequestParam("dateStart")  String dateStart,
                                                        @RequestParam("dateEnd") String dateEnd,
                                                        @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        Date start,end;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtils.isEmpty(dateStart)){
            start = dateFormat.parse(dateStart);
        }else {
            start = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        if (StringUtils.isEmpty(dateEnd)||
                dateEnd.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {//如果是今天也要变昨天
            end = Date.from(LocalDate.now().minusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        }else {
            end = dateFormat.parse(dateEnd);
        }
        Victim param = new Victim();
        param.setTimeStart(DateUtil.formatDateToStart(start));
        param.setTimeEnd(DateUtil.formatDateToEnd(end));
        List<Victim> list = victimService.selectAll(param);
        VictimIndex index = dealVictimIndex(list);
        index.setDateChinese(dateFormat.format(end).substring(5,10));
        return index;
    }

    private VictimIndex dealVictimIndex(List<Victim> list) {
        Map<String,Integer> employerMap = new HashMap<>();
        Map<String,Integer> genderMap = new HashMap<>();
        Map<String,Integer> typeMap = new HashMap<>();
        Map<String,Integer> ageMap = new HashMap<String,Integer>(){{put("不详",0);put("18岁以下",0);put("18岁-30岁",0);put("30岁-45岁",0);put("45岁-60岁",0);put("60岁以上",0);}};
        for (Victim victim:list){
            //职业
            if (employerMap.get(victim.getEmployer())==null){
                employerMap.put(victim.getEmployer(),1);
            }else {
                employerMap.put(victim.getEmployer(),employerMap.get(victim.getEmployer())+1);
            }
            //性别
            if (genderMap.get(victim.getGender())==null){
                genderMap.put(victim.getGender(),1);
            }else {
                genderMap.put(victim.getGender(),genderMap.get(victim.getGender())+1);
            }
            //类型
            if (typeMap.get(victim.getType())==null){
                typeMap.put(victim.getType(),1);
            }else {
                typeMap.put(victim.getType(),typeMap.get(victim.getType())+1);
            }
            //年龄
            int age = victim.getAge();
            if (age<=0){
                ageMap.put("不详",ageMap.get("不详")+1);
            }else if (age<18){
                ageMap.put("18岁以下",ageMap.get("18岁以下")+1);
            }else if (age>=18&&age<30){
                ageMap.put("18岁-30岁",ageMap.get("18岁-30岁")+1);
            }else if (age>=30&&age<45){
                ageMap.put("30岁-45岁",ageMap.get("30岁-45岁")+1);
            }else if (age>=45&&age<60){
                ageMap.put("45岁-60岁",ageMap.get("45岁-60岁")+1);
            }else {
                ageMap.put("60岁以上",ageMap.get("60岁以上")+1);
            }
        }
        VictimIndex victimIndex = new VictimIndex();
        int size = list.size();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        //职业
        StringBuilder employerSituationBuilder=new StringBuilder();//"单位人员x人（x），其余为自谋职业或无业。";
        StringBuilder employerListBuilder=new StringBuilder();
        int employeeCount=0;
        for (Map.Entry<String,Integer> entry : employerMap.entrySet()){
            String employer = entry.getKey();
            if (StringUtils.isEmpty(employer)||employer.equals("无业")||employer.equals("无")||employer.equals("个体户")
                    ||employer.equals("自由职业")||employer.equals("待业")||employer.equals("不详")||employer.equals("不愿透露")){
                continue;
            }
            employeeCount++;
            employerListBuilder.append(entry.getKey()).append(entry.getValue()).append("人,");
        }
        if (employeeCount>0){
            employerSituationBuilder.append("单位人员").append(employeeCount).append("人（")
                    .append(employerListBuilder.subSequence(0,employerListBuilder.length()-1)).append("），其余为自谋职业或无业。");
        }else {
            employerSituationBuilder.append("均为为自谋职业或无业。");
        }
        victimIndex.setEmployerSituation(employerSituationBuilder.toString());
        //性别
        int male=genderMap.get("男")==null?0:genderMap.get("男");
        int female = genderMap.get("女")==null?0:genderMap.get("女");
        float maleRatio = (float) male/size*100;
        float femaleRatio = (float) female/size*100;
        String genderSituation ;
        if (female>=male){
            genderSituation = "女性"+female+"人（占比"+decimalFormat.format(femaleRatio)+"%），"+"男性"+male+"人（占比"+decimalFormat.format(maleRatio)+"%）。";
        }else {
            genderSituation = "男性"+male+"人（占比"+decimalFormat.format(maleRatio)+"%），"+"女性"+female+"人（占比"+decimalFormat.format(femaleRatio)+"%）。";
        }
        victimIndex.setGenderSituation(genderSituation);
        //年龄
        int max=-9999;
        String maxString="";
        for (Map.Entry<String,Integer> entry : ageMap.entrySet()){
            if (entry.getValue()>max){
                maxString=entry.getKey();
                max=entry.getValue();
            }
        }
        float ageRatio = (float) max/size*100;
        String ageSituation="主要年龄段为"+maxString+"，共"+max+"人（占比"+decimalFormat.format(ageRatio)+"%）";
        int second=-9999;
        String secondString="";
        ageMap.remove(maxString);
        for (Map.Entry<String,Integer> entry : ageMap.entrySet()){
            if (entry.getValue()>max){
                secondString=entry.getKey();
                second=entry.getValue();
            }
        }
        //两个相同
        if (second==max){
            ageSituation+=","+secondString+"，共"+second+"人（占比"+decimalFormat.format(ageRatio)+"%）。";
        }else {
            ageSituation+="。";
        }
        victimIndex.setAgeSituation(ageSituation);
        //类型
        max=-9999;
        maxString="";
        for (Map.Entry<String,Integer> entry : typeMap.entrySet()){
            if (entry.getValue()>max){
                maxString=entry.getKey();
                max=entry.getValue();
            }
        }
        float typeRatio = (float) max/size*100;
        String typeMapSituation="主要类型为"+maxString+"，共"+max+"起（占比"+decimalFormat.format(typeRatio)+"%）";
        second=-9999;
        secondString="";
        typeMap.remove(maxString);
        for (Map.Entry<String,Integer> entry : ageMap.entrySet()){
            if (entry.getValue()>max){
                secondString=entry.getKey();
                second=entry.getValue();
            }
        }
        //两个相同
        if (second==max){
            typeMapSituation+=","+secondString+"，共"+second+"起（占比"+decimalFormat.format(typeRatio)+"%）。";
        }else {
            typeMapSituation+="。";
        }
        victimIndex.setTypeSituation(typeMapSituation);
        return victimIndex;
    }

    public static String allEffectSituation="72小时，全市无新增战果。";
    @GetMapping("/getEffectIndex")
    public List<EffectIndex> getEffectIndex(@RequestParam("dateStart")  String dateStart,
                                      @RequestParam("dateEnd") String dateEnd,
                                      @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        if (StringUtils.isEmpty(dateEnd)||
                dateEnd.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {//如果是今天也要变昨天
            dateEnd = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        LocalDate currDate = LocalDate.parse(dateEnd);
        LocalDate dateEnd1=currDate.minusDays(1);
        LocalDate dateEnd2=currDate.minusDays(2);
        //获取数据
        Effect effect = new Effect();
        LocalDate endDate = currDate.minusDays(2);
        // Convert LocalDate to Date using the system default time zone
        Date sDate = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date eDate = Date.from(currDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        effect.setDateStart(sDate);
        effect.setDateEnd(eDate);
        List<Effect> effectList = effectService.selectAll(effect);
        Map<LocalDate,EffectIndex> map = new TreeMap<LocalDate,EffectIndex>(){{
            put(currDate,new EffectIndex());
            put(dateEnd1,new EffectIndex());
            put(dateEnd2,new EffectIndex());
        }};
        int detentionCount=0,sueCount=0;
        for (Effect item:effectList){
            EffectIndex effectIndex = map.get(item.getDate());
            effectIndex.setDateChinese(item.getDate().format(DateTimeFormatter.ofPattern("MM月dd日")));
            if (item.getDetention()>0){
                detentionCount+=item.getDetention();
                effectIndex.setDetention(effectIndex.getDetention()+item.getDetention());
                effectIndex.getDetentionMap().put(item.getDepartment(),item.getDetention());
            }
            if (item.getSue()>0){
                sueCount+=item.getSue();
                effectIndex.setSue(effectIndex.getSue()+item.getSue());
                effectIndex.getSueMap().put(item.getDepartment(),item.getSue());
            }
        }
        List<EffectIndex> indexList = new ArrayList<>();
        indexList.addAll(map.values());
        for (EffectIndex effectIndex:indexList){
            String situation="全市新增";
            String detentionSituation ="";
            if (effectIndex.getDetention()>0){
                detentionSituation=effectIndex.getDetention()+"个刑拘数（";
                Map<String,Integer> detentionMap=effectIndex.getDetentionMap();
                for (Map.Entry<String,Integer> entry:detentionMap.entrySet()){
                    detentionSituation+=entry.getKey()+entry.getValue()+"人，";
                }
                detentionSituation=detentionSituation.substring(0,detentionSituation.length()-1);
                detentionSituation+="）。";
            }

            String sueSituation ="";
            if (effectIndex.getSue()>0){
                sueSituation=effectIndex.getSue()+"个起诉数（";
                Map<String,Integer> sueMap=effectIndex.getSueMap();
                for (Map.Entry<String,Integer> entry:sueMap.entrySet()){
                    sueSituation+=entry.getKey()+entry.getValue()+"人，";
                }
                sueSituation=sueSituation.substring(0,sueSituation.length()-1);
                sueSituation+="）。";
            }
            situation=situation+detentionSituation+sueSituation;
            if (effectIndex.getDetention()<=0&&effectIndex.getSue()<=0){
                situation="全市无新增打击战果。";
            }
            effectIndex.setSituation(situation);
        }

        if (detentionCount>0||sueCount>0){
            allEffectSituation="72小时，全市新增";
        }
        if (detentionCount>0){
            allEffectSituation+="刑拘战果"+detentionCount+"人，";
        }
        if (sueCount>0){
            allEffectSituation+="起诉战果"+sueCount+"人。";
        }
        //结尾的，换成。
        if (allEffectSituation.endsWith("，")){
            allEffectSituation=allEffectSituation.substring(0,allEffectSituation.length()-1)+"。";
        }
        return indexList;
    }

    @GetMapping("/getAlarmReceiptIndex")
    public List<AlarmReceiptIndex> getAlarmReceiptIndex(@RequestParam("dateStart")  String dateStart,
                                        @RequestParam("dateEnd") String dateEnd,
                                        @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        if (StringUtils.isEmpty(dateEnd)||
                dateEnd.equals(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))) {//如果是今天也要变昨天
            dateEnd = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        LocalDate currDate = LocalDate.parse(dateEnd);
        LocalDate dateEnd1=currDate.minusDays(1);
        LocalDate dateEnd2=currDate.minusDays(2);
        LocalDate dateEnd3=currDate.minusDays(3);

        AlarmReceipt param = new AlarmReceipt();
        param.setAlarmTimeStart(dateFormat.parse(dateEnd3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        param.setAlarmTimeEnd(dateFormat.parse(dateEnd));
        List<AlarmReceipt> list = alarmReceiptService.selectAll(param);
        List<AlarmReceiptIndex> listIndex = alarmReceiptService.getAlarmReceiptIndex(param);
        List<AlarmReceiptIndex> alarmReceiptIndexList = dealAlarmReceiptIndex(list,listIndex,dateEnd3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),dateEnd);
        return alarmReceiptIndexList;
    }

    private List<AlarmReceiptIndex> dealAlarmReceiptIndex(List<AlarmReceipt> list,List<AlarmReceiptIndex> listIndex, String dateStart, String dateEnd) throws ParseException {
        Case param = new Case();

        param.setRegisterDateStart(dateFormat.parse(dateStart));
        param.setRegisterDateEnd(dateFormat.parse(dateEnd));
        List<Case> listRegister = caseService.selectAll(param);

        Case paramSolve = new Case();
        paramSolve.setSolveDateStart(dateFormat.parse(dateStart));
        paramSolve.setSolveDateEnd(dateFormat.parse(dateEnd));
        List<Case> listSolve = caseService.selectAllSolve(paramSolve);

        LocalDate currDate = LocalDate.parse(dateEnd);
        LocalDate dateEnd1=currDate.minusDays(1);
        LocalDate dateEnd2=currDate.minusDays(2);
        LocalDate dateEnd3=currDate.minusDays(3);

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (int i=0;i<listIndex.size();i++){
            listIndex.get(i).setLossMoneyFormat(decimalFormat.format(listIndex.get(i).getLossMoney()));
        }
        for (Case caseObj:listRegister){
            if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(0).setRegisterCount(listIndex.get(0).getCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(1).setRegisterCount(listIndex.get(1).getCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(2).setRegisterCount(listIndex.get(2).getCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd)){
                listIndex.get(3).setRegisterCount(listIndex.get(3).getCount()+1);
                continue;
            }
        }

        for (Case caseObj:listSolve){
            if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd3.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(0).setSolveCount(listIndex.get(0).getSolveCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd2.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(1).setSolveCount(listIndex.get(1).getSolveCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                listIndex.get(2).setSolveCount(listIndex.get(2).getSolveCount()+1);
                continue;
            }else if (dateFormat.format(caseObj.getRegisterDate()).contains(dateEnd)){
                listIndex.get(3).setSolveCount(listIndex.get(3).getSolveCount()+1);
                continue;
            }
        }

        int baseCount;
        float baseLossMoney;

        for (int i=1;i<listIndex.size();i++){
            baseCount = listIndex.get(i-1).getCount();
            baseLossMoney = listIndex.get(i-1).getLossMoney();
            if (baseCount!=0){
                float countRate = (float) (listIndex.get(i).getCount()-baseCount)/baseCount*100;
                listIndex.get(i).setCountRate(countRate);
                if (countRate!=0){
                    listIndex.get(i).setCountRateFormat(decimalFormat.format(countRate)+"%");
                }else {
                    listIndex.get(i).setCountRateFormat("");
                }
            }
            if (baseLossMoney!=0){
                float lossMoneyRate = (listIndex.get(i).getLossMoney()-baseLossMoney)/baseLossMoney*100;
                listIndex.get(i).setLossMoneyRate(lossMoneyRate);
                if (lossMoneyRate!=0){
                    listIndex.get(i).setLossMoneyRateFormat(decimalFormat.format(lossMoneyRate)+"%");
                }else {
                    listIndex.get(i).setCountRateFormat("");
                }
            }
        }

        listIndex.get(0).setDateChinese(dateEnd3.format(DateTimeFormatter.ofPattern("MM月dd日")));
        listIndex.get(1).setDateChinese(dateEnd2.format(DateTimeFormatter.ofPattern("MM月dd日")));
        listIndex.get(2).setDateChinese(dateEnd1.format(DateTimeFormatter.ofPattern("MM月dd日")));
        listIndex.get(3).setDateChinese(dateEnd.substring(5,7)+"月"+dateEnd.substring(8,10)+"日");

        return listIndex;
    }

    public List<CaseIndex> dealCaseIndex(List<Case> caseList,List<Case> solveCaseList,List<Case> historyCaseList,String dateStart,String dateEnd) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] jurisdictionArray=new String[]{"市本级","城中分局","鱼峰分局","柳南分局","柳北分局","柳江分局","柳东分局","柳城县局","鹿寨县局","融安县局","融水县局","三江县局"};
        Map<String,Integer> jurisdictionMap = new HashMap<String,Integer>() {
            {
                put("市本级",0 );
                put("城中分局",1 );
                put("鱼峰分局",2 );
                put("柳南分局",3 );
                put("柳北分局",4 );
                put("柳江分局",5 );
                put("柳东分局",6 );
                put("柳城县局",7 );
                put("鹿寨县局",8 );
                put("融安县局",9 );
                put("融水县局",10);
                put("三江县局",11);
            }
        };
        Map<String,Integer> jurisdictionPopulationMap = new HashMap<String,Integer>() {
            {
                put("市本级",4157934);
                put("城中分局",243628);
                put("鱼峰分局",414952);
                put("柳南分局",617925);
                put("柳北分局",484765);
                put("柳江分局",503772);
                put("柳东分局",254009);
                put("柳城县局",314242);
                put("鹿寨县局",337298);
                put("融安县局",253360);
                put("融水县局",412445);
                put("三江县局",321538);
            }
        };
        Map<Integer,CaseIndex> caseIndexMap = new HashMap<>();
        for (int i=0;i<jurisdictionArray.length;i++){
            CaseIndex caseIndex = new CaseIndex();
            caseIndex.setJurisdiction(jurisdictionArray[i]);
            caseIndex.setStartDate(dateStart);
            caseIndex.setEndDate(dateEnd);
            caseIndex.setStartDateChinese(dateStart.substring(0,4)+"年"+dateStart.substring(5,7)+"月"+dateStart.substring(8,10)+"日");
            caseIndex.setEndDateChinese(dateEnd.substring(0,4)+"年"+dateEnd.substring(5,7)+"月"+dateEnd.substring(8,10)+"日");

            Date date1 = new Date();
            Date date2 = dateFormat.parse("2023-09-12");
            long differenceInMilliseconds = Math.abs(date1.getTime() - date2.getTime());
            // Convert the difference from milliseconds to days
            long differenceInDays = TimeUnit.DAYS.convert(differenceInMilliseconds, TimeUnit.MILLISECONDS);
            //2023-09-12是105期
            int seq = (int) (105+differenceInDays);
            caseIndex.setSeq(seq);
            caseIndexMap.put(i,caseIndex);
        }
        CaseIndex cityCaseIndex = caseIndexMap.get(0);
        for (Case caseObj:caseList){
            String jurisdiction = caseObj.getJurisdiction();
            CaseIndex caseIndex = caseIndexMap.get(jurisdictionMap.get(jurisdiction));
            //立案数
            caseIndex.setCount(caseIndex.getCount()+1);
            cityCaseIndex.setCount(cityCaseIndex.getCount()+1);
            //损失数
            caseIndex.setLossMoney(caseIndex.getLossMoney()+caseObj.getMoney());
            cityCaseIndex.setLossMoney(cityCaseIndex.getLossMoney()+caseObj.getMoney());
            //破案数
            /*if (caseObj.getSolveDate()!=null){
                caseIndex.setSolveCount(caseIndex.getSolveCount()+1);
                cityCaseIndex.setSolveCount(cityCaseIndex.getSolveCount()+1);
            }*/
        }
        for (Case caseObj:solveCaseList) {
            String jurisdiction = caseObj.getJurisdiction();
            CaseIndex caseIndex = caseIndexMap.get(jurisdictionMap.get(jurisdiction));
            //破案数
            caseIndex.setSolveCount(caseIndex.getSolveCount() + 1);
            cityCaseIndex.setSolveCount(cityCaseIndex.getSolveCount() + 1);
        }
        //历史案件不准
        for (Case caseObj:historyCaseList) {
            String jurisdiction = caseObj.getJurisdiction();
            CaseIndex caseIndex = caseIndexMap.get(jurisdictionMap.get(jurisdiction));
            //立案数
            caseIndex.setCountHistory(caseIndex.getCountHistory()+1);
            cityCaseIndex.setCountHistory(cityCaseIndex.getCountHistory()+1);
            //损失数
            caseIndex.setLossMoneyHistory(caseIndex.getLossMoneyHistory()+caseObj.getMoney());
            cityCaseIndex.setLossMoneyHistory(cityCaseIndex.getLossMoneyHistory()+caseObj.getMoney());
        }


        List<CaseIndex> caseIndexList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        DecimalFormat decimalFormat2 = new DecimalFormat("#.##");
        DecimalFormat decimalFormat3 = new DecimalFormat("#");
        String formattedResult;
        String formattedResult2;
        for (CaseIndex caseIndex :caseIndexMap.values()){
            String jurisdiction = caseIndex.getJurisdiction();
            //破案率
            float solveRate = (float)caseIndex.getSolveCount()/caseIndex.getCount()*100;
            formattedResult = decimalFormat.format(solveRate);
            caseIndex.setSolveRateFormat(formattedResult);
            caseIndex.setSolveRate(solveRate);
            //案均损失数
            float averageLossMoney = caseIndex.getLossMoney()/caseIndex.getCount()/10000;
            caseIndex.setAverageLossMoney(averageLossMoney);
            caseIndex.setAverageLossMoneyFormat(decimalFormat.format(averageLossMoney));
            //损失金额调格式(亿元)
            float lossMoney = caseIndex.getLossMoney();
            formattedResult = decimalFormat3.format(lossMoney/10000);
            formattedResult2 = decimalFormat2.format(lossMoney/100000000);
            caseIndex.setLossMoneyFormat(formattedResult);
            caseIndex.setLossMoneyFormat2(formattedResult2);
            //历史
            float lossMoneyHistory = caseIndex.getLossMoneyHistory();
            caseIndex.setLossMoneyHistory(lossMoneyHistory);
            //caseIndex.setLossMoneyHistoryFormat(decimalFormat.format(lossMoneyHistory));
            caseIndex.setLossMoneyHistoryFormat(decimalFormat3.format(lossMoneyHistory/10000));
            caseIndex.setLossMoneyHistoryFormat2(decimalFormat2.format(lossMoneyHistory/100000000));
            //caseIndex.setCountHistory(getCountHistory(jurisdiction,dateEnd));
            //float lossMoneyHistory = getLossMoneyHistory(jurisdiction,dateEnd);

            formattedResult2 = decimalFormat2.format(lossMoneyHistory/10000);
            caseIndex.setLossMoneyHistoryFormat2(formattedResult2);
            //万人发案数
            float countPerW = (float)caseIndex.getCount()/jurisdictionPopulationMap.get(jurisdiction);
            countPerW=countPerW*10000;
            caseIndex.setCountPerW(countPerW);
            caseIndex.setCountPerWFormat(decimalFormat.format(countPerW));
            //立案同比
            float yCountRatio=(float)(caseIndex.getCount()-caseIndex.getCountHistory())/caseIndex.getCountHistory()*100;
            caseIndex.setYCountRatio(decimalFormat.format(yCountRatio));
            //损失同比
            float yLossMoneyRatio=(caseIndex.getLossMoney()-caseIndex.getLossMoneyHistory()*10000)/(caseIndex.getLossMoneyHistory()*10000)*100;
            caseIndex.setYLossMoneyRatio(decimalFormat.format(yLossMoneyRatio));
            caseIndexList.add(caseIndex);
        }
        return caseIndexList;
    }

    Map<String,Integer> countHistory = new HashMap<String,Integer>(){{
        put("城中分局",	302 );
        put("鱼峰分局",	347 );
        put("柳南分局",	366 );
        put("柳北分局",	347 );
        put("柳江分局",	326 );
        put("柳东分局",	238 );
        put("柳城县局",	142 );
        put("鹿寨县局",	151 );
        put("融安县局",	104 );
        put("融水县局",	110 );
        put("三江县局",	128 );
        put("市本级",	2561);
        put("南宁",8958);
        put("柳州",2561);
        put("桂林",2332);
        put("梧州",900);
        put("北海",982);
        put("防城港",451);
        put("钦州",1359);
        put("贵港",1311);
        put("玉林",2907);
        put("百色",1660);
        put("贺州",671);
        put("河池",1676);
        put("来宾",1519);
        put("崇左",1020);
        put("全区",28307);
    }};
    Map<String,Float> countIncHistory = new HashMap<String,Float>(){{
        put("城中分局",	1.582565710f);
        put("鱼峰分局",	1.728038324f);
        put("柳南分局",	1.974900942f);
        put("柳北分局",	1.609015276f);
        put("柳江分局",	1.772120934f);
        put("柳东分局",	1.181413956f);
        put("柳城县局",	0.608340022f);
        put("鹿寨县局",	0.744996114f);
        put("融安县局",	0.515766541f);
        put("融水县局",	0.445234364f);
        put("三江县局",	0.652422632f);
        put("市本级",	12.81481481f);
        put("南宁",56.3f);
        put("柳州",12.8f);
        put("桂林",12.7f);
        put("梧州",5.3f);
        put("北海",4.4f);
        put("防城港",1.8f);
        put("钦州",10.9f);
        put("贵港",7.5f);
        put("玉林",18.6f);
        put("百色",9.3f);
        put("贺州",2.3f);
        put("河池",-1.9f);
        put("来宾",8.3f);
        put("崇左",5.7f);
        put("总计",154.0f);
    }};

    Map<String,Float> lossMoneyHistory = new HashMap<String,Float>(){{
        put("城中分局",	2943.289002f);
        put("鱼峰分局",	1625.016123f);
        put("柳南分局",	2263.263871f);
        put("柳北分局",	2165.154813f);
        put("柳江分局",	1402.192074f);
        put("柳东分局",	1283.396145f);
        put("柳城县局",	806.289446f);
        put("鹿寨县局",	624.510128f);
        put("融安县局",	568.487722f);
        put("融水县局",	805.174114f);
        put("三江县局",	556.405987f);
        put("市本级",	15043.179425f);
        put("南宁",71610.51f);
        put("柳州",15043.18f);
        put("桂林",15421.16f);
        put("梧州",3888.04f);
        put("北海",6855.66f);
        put("防城港",2860.07f);
        put("钦州",7185.68f);
        put("贵港",7610.42f);
        put("玉林",12731.51f);
        put("百色",8689.28f);
        put("贺州",2904.86f);
        put("河池",10337.16f);
        put("来宾",7429.48f);
        put("崇左",4709.05f);
        put("总计",177276.06f);
    }};
    Map<String,Float> lossMoneyIncHistory = new HashMap<String,Float>(){{
        put("城中分局",	9.52756274378982f);
        put("鱼峰分局",	5.68993547507037f);
        put("柳南分局",	9.86393716275945f);
        put("柳北分局",	8.33139106857801f);
        put("柳江分局",	6.59329502976163f);
        put("柳东分局",	4.69371727291672f);
        put("柳城县局",	2.16291832734297f);
        put("鹿寨县局",	2.35475914641386f);
        put("融安县局",	2.08100090304936f);
        put("融水县局",	1.87436785128135f);
        put("三江县局",	2.37563353755499f);
        put("市本级",	55.5394102592593f);
        put("南宁",423.71f);
        put("柳州",55.54f);
        put("桂林",77.97f);
        put("梧州",13.60f);
        put("北海",31.27f);
        put("防城港",12.84f);
        put("钦州",59.43f);
        put("贵港",48.10f);
        put("玉林",103.07f);
        put("百色",124.78f);
        put("贺州",14.23f);
        put("河池",38.34f);
        put("来宾",50.00f);
        put("崇左",28.62f);
        put("总计",1081.49f);
    }};

    LocalDate October1 = LocalDate.of(LocalDate.now().getYear(), 10, 1);
    Date October_1 = Date.from(October1.atStartOfDay(ZoneId.systemDefault()).toInstant());
    @SneakyThrows
    private int getCountHistory(String jurisdiction, String dateEnd) {
        Date date = dateFormat.parse(dateEnd);
        date = DateUtil.formatDateToStart(date);
        long interval = date.getTime()-October_1.getTime();
        interval = interval/(86400*1000)+1;
        int count = Math.round(countHistory.get(jurisdiction)+interval*countIncHistory.get(jurisdiction));
        return count;
    }

    @SneakyThrows
    private float getLossMoneyHistory(String jurisdiction, String dateEnd) {
        Date date = dateFormat.parse(dateEnd);
        date = DateUtil.formatDateToStart(date);
        long interval = date.getTime()-October_1.getTime();
        interval = interval/(86400*1000)+1;
        float loss = lossMoneyHistory.get(jurisdiction)+interval*lossMoneyIncHistory.get(jurisdiction);
        return loss;
    }

    @GetMapping("/sync")
    public String sync(@RequestParam("dateStart")  String dateStart,
                                @RequestParam("dateEnd") String dateEnd) throws ParseException {
        List<Case> list = syncCaseList(dateStart,dateEnd,jzurl);
        Case param = new Case();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (!StringUtils.isEmpty(dateStart)){
            param.setRegisterDateStart(dateFormat.parse(dateStart));
        }else {
            param.setRegisterDateStart(dateFormat.parse("2023-01-01"));
        }
        if (!StringUtils.isEmpty(dateEnd)){
            param.setRegisterDateEnd(dateFormat.parse(dateEnd));
        }else {
            param.setRegisterDateEnd(new Date());
        }
        List<Case> originList = caseService.selectAll(param);
        HashSet<String> syncSet = new HashSet();
        for (Case caseObj:list){
            caseObj.setCaseUnit(caseObj.getCaseUnit().replaceAll("\\s+",""));
            caseObj.setOrganiser(caseObj.getOrganiser().replaceAll("\\s+",""));
            syncSet.add(caseObj.getCaseNo());
        }
        List<Case> resultList = originList.stream()
                .filter(element -> !syncSet.contains(element.getCaseNo()))
                .collect(Collectors.toList());
        List<String> caseNoList = new ArrayList<>();
        for (Case c:resultList){
            caseNoList.add(c.getCaseNo());
        }
        if (!CollectionUtils.isEmpty(caseNoList)){
            log.info("删除案件:"+caseNoList);
            log.info("删除条数："+caseService.batchDelete(caseNoList));
        }

        List<Case> listSolve = syncCaseList(dateStart,dateEnd,jzsolveurl);
        Case paramSolve = new Case();
        if (!StringUtils.isEmpty(dateStart)){
            paramSolve.setSolveDateStart(dateFormat.parse(dateStart));
        }else {
            paramSolve.setSolveDateStart(dateFormat.parse("2023-01-01"));
        }
        if (!StringUtils.isEmpty(dateEnd)){
            paramSolve.setSolveDateEnd(dateFormat.parse(dateEnd));
        }else {
            paramSolve.setSolveDateEnd(new Date());
        }
        List<Case> originSolveList = caseService.selectAllSolve(param);
        HashSet<String> syncSolveSet = new HashSet();
        for (Case caseObj:list){
            caseObj.setCaseUnit(caseObj.getCaseUnit().replaceAll("\\s+",""));
            caseObj.setOrganiser(caseObj.getOrganiser().replaceAll("\\s+",""));
            syncSet.add(caseObj.getCaseNo());
        }
        List<Case> resultSolveList = originSolveList.stream()
                .filter(element -> !syncSolveSet.contains(element.getCaseNo()))
                .collect(Collectors.toList());
        List<String> caseNoSolveList = new ArrayList<>();
        for (Case c:resultSolveList){
            caseNoSolveList.add(c.getCaseNo());
        }
        if (!CollectionUtils.isEmpty(caseNoSolveList)){
            log.info("删除破案案件:"+caseNoSolveList);
            log.info("删除破案条数："+caseService.batchDeleteSolve(caseNoSolveList));
        }
        for (Case caseObj:listSolve){
            caseObj.setCaseUnit(caseObj.getCaseUnit().replaceAll("\\s+",""));
            caseObj.setOrganiser(caseObj.getOrganiser().replaceAll("\\s+",""));
        }
        int num = caseService.batchInsertOrUpdate(list);
        int numSolve = caseService.batchInsertOrUpdateSolve(listSolve);
        log.info("同步立案数据"+num+"条,破案数据"+numSolve+"条");
        return "同步成功";
    }

    @GetMapping("/getCaseLineIndex")
    public Map<String,List<SimpleIndex>> getCaseLineIndex(@RequestParam("dateStart")  String dateStart,
                                                          @RequestParam("dateEnd") String dateEnd,
                                                          @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认一个月
        if (StringUtils.isEmpty(dateStart)){
            LocalDate date = LocalDate.parse(dateEnd);
            LocalDate oneMonthAgo = date.minusMonths(1);
            dateStart = oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        Case caseObj = new Case();
        caseObj.setRegisterDateStart(start);
        caseObj.setRegisterDateEnd(end);
        Case solveCase = new Case();
        solveCase.setSolveDateStart(start);
        solveCase.setSolveDateEnd(end);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> caseIndexList = addZeroSimpleIndex(caseService.getCaseCountByDate(caseObj),dateStart,dateEnd);
        List<SimpleIndex> caseSolveIndexList = addZeroSimpleIndex(caseService.getCaseSolveCountByDate(solveCase),dateStart,dateEnd);
        List<SimpleIndex> alarmReceiptIndexList = addZeroSimpleIndex(alarmReceiptService.getAlarmReceiptCountByDate(alarmReceipt),dateStart,dateEnd);
        Map<String,List<SimpleIndex>> index = new HashMap<>();
        index.put("caseIndexList",caseIndexList);
        index.put("caseSolveIndexList",caseSolveIndexList);
        index.put("alarmReceiptIndexList",alarmReceiptIndexList);
        return index;
    }

    private List<SimpleIndex> addZeroSimpleIndex(List<SimpleIndex> list,String dateStart,String dateEnd){
        List<LocalDate> dateList = DateUtil.getDateRange(dateStart,dateEnd);
        List<LocalDate> zeroDateList=new ArrayList<>();
        List<SimpleIndex> zeroSimpleIndexList = new ArrayList<>();
        for (LocalDate date:dateList){
            boolean exist=false;
            for (SimpleIndex index:list){
                if (index.getDate().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                    exist=true;
                    break;
                }
            }
            if (!exist){
                zeroDateList.add(date);
            }
        }
        for (LocalDate date:zeroDateList){
            SimpleIndex simpleIndex = new SimpleIndex();
            simpleIndex.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            simpleIndex.setCount(0);
            list.add(simpleIndex);
        }
        // 创建一个自定义的Comparator，按照破案率升序排序
        // 使用Collections.sort()对列表进行排序
        Collections.sort(list, new StringFieldComparator());
        return list;
    }

    @GetMapping("/getAlarmReceiptTypeIndex")
    public List<SimpleIndex> getAlarmReceiptTypeIndex(@RequestParam("dateStart")  String dateStart,
                                                          @RequestParam("dateEnd") String dateEnd,
                                                          @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认全年
        if (StringUtils.isEmpty(dateStart)){
            dateStart="2023-01-01";
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> alarmReceiptIndexList = alarmReceiptService.getAlarmReceiptCountByType(alarmReceipt);
        return alarmReceiptIndexList;
    }

    @GetMapping("/getAlarmReceiptCommunityIndex")
    public List<SimpleIndex> getAlarmReceiptCommunityIndex(@RequestParam("dateStart")  String dateStart,
                                                      @RequestParam("dateEnd") String dateEnd,
                                                      @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认全年
        if (StringUtils.isEmpty(dateStart)){
            dateStart="2023-01-01";
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> alarmReceiptIndexList = alarmReceiptService.getAlarmReceiptCountByCommunity(alarmReceipt);
        return alarmReceiptIndexList;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return caseService.getLatestDay();
    }

    @PostMapping("/insertCase")
    public int insertCase(@RequestBody Case caseObj) {
        return caseService.insert(caseObj);
    }

    @PutMapping("/updateCase")
    public int updateCase(@RequestBody Case caseObj) {
        return caseService.update(caseObj);
    }

    @DeleteMapping("/deleteCase")
    public int updateCase(@RequestParam String clueId) {
        return caseService.delete(clueId);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public List<Case> syncCaseList(String dateStart,String dateEnd,String urlString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (StringUtils.isEmpty(dateStart)){
                dateStart="2023-01-01";
            }
            if (StringUtils.isEmpty(dateEnd)){
                dateEnd=dateFormat.format(new Date());
            }
            // 创建一个URL对象，表示要访问的URL
            //URL url = new URL("http://"+host+":"+port+"/case-simple.html?KSLASJ="+dateStart+"&JSLASJ="+dateEnd+"&LADW=4502");
            URL url = new URL(urlString+"&KSLASJ="+dateStart+"&JSLASJ="+dateEnd+"&LADW=4502");
            log.info(urlString+"&KSLASJ="+dateStart+"&JSLASJ="+dateEnd+"&LADW=4502");
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求方法为GET
            connection.setRequestMethod("GET");

            // 获取响应代码
            int responseCode = connection.getResponseCode();
            log.info("Response Code: " + responseCode);

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),Charset.forName("GBK")));
            String line;
            StringBuilder responseBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
            // 将GBK编码的内容解码为内部字符串（UTF-16）
            String gbkResponse = responseBuilder.toString();

            // 将解码后的内容编码为UTF-8编码的字节数组
            byte[] utf8Bytes = gbkResponse.getBytes(StandardCharsets.UTF_8);

            // 创建UTF-8编码的字符串
            String utf8Response = new String(utf8Bytes, StandardCharsets.UTF_8);

            reader.close();

            // 打印响应内容
            log.info("Response Content: ");
            log.info(utf8Response);
            List<Case> list = parseHtmlToCases(utf8Response.toString());
            log.info("同步并解析"+list.size()+"个案件");
            for (Case case1:list){
                //log.info("----------------------------case----------------------------");
                case1.setJurisdiction(JurisdictionUtil.getJurisdiction(case1.getCaseUnit()));
                if (StringUtils.isEmpty(case1.getJurisdiction())){
                    case1.setJurisdiction(JurisdictionUtil.getJurisdiction(case1.getOrganizer()));
                }
                //log.info(case1);
            }
            // 关闭连接
            connection.disconnect();
            return list;
        } catch (Exception e) {
            log.error("获取case列表错误",e);
        }
        return null;
    }

    public List<Case> parseHtmlToCases(String html) {
        List<Case> cases = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements rows = doc.select("tr");

        for (Element row : rows) {
            Elements cells = row.select("td");

            if (cells.size() == 12) { // 检查是否有合适的<td>标签
                if (StringUtils.isEmpty(cells.get(1).text())||"案件编号".equals(cells.get(1).text())){//首行（标题行）和caseNo是空直接过
                    continue;
                }
                Case caseObj = new Case();
                caseObj.setSeq(cells.get(0).text());
                caseObj.setCaseNo(cells.get(1).text());
                caseObj.setCaseName(cells.get(2).text());
                caseObj.setCaseUnit(cells.get(3).text());

                try {
                    String registerDateString = cells.get(4).text();
                    if (!StringUtils.isEmpty(registerDateString)){
                        Date registerDate = dateFormat.parse(registerDateString);
                        caseObj.setRegisterDate(registerDate);
                    }
                    String solveDateString = cells.get(6).text();
                    if (!StringUtils.isEmpty(solveDateString)){
                        Date solveDate = dateFormat.parse(solveDateString);
                        caseObj.setSolveDate(solveDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                caseObj.setOrganizer(cells.get(5).text());
                caseObj.setType(cells.get(7).text());
                caseObj.setProjectId(cells.get(8).text());
                caseObj.setOrganiser(cells.get(9).text());
                caseObj.setCoOrganiser(cells.get(10).text());
                try {
                    if (!StringUtils.isEmpty(cells.get(11).text())){
                        caseObj.setMoney(Float.parseFloat(cells.get(11).text()));
                    }else {
                        caseObj.setMoney(0);
                    }
                } catch (NumberFormatException e) {
                    caseObj.setMoney(0);
                }
                cases.add(caseObj);
            }
        }

        return cases;
    }
}

class StringFieldComparator implements Comparator<SimpleIndex> {
    @Override
    public int compare(SimpleIndex obj1, SimpleIndex obj2) {
        // 按照 name 字段的字母顺序进行比较
        return obj1.getDate().compareTo(obj2.getDate());
    }
}
