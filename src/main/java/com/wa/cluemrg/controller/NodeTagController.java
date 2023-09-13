package com.wa.cluemrg.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BSLocation;
import com.wa.cluemrg.entity.NodeTag;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.NodeTagService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nodeTag")
public class NodeTagController {

    @Autowired
    private NodeTagService nodeTagService;

    @PostMapping("/getNodeTagList")
    public JsGridVO getNodeTagList(@RequestBody PageBO<NodeTag> pageBo) {
        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<NodeTag> list = nodeTagService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BSLocation> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @PostMapping("/insert")
    public int insert(@RequestBody NodeTag nodeTag) {
        return nodeTagService.insert(nodeTag);
    }

    @PutMapping("/update")
    public int update(@RequestBody NodeTag nodeTag) {
        return nodeTagService.update(nodeTag);
    }

    @DeleteMapping("/delete")
    public int update(@RequestParam int id) {
        return nodeTagService.delete(id);
    }
}

