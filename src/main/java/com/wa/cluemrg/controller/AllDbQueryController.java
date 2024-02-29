package com.wa.cluemrg.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.AllDbQueryParam;
import com.wa.cluemrg.entity.AllDbQuery;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.AllDbQueryService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/allDbQuery")
public class AllDbQueryController {

    @Autowired
    AllDbQueryService allDbQueryService;
    public static String lastUpdatedTime;

    @PostMapping("/batchInsertOrUpdateAllDbQuery")
    public int batchInsertOrUpdateByRequest(@RequestBody AllDbQueryParam allDbQueryParam) throws JsonProcessingException {
        String gjfzResponse = allDbQueryParam.getGjfzResponse();
        return allDbQueryService.batchInsertOrUpdateByRequest(gjfzResponse);
    }

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.AllDbQuery>
     */
    @PostMapping("/getAllDbQueryList")
    public JsGridVO getAllDbQueryList(@RequestBody PageBO<AllDbQuery> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="ACCOUNT_NUMBER";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<AllDbQuery> list = allDbQueryService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<AllDbQuery> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insertAllDbQuery")
    @Secured("ROLE_LEVEL2")
    public int insertAllDbQuery(@RequestBody AllDbQuery allDbQuery) {
        return allDbQueryService.insert(allDbQuery);
    }

    @PutMapping("/updateAllDbQuery")
    @Secured("ROLE_LEVEL2")
    public int updateAllDbQuery(@RequestBody AllDbQuery allDbQuery) {
        return allDbQueryService.update(allDbQuery);
    }

    @DeleteMapping("/deleteAllDbQuery")
    @Secured("ROLE_LEVEL2")
    public int updateAllDbQuery(@RequestParam String allDbQueryId) {
        return allDbQueryService.delete(allDbQueryId);
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return lastUpdatedTime;
    }
}


