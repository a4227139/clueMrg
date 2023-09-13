package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.PhoneTag;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.PhoneTagService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/phoneTag")
public class PhoneTagController {

    @Autowired
    private PhoneTagService phoneTagService;

    @PostMapping("/getPhoneTagList")
    public JsGridVO getPhoneTagList(@RequestBody PageBO<PhoneTag> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<PhoneTag> list = phoneTagService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody PhoneTag phoneTag) {
        return phoneTagService.insert(phoneTag);
    }

    @PutMapping("/update")
    public int update(@RequestBody PhoneTag phoneTag) {
        return phoneTagService.update(phoneTag);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam int id) {
        return phoneTagService.delete(id);
    }
}

