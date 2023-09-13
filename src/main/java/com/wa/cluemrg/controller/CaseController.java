package com.wa.cluemrg.controller;


import com.alibaba.excel.util.MapUtils;
import com.alibaba.fastjson.JSON;
import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.util.PoitlIOUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.BtClueBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.Case;
import com.wa.cluemrg.entity.CaseIndex;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.CallLogService;
import com.wa.cluemrg.service.CaseService;
import com.wa.cluemrg.service.PhoneImeiService;
import com.wa.cluemrg.util.JurisdictionUtil;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
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
import javax.validation.Valid;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/exportWord")
    public void exportWord(@RequestParam("registerDateStart")  String registerDateStart,
                           @RequestParam("registerDateEnd") String registerDateEnd,
                           HttpServletRequest request,
                           HttpServletResponse response) throws IOException{
        try {
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("案件报告", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".docx");
            Case param = new Case();
            if (!StringUtils.isEmpty(registerDateStart)){
                param.setRegisterDateStart(dateFormat.parse(registerDateStart));
            }else {
                registerDateStart="2023-01-01";
            }
            if (!StringUtils.isEmpty(registerDateEnd)){
                param.setRegisterDateEnd(dateFormat.parse(registerDateEnd));
            }else {
                registerDateEnd=dateFormat.format(new Date());
            }
            List<Case> list = caseService.selectAll(param);
            List<CaseIndex> caseIndexList = dealCaseIndex(list,registerDateStart,registerDateEnd);

            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("list", caseIndexList);
            // 创建一个新列表存储排序后的指标
            List<CaseIndex> caseIndexListSortByCount = new ArrayList<>();
            caseIndexListSortByCount.addAll(caseIndexList);
            caseIndexListSortByCount.remove(0);
            // 创建一个自定义的Comparator，按照年龄升序排序
            Comparator<CaseIndex> countComparator = Comparator.comparingInt(CaseIndex::getCount);
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortByCount, countComparator);
            dataMap.put("caseIndexListSortByCount", caseIndexListSortByCount);

            // 创建一个新列表存储排序后的指标
            List<CaseIndex> caseIndexListSortBySolveRate = new ArrayList<>();
            caseIndexListSortBySolveRate.addAll(caseIndexList);
            caseIndexListSortBySolveRate.remove(0);
            // 创建一个自定义的Comparator，按照年龄升序排序
            Comparator<CaseIndex> solveRateComparator = Comparator.comparingDouble(CaseIndex::getSolveRate);
            // 使用Collections.sort()对列表进行排序
            Collections.sort(caseIndexListSortBySolveRate, solveRateComparator);
            dataMap.put("caseIndexListSortBySolveRate", caseIndexListSortBySolveRate);
            OutputStream out = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(out);
            Configure config = Configure.builder().useSpringEL().build();
            XWPFTemplate template = XWPFTemplate.compile(this.getClass().getClassLoader().getResourceAsStream("templates/templateJQ.docx"),config).render(dataMap);
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
    public List<CaseIndex> getCaseIndex(@RequestParam("registerDateStart")  String registerDateStart,
                                        @RequestParam("registerDateEnd") String registerDateEnd,
                                        @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        Case param = new Case();
        if (!StringUtils.isEmpty(registerDateStart)){
            param.setRegisterDateStart(dateFormat.parse(registerDateStart));
        }
        if (!StringUtils.isEmpty(registerDateEnd)){
            param.setRegisterDateEnd(dateFormat.parse(registerDateEnd));
        }
        List<Case> list = caseService.selectAll(param);
        List<CaseIndex> caseIndexList = dealCaseIndex(list,registerDateStart,registerDateEnd);
        return caseIndexList;
    }

    public List<CaseIndex> dealCaseIndex(List<Case> caseList,String registerDateStart,String registerDateEnd){
        String[] jurisdictionArray=new String[]{"市本级","城中分局","鱼峰分局","柳南分局","柳北分局","柳江分局","柳东分局","柳城分局","鹿寨县局","融安县局","融水县局","三江县局"};
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
        Map<Integer,CaseIndex> caseIndexMap = new HashMap<>();
        for (int i=0;i<jurisdictionArray.length;i++){
            CaseIndex caseIndex = new CaseIndex();
            caseIndex.setJurisdiction(jurisdictionArray[i]);
            caseIndex.setStartDate(registerDateStart);
            caseIndex.setEndDate(registerDateEnd);
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
            if (caseObj.getSolveDate()!=null){
                caseIndex.setSolveCount(caseIndex.getSolveCount()+1);
                cityCaseIndex.setSolveCount(cityCaseIndex.getSolveCount()+1);
            }
        }
        List<CaseIndex> caseIndexList = new ArrayList<>();
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String formattedResult;
        for (CaseIndex caseIndex :caseIndexMap.values()){
            //破案率
            float solveRate = (float)caseIndex.getSolveCount()/caseIndex.getCount()*100;
            formattedResult = decimalFormat.format(solveRate);
            caseIndex.setSolveRateFormat(formattedResult);
            caseIndex.setSolveRate(solveRate);
            //案均损失数
            float averageLossMoney = caseIndex.getLossMoney()/caseIndex.getCount()/10000;
            formattedResult = decimalFormat.format(averageLossMoney);
            caseIndex.setAverageLossMoney(formattedResult);
            //损失金额调格式
            float lossMoney = caseIndex.getLossMoney();
            lossMoney=lossMoney/10000;
            formattedResult = decimalFormat.format(lossMoney);
            caseIndex.setLossMoneyFormat(formattedResult);
            caseIndexList.add(caseIndex);
        }
        return caseIndexList;
    }

    @GetMapping("/sync")
    public String sync(@RequestParam("registerDateStart")  String registerDateStart,
                                @RequestParam("registerDateEnd") String registerDateEnd) throws ParseException {
        List<Case> list = syncCaseList(registerDateStart,registerDateEnd);
        Case param = new Case();
        if (!StringUtils.isEmpty(registerDateStart)){
            param.setRegisterDateStart(dateFormat.parse(registerDateStart));
        }
        if (!StringUtils.isEmpty(registerDateEnd)){
            param.setRegisterDateEnd(dateFormat.parse(registerDateEnd));
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

        int num = caseService.batchInsertOrUpdate(list);
        log.info("同步数据"+num+"条");
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

    public List<Case> syncCaseList(String registerDateStart,String registerDateEnd) {
        try {
            if (StringUtils.isEmpty(registerDateStart)){
                registerDateStart="2023-01-01";
            }
            if (StringUtils.isEmpty(registerDateEnd)){
                registerDateEnd=dateFormat.format(new Date());
            }
            // 创建一个URL对象，表示要访问的URL
            //URL url = new URL("http://"+host+":"+port+"/case-simple.html?KSLASJ="+registerDateStart+"&JSLASJ="+registerDateEnd+"&LADW=4502");
            URL url = new URL(jzurl+"&KSLASJ="+registerDateStart+"&JSLASJ="+registerDateEnd+"&LADW=4502");
            log.info(jzurl+"&KSLASJ="+registerDateStart+"&JSLASJ="+registerDateEnd+"&LADW=4502");
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
                //log.info(case1);
            }
            // 关闭连接
            connection.disconnect();

            //暂时的烂代码，同步破案列表
            URL urlSolve = new URL(jzsolveurl+"&KSLASJ="+registerDateStart+"&JSLASJ="+registerDateEnd+"&LADW=4502");
            log.info(jzsolveurl+"&KSLASJ="+registerDateStart+"&JSLASJ="+registerDateEnd+"&LADW=4502");
            // 打开连接
            HttpURLConnection connection2 = (HttpURLConnection) urlSolve.openConnection();

            // 设置请求方法为GET
            connection2.setRequestMethod("GET");

            // 获取响应代码
            int responseCode2 = connection2.getResponseCode();
            log.info("Response Code: " + responseCode2);

            // 读取响应内容
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(connection2.getInputStream(),Charset.forName("GBK")));
            String line2;
            StringBuilder responseBuilder2 = new StringBuilder();

            while ((line2 = reader2.readLine()) != null) {
                responseBuilder2.append(line2);
            }
            // 将GBK编码的内容解码为内部字符串（UTF-16）
            String gbkResponse2 = responseBuilder2.toString();

            // 将解码后的内容编码为UTF-8编码的字节数组
            byte[] utf8Bytes2 = gbkResponse2.getBytes(StandardCharsets.UTF_8);

            // 创建UTF-8编码的字符串
            String utf8Response2 = new String(utf8Bytes2, StandardCharsets.UTF_8);

            reader2.close();

            // 打印响应内容
            log.info("Response Content: ");
            log.info(utf8Response2);
            List<Case> list2 = parseHtmlToCases(utf8Response2.toString());
            log.info("同步并解析已破案的"+list2.size()+"个案件");
            /*for (Case case1:list){
                for (Case case2:list2) {
                    if (case1.getCaseNo().equals(case2.getCaseNo())){
                        case1.setSolveDate(case2.getSolveDate());
                        break;
                    }
                }
                //log.info(case1);
            }*/
            // 关闭连接
            connection2.disconnect();
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

