package com.wa.cluemrg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.cluemrg.controller.RequisitionController;
import com.wa.cluemrg.dao.EffectMapper;
import com.wa.cluemrg.entity.AllDbQuery;
import com.wa.cluemrg.entity.Effect;
import com.wa.cluemrg.entity.Gang;
import com.wa.cluemrg.entity.Requisition;
import com.wa.cluemrg.util.FileUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class ScheduledTaskService {

    @Autowired
    EffectMapper effectMapper;
    String[] departments = new String[]{"城中分局","鱼峰分局","柳南分局","柳北分局","柳江分局","柳东分局","柳城县局","鹿寨县局","融安县局","融水县局","三江县局"};

    @Autowired
    GangService gangService;
    @Autowired
    RequisitionService requisitionService;
    @Autowired
    AllDbQueryService allDbQueryService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd");

    static List<String> waitToAdd = new ArrayList<>();

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void initEffectEveryDay() {
        for (String depart:departments){
            Effect effect = new Effect();
            effect.setDate(LocalDate.now());
            effect.setDepartment(depart);
            effect.setSue(0);
            effect.setDetention(0);
            try{
                effectMapper.insert(effect);
            }catch (Exception e){
                log.info("后台任务：初始化战果表effect，插入失败："+effect);
            }
        }
    }

    /*@Scheduled(cron = "0 0 3 * * *") // 3 o'clock 0 0 3 * * *
    public void findAllGang() {
        int id=1;
        List<Gang> gangList = new ArrayList<>();
        List<String> phoneList = gangService.selectAllPhone();
        Set<String> visited = new HashSet<>();
        for (String phone : phoneList){
            if (visited.contains(phone)){
                continue;
            }
            Gang gang = gangService.genarateGang(phone);
            visited.addAll(gang.getPhoneList());
            if (gang.getPhoneList().size()>2){
                gang.setId(id);
                gangList.add(gang);
                gang.setLastReviseTime(new Date());
                id++;
            }
        }
        gangService.deleteAll();
        gangService.batchInsert(gangList);
    }*/

    @Scheduled(cron = "0 0 3 * * *") // 3 o'clock
    public void findAllGang() {
        List<String> phoneList = gangService.selectAllPhone();
        for (String phone : phoneList){
            waitToAdd.add(phone);
        }
    }

    public static void waitToAdd(String element){
        waitToAdd.add(element);
    }

    public static void waitToAdd(Collection<String> elements){
        waitToAdd.addAll(elements);
    }

    @Scheduled(cron = "*/10 * * * * *") // 10 seconds
    public void addElementToGang() {
        Set<String> added = new HashSet<>();
        List<Gang> insertList = new ArrayList<>();
        for (String element:waitToAdd){
            if (added.contains(element)){
                continue;
            }
            Gang gang = gangService.genarateGang(element);
            Set<String> phoneSet = new HashSet<>();
            phoneSet.addAll(gang.getPhoneList());
            added.addAll(gang.getPhoneList());
            Set<Gang> originalGangSet = new HashSet<>();
            for (String phone:phoneSet){
                if (StringUtils.isEmpty(phone)){
                    continue;
                }
                Gang originalGang = gangService.selectByPhone(phone);
                if (originalGang!=null){
                    originalGangSet.add(originalGang);
                }
            }
            for (Gang originalGang:originalGangSet){
                Set<String> originalPhoneSet = new HashSet<>();
                originalPhoneSet.addAll(originalGang.getPhoneList());
                //判断是否子集，是则更新即可
                if (phoneSet.containsAll(originalPhoneSet)){
                    gang.setId(originalGang.getId());
                    gang.setLastReviseTime(LocalDateTime.now());
                    gang.setName(originalGang.getName());
                    gang.setJurisdiction(originalGang.getJurisdiction());
                    gang.setIssueTime(originalGang.getIssueTime());
                    gangService.update(gang);
                    break;
                }else {//得到phone差集，删除gang，重新生成新gang
                    Set<String> difference = new HashSet<>(originalPhoneSet);
                    difference.removeAll(phoneSet);
                    waitToAdd.addAll(difference);
                    gangService.delete(originalGang.getId());
                }
            }
            if (originalGangSet.isEmpty()&&gang.getPhoneList().size()>=2){
                gang.setLastReviseTime(LocalDateTime.now());
                insertList.add(gang);
            }
        }
        if (!insertList.isEmpty()){
            gangService.batchInsert(insertList);
        }
        waitToAdd.clear();
    }


    int current = 1;
    int pages = 10;
    @Scheduled(cron = "0 * * * * *") // 1 minute
    public void updateRequisition() throws JsonProcessingException, InterruptedException {
        Thread.sleep((long) (Math.random()*10000));
        if (StringUtils.isEmpty(RequisitionController.jjzfToken)){
            log.info("The jjzfToken is null");
            return;
        }else {
            log.info("The jjzfToken:"+RequisitionController.jjzfToken);
        }
        //页码到了就重新开始
        if (current==pages){
            current=1;
        }
        String response = sendToJJZF(current);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        // 获取data节点，并检查是否为空对象
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode != null && dataNode.isObject()) {
            JsonNode recordsNode = dataNode.path("records");
            // 获取data节点下的current和pages的值
            current = jsonNode.get("data").get("current").asInt();
            pages = jsonNode.get("data").get("pages").asInt();

            List<Requisition> requisitions = new ArrayList<>();

            for (JsonNode recordNode : recordsNode) {
                Requisition requisition = objectMapper.treeToValue(recordNode, Requisition.class);
                requisitions.add(requisition);
            }
            requisitionService.batchInsertOrUpdate(requisitions);
            RequisitionController.lastUpdatedTime=sdf.format(new Date());
            current++;
        } else {
            log.info("Data is empty or not an object.");
        }
    }

    private String sendToJJZF(int current){
        //String url = "http://10.148.105.235/dzfw/yj/list?size=10&current=1&rawData=1&cityCode=";
        String url = "http://10.148.105.235/dzfw/yj/list?CardNum=&Name=&IDNum=&phone=&sLastContactTime=&eLastContactTime=&sExcEndTime=2024-01-01&eExcEndTime=2024-02-23&sCreateTime=&eCreateTime=&rawData=1&cityCode=450200&size=200&current="+current;
        String jjzfToken = "Bearer 407ff196-ab74-4d23-a1c9-d429714824b6";
        String lyxtbhValue = "4500001100000001";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 构建 GET 请求
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
        httpGet.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, RequisitionController.jjzfToken);
        httpGet.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        httpGet.setHeader(HttpHeaders.COOKIE, "Authorization=" + RequisitionController.jjzfToken);
        httpGet.setHeader("SYSTEM-CODE", "0503");
        httpGet.setHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
        httpGet.setHeader("lyxtbh", lyxtbhValue);

        try {
            log.info("sendToJJZF:"+url);
            // 发送请求并获取响应
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);

            // 打印响应码
            log.info("Response Code: " + response.getStatusLine().getStatusCode());

            // 打印响应体
            String responseBody = EntityUtils.toString(response.getEntity());
            log.info("Response Body: " + responseBody);

            // 打印响应头
            Arrays.stream(response.getAllHeaders())
                    .forEach(header -> log.info(header.getName() + ": " + header.getValue()));
            return responseBody;
        } catch (IOException e) {
            log.info("请求止付列表失败",e);
            return null;
        }
        //return FileUtil.readFile("C:\\Users\\WWH\\Desktop\\sendToJJZF-Response.txt");
    }

    //@Scheduled(cron = "0 * * * * *") // 1 minute
    public void updateAllDbQuery() throws JsonProcessingException, InterruptedException {
        Thread.sleep((long) (Math.random()*10000));
        if (StringUtils.isEmpty(RequisitionController.gjfzToken)){
            log.info("The gjfzToken is null");
            return;
        }else {
            log.info("The gjfzToken:"+RequisitionController.gjfzToken);
        }
        //页码到了就重新开始
        /*if (current==pages){
            current=1;
        }*/
        String response = sendToGJFZ("","","");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        // 获取data节点，并检查是否为空对象
        JsonNode dataNode = jsonNode.get("data");
        if (dataNode != null && dataNode.isObject()) {
            JsonNode recordsNode = dataNode.path("result");
            // 获取data节点下的current和pages的值
            /*current = jsonNode.get("data").get("currentPage").asInt();
            pages = jsonNode.get("data").get("totalPage").asInt();*/

            List<AllDbQuery> allDbQueryList = new ArrayList<>();

            for (JsonNode recordNode : recordsNode) {
                AllDbQuery allDbQuery = objectMapper.treeToValue(recordNode, AllDbQuery.class);
                allDbQueryList.add(allDbQuery);
            }
            allDbQueryService.batchInsertOrUpdate(allDbQueryList);
            //RequisitionController.lastUpdatedTime=sdf.format(new Date());
            //current++;
        } else {
            log.info("Data is empty or not an object.");
        }
    }

    private String sendToGJFZ(String accountNumber,String queryStartTime,String queryEndTime){
        String apiUrl = "http://11.4.37.242/faceapi/alldbquery/likeSelect";
        //token="3kwC1DQFHUB7Ua9z1RU6VAALMD9TBSquL5SUSSXmREPpvhVhtPhj5BJnMsQicqomyqjK6QDBZVXdJGiqgUpX6yQoXuFv7i6jY9NesNWtCfSr1V";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求头
            connection.setRequestMethod("POST");
            connection.setRequestProperty(HttpHeaders.ACCEPT, "application/json, text/plain, */*");
            connection.setRequestProperty(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.9");
            connection.setRequestProperty(HttpHeaders.CONNECTION, "keep-alive");
            connection.setRequestProperty(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Origin", "http://11.4.37.242");
            connection.setRequestProperty("Referer", "http://11.4.37.242/fundMove/wholeBaseQuery/10");
            connection.setRequestProperty("extra", "codeNumber=450202199507230015&identifier=%E7%A8%8B%E4%B9%89%E9%92%A7&unitThisName=%E5%B9%BF%E8%A5%BF%E5%A3%AE%E6%97%8F%E8%87%AA%E6%B2%BB%E5%8C%BA%E6%9F%B3%E5%B7%9E%E5%B8%82%E5%85%AC%E5%AE%89%E5%B1%80%E5%88%91%E4%BE%A6%E6%94%AF%E9%98%9F&openid=3a538e06b04244bcae5f312f3709cd3a&phone=18775228595");
            connection.setRequestProperty("token", RequisitionController.gjfzToken);

            // 构建请求体
            String requestBody = "{\"currentPage\":1,\"pageSize\":\"100\",\"many\":0,\"bankname\":\"\",\"bankid\":\"\",\"checktype\":\"\",\"accountname\":\"\",\"accountnumber\":\"" + accountNumber + "\",\"querystarttime\":\"" + queryStartTime + "\",\"queryendtime\":\"" + queryEndTime + "\",\"result\":\"\",\"operatorname\":\"\",\"applicationorgid\":\"\",\"txcode\":\"200201,200101,200103,200105,200203,200205,200301,200309,200303\",\"querytype\":\"1\",\"bindingTelphone\":\"\",\"idCard\":\"\",\"manager\":true,\"relistenable\":true,\"recountenable\":false,\"casefrom\":\"1\"}";

            // 启用输出流，向服务器写入请求体
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            log.info("sendToGJFZ:"+url);
            int responseCode = connection.getResponseCode();
            // 打印响应码
            log.info("Response Code: " + responseCode);
            StringBuilder response = new StringBuilder();
            // 读取响应内容
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                // 打印响应内容
                log.info("Response Content: " + response);
            }
            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            log.info("请求国家反诈平台失败，token："+RequisitionController.gjfzToken+",accountNumber:"+accountNumber,e);
            return null;
        }
        //return FileUtil.readFile("C:\\Users\\WWH\\Desktop\\sendToGJZF-Response.txt");
    }

}