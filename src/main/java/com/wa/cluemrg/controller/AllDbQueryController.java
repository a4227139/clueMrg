package com.wa.cluemrg.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.wa.cluemrg.entity.AllDbQueryParam;
import com.wa.cluemrg.service.AllDbQueryService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/allDbQuery")
public class AllDbQueryController {

    @Autowired
    AllDbQueryService allDbQueryService;

    @PostMapping("/batchInsertOrUpdateAllDbQuery")
    public int batchInsertOrUpdateByRequest(@RequestBody AllDbQueryParam allDbQueryParam) throws JsonProcessingException {
        String gjfzResponse = allDbQueryParam.getGjfzResponse();
        return allDbQueryService.batchInsertOrUpdateByRequest(gjfzResponse);
    }
}


