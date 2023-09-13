package com.wa.cluemrg.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.QgBtClueService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/qgBtClue")
public class QgBtClueController {

    @Autowired
    QgBtClueService qgBtClueService;
    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.BtClue>
     */
    @PostMapping("/getClueList")
    public JsGridVO getClueList(@RequestBody PageBO<BtClue> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="CLUE_ID";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<BtClue> list = qgBtClueService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<BtClue> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return qgBtClueService.getLatestDay();
    }

    @PostMapping("/insertClue")
    public int insertClue(@RequestBody BtClue btClue) {
        return qgBtClueService.insert(btClue);
    }

    @PutMapping("/updateClue")
    public int updateClue(@RequestBody BtClue btClue) {
        return qgBtClueService.update(btClue);
    }

    @DeleteMapping("/deleteClue")
    public int updateClue(@RequestParam String clueId) {
        return qgBtClueService.delete(clueId);
    }

    @PostMapping("/upload")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return qgBtClueService.dealUpload(file);
    }


}
