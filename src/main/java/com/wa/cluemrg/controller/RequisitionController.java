package com.wa.cluemrg.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.Requisition;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.RequisitionService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/requisition")
@Secured("ROLE_LEVEL1")
public class RequisitionController {

    @Autowired
    RequisitionService requisitionService;

    private final ResourceLoader resourceLoader;

    @Autowired
    public RequisitionController(ResourceLoader resourceLoader) {
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

    public static String lastUpdatedTime;
    public static String authorizationToken;

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.Requisition>
     */
    @PostMapping("/getRequisitionList")
    public JsGridVO getRequisitionList(@RequestBody PageBO<Requisition> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="CREATE_TIME";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<Requisition> list = requisitionService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<Requisition> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }
    
    @PostMapping("/insertRequisition")
    @Secured("ROLE_LEVEL2")
    public int insertClue(@RequestBody Requisition requisition) {
        return requisitionService.insert(requisition);
    }

    @PutMapping("/updateRequisition")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestBody Requisition requisition) {
        return requisitionService.update(requisition);
    }

    @DeleteMapping("/deleteRequisition")
    @Secured("ROLE_LEVEL2")
    public int updateClue(@RequestParam int requisitionId) {
        return requisitionService.delete(requisitionId);
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return lastUpdatedTime;
    }

    @GetMapping("/updateToken")
    public void updateToken(@RequestParam("token") String token) {
        authorizationToken=token;
    }

}

