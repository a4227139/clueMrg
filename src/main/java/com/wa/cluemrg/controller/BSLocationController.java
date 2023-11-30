package com.wa.cluemrg.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.BSLocationService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/bsLocation")
@Secured("ROLE_LEVEL1")
public class BSLocationController {

    @Autowired
    BSLocationService bsLocationService;
    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.BSLocation>
     */
    @PostMapping("/getBSLocationList")
    public JsGridVO getBSLocationList(@RequestBody PageBO<BSLocation> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<BSLocation> list = bsLocationService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    @Secured("ROLE_LEVEL2")
    public int insert(@RequestBody BSLocation bsLocation) {
        return bsLocationService.insert(bsLocation);
    }

    @PutMapping("/update")
    @Secured("ROLE_LEVEL2")
    public int update(@RequestBody BSLocation bsLocation) {
        return bsLocationService.update(bsLocation);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_LEVEL2")
    public int delete(@RequestParam String id) {
        return bsLocationService.delete(id);
    }

}
