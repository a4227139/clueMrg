package com.wa.cluemrg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wa.cluemrg.dao.AllDbQueryMapper;
import com.wa.cluemrg.entity.AllDbQuery;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class AllDbQueryService {

    @Autowired
    private AllDbQueryMapper allDbQueryMapper;

    public AllDbQuery select(String applicationId) {
        return allDbQueryMapper.selectById(applicationId);
    }

    public List<AllDbQuery> selectAll(AllDbQuery allDbQuery) {
        return allDbQueryMapper.selectAll(allDbQuery);
    }

    public int insert(AllDbQuery allDbQuery) {
        return allDbQueryMapper.insert(allDbQuery);
    }

    public int update(AllDbQuery allDbQuery) {
        return allDbQueryMapper.update(allDbQuery);
    }

    public int delete(String applicationId) {
        return allDbQueryMapper.delete(applicationId);
    }

    public int batchInsertOrUpdate(List<AllDbQuery> list) {
        return allDbQueryMapper.batchInsertOrUpdate(list);
    }

    public int batchInsertOrUpdateByRequest(String gjfzResponse) throws JsonProcessingException {
        int count=0;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(gjfzResponse);
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
            count = allDbQueryMapper.batchInsertOrUpdate(allDbQueryList);
            //RequisitionController.lastUpdatedTime=sdf.format(new Date());
            //current++;
        } else {
            log.info("Data is empty or not an object.");
        }
        return count;
    }

}
