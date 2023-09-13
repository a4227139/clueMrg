package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.PhoneImeiService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneImei")
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
    public int insert(@RequestBody PhoneImei phoneImei) {
        return phoneImeiService.insert(phoneImei);
    }

    @PutMapping("/update")
    public int update(@RequestBody PhoneImei phoneImei) {
        return phoneImeiService.update(phoneImei);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam int id) {
        return phoneImeiService.delete(id);
    }
}

