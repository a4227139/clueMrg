package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.PhoneImsi;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.PhoneImsiService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneImsi")
public class PhoneImsiController {

    @Autowired
    private PhoneImsiService phoneImsiService;

    @PostMapping("/getPhoneImsiList")
    public JsGridVO getPhoneImsiList(@RequestBody PageBO<PhoneImsi> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<PhoneImsi> list = phoneImsiService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody PhoneImsi phoneImsi) {
        return phoneImsiService.insert(phoneImsi);
    }

    @PutMapping("/update")
    public int update(@RequestBody PhoneImsi phoneImsi) {
        return phoneImsiService.update(phoneImsi);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam int id) {
        return phoneImsiService.delete(id);
    }
}

