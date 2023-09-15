package com.wa.cluemrg.controller;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.util.PoitlIOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.bo.CallLogExportBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.Case;
import com.wa.cluemrg.entity.CaseIndex;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.CaseService;
import com.wa.cluemrg.util.JurisdictionUtil;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/case")
public class CaseController {

    @Autowired
    CaseService caseService;
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
        try {
            if (StringUtils.isEmpty(dateStart)){
                dateStart="2023-01-01";
            }
            if (StringUtils.isEmpty(dateEnd)) {
                dateEnd = dateFormat.format(new Date());
            }
            Workbook workbook = generateExcelWorkbook(dateStart,dateEnd);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码
            String fileName = URLEncoder.encode("2023年全市电诈案件情况统计表（1.1-9.16）", "UTF-8").replaceAll("\\+", "%20");
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
        //导出线索表
        String caseExcelFileName="2023年全市电诈案件情况统计表"+"（"+dateStart+"到"+dateEnd+"）.xlsx";
        String fullCaseExcelFileName = ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" + caseExcelFileName;
        //创建文件夹
        File directory = new File(ROOT_APPLICATION_PATH+"/case/"+formatYMD.format(new Date())+ "/" );
        if (!directory.exists()) {
            directory.mkdirs(); // Creates the directory and any necessary parent directories
        }
        //获取指标
        List<CaseIndex> caseIndexList = getCaseIndex(dateStart,dateEnd,"");
        String filePath = this.getClass().getClassLoader().getResource("templates/caseTemplate.xlsx").getPath();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
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

    @GetMapping("/exportWord")
    public void exportWord(@RequestParam("dateStart") String dateStart,
                           @RequestParam("dateEnd") String dateEnd,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException{
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("案件报告", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".docx");
            //获取指标
            List<CaseIndex> caseIndexList = getCaseIndex(dateStart,dateEnd,"");
            //填充文档
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("list", caseIndexList);
            // 创建一个新列表存储排序后的指标
            /*List<CaseIndex> caseIndexListSortByCount = new ArrayList<>();
            caseIndexListSortByCount.addAll(caseIndexList);
            caseIndexListSortByCount.remove(0);
            // 创建一个自定义的Comparator，按照数量升序排序
            Comparator<CaseIndex> countComparator = Comparator.comparingInt(CaseIndex::getCount);
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortByCount, countComparator);
            dataMap.put("caseIndexListSortByCount", caseIndexListSortByCount);*/

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

    @GetMapping("/getCaseIndex")
    public List<CaseIndex> getCaseIndex(@RequestParam("dateStart")  String dateStart,
                                        @RequestParam("dateEnd") String dateEnd,
                                        @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        Case param = new Case();
        if (StringUtils.isEmpty(dateStart)){
            dateStart="2023-01-01";
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

        Case paramHistory = new Case();
        String lastYearDateStart=(Integer.parseInt(dateStart.substring(0,4))-1)+dateStart.substring(4);
        String lastYearDateEnd=(Integer.parseInt(dateEnd.substring(0,4))-1)+dateEnd.substring(4);
        paramHistory.setRegisterDateStart(dateFormat.parse(lastYearDateStart));
        paramHistory.setRegisterDateEnd(dateFormat.parse(lastYearDateEnd));
        List<Case> listHistory = caseService.selectAllHistory(paramHistory);

        List<CaseIndex> caseIndexList = dealCaseIndex(list,listSolve,listHistory,dateStart,dateEnd);
        return caseIndexList;
    }

    public List<CaseIndex> dealCaseIndex(List<Case> caseList,List<Case> solveCaseList,List<Case> historyCaseList,String dateStart,String dateEnd) throws ParseException {
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
            float lossMoneyHistory = caseIndex.getLossMoneyHistory();
            caseIndex.setLossMoneyHistoryFormat(decimalFormat3.format(lossMoneyHistory/10000));
            caseIndex.setLossMoneyHistoryFormat2(decimalFormat2.format(lossMoneyHistory/100000000));
            //万人发案数
            float countPerW = (float)caseIndex.getCount()/jurisdictionPopulationMap.get(jurisdiction);
            countPerW=countPerW*10000;
            caseIndex.setCountPerW(countPerW);
            caseIndex.setCountPerWFormat(decimalFormat.format(countPerW));
            //立案同比
            float yCountRatio=(float)(caseIndex.getCount()-caseIndex.getCountHistory())/caseIndex.getCountHistory()*100;
            caseIndex.setYCountRatio(decimalFormat.format(yCountRatio));
            //损失同比
            float yLossMoneyRatio=(caseIndex.getLossMoney()-caseIndex.getLossMoneyHistory())/caseIndex.getLossMoneyHistory()*100;
            caseIndex.setYLossMoneyRatio(decimalFormat.format(yLossMoneyRatio));
            caseIndexList.add(caseIndex);
        }
        return caseIndexList;
    }

    @GetMapping("/sync")
    public String sync(@RequestParam("dateStart")  String dateStart,
                                @RequestParam("dateEnd") String dateEnd) throws ParseException {
        List<Case> list = syncCaseList(dateStart,dateEnd,jzurl);
        Case param = new Case();
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

