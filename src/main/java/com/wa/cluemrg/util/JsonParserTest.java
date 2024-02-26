package com.wa.cluemrg.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.cluemrg.entity.Requisition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonParserTest {

    public static void main(String[] args) throws Exception {
        String json = "{\n" +
                "    \"msg\": null,\n" +
                "    \"code\": 0,\n" +
                "    \"data\": {\n" +
                "        \"total\": 125624,\n" +
                "        \"current\": 1,\n" +
                "        \"hitCount\": false,\n" +
                "        \"pages\": 12563,\n" +
                "        \"size\": 10,\n" +
                "        \"optimizeCountSql\": true,\n" +
                "        \"records\": [\n" +
                "            {\n" +
                "                \"cityCode\": \"450800\",\n" +
                "                \"bankName\": \"中国邮政储蓄银行\",\n" +
                "                \"lockState\": \"0\",\n" +
                "                \"cardNum\": \"6236776100000042642\",\n" +
                "                \"transactionAmount\": \"4800\",\n" +
                "                \"cityCodeLy\": null,\n" +
                "                \"secrit\": \"1273915\",\n" +
                "                \"lastContactTime\": \"2024-02-07 07:01:06\",\n" +
                "                \"ecreateTime\": \"2024-03-08 16:57:18\",\n" +
                "                \"state\": \"0\",\n" +
                "                \"jiechuState\": null,\n" +
                "                \"id\": \"623783752\",\n" +
                "                \"doQzh\": \"0\",\n" +
                "                \"bankCode\": \"403100000004\",\n" +
                "                \"firstZfTime\": null,\n" +
                "                \"applicantIp\": null,\n" +
                "                \"nextZfTime\": \"2099-01-01 08:00:00\",\n" +
                "                \"zfTime\": \"0\",\n" +
                "                \"applicant\": null,\n" +
                "                \"insertTime\": \"2024-02-07 16:58:00\",\n" +
                "                \"jobId\": null,\n" +
                "                \"lastZfTime\": \"2024-02-08 00:58:00\",\n" +
                "                \"spr\": null,\n" +
                "                \"createTime\": \"2024-02-07 16:57:18\",\n" +
                "                \"phone\": \"18520144764\",\n" +
                "                \"wantToQzhZf\": \"2\",\n" +
                "                \"name\": \"徐英强\",\n" +
                "                \"approvalCityCode\": null,\n" +
                "                \"idnum\": \"450881199608082697\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"searchCount\": true,\n" +
                "        \"orders\": []\n" +
                "    }\n" +
                "}";

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(json);

        JsonNode recordsNode = jsonNode.path("data").path("records");

        List<Requisition> requisitions = new ArrayList<>();

        for (JsonNode recordNode : recordsNode) {
            Requisition requisition = objectMapper.treeToValue(recordNode, Requisition.class);
            requisitions.add(requisition);
        }

        // 打印解析后的对象列表
        requisitions.forEach(System.out::println);
    }
}
