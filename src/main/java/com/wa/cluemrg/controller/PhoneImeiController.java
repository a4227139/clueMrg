package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.PhoneImeiService;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneImei")
@Secured("ROLE_LEVEL1")
public class PhoneImeiController {

    @Autowired
    private PhoneImeiService phoneImeiService;

    @PostMapping("/getPhoneImeiList")
    public JsGridVO getPhoneImeiList(@RequestBody PageBO<PhoneImei> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<PhoneImei> list = phoneImeiService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    @Secured("ROLE_LEVEL2")
    public int insert(@RequestBody PhoneImei phoneImei) {
        return phoneImeiService.insert(phoneImei);
    }

    @PutMapping("/update")
    @Secured("ROLE_LEVEL2")
    public int update(@RequestBody PhoneImei phoneImei) {
        return phoneImeiService.update(phoneImei);
    }

    @DeleteMapping("/delete")
    @Secured("ROLE_LEVEL2")
    public int delete(@RequestParam int id) {
        return phoneImeiService.delete(id);
    }
}

