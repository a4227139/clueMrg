package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.Attribution;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.AttributionService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attribution")
public class AttributionController {

    @Autowired
    private AttributionService attributionService;

    @GetMapping("/getAttribution")
    public Attribution getAttribution(@RequestParam String phone) {
        Attribution attribution = new Attribution();
        attribution.setSegment(phone.substring(0,7));
        List<Attribution> list = attributionService.selectAll(attribution);
        if (!CollectionUtils.isEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    @PostMapping("/getAttributionList")
    public JsGridVO getAttributionList(@RequestBody PageBO<Attribution> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="SEQ";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="ASC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<Attribution> list = attributionService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody Attribution attribution) {
        return attributionService.insert(attribution);
    }

    @PutMapping("/update")
    public int update(@RequestBody Attribution attribution) {
        return attributionService.update(attribution);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam int id) {
        return attributionService.delete(id);
    }
}

