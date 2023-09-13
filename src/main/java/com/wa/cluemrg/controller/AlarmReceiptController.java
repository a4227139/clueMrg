package com.wa.cluemrg.controller;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.AlarmReceipt;
import com.wa.cluemrg.exception.BusinessException;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.AlarmReceiptService;
import com.wa.cluemrg.vo.JsGridVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@RequestMapping("/alarmReceipt")
public class AlarmReceiptController {

    @Autowired
    AlarmReceiptService alarmReceiptService;
    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.AlarmReceipt>
     */
    @PostMapping("/getAlarmList")
    public JsGridVO getAlarmList(@RequestBody PageBO<AlarmReceipt> pageBo) {
        if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }

        PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortField)){
            sortField="ALARM_TIME";
        }
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder="DESC";
        }
        PageHelper.orderBy(sortField+" "+sortOrder);

        List<AlarmReceipt> list = alarmReceiptService.selectAll(pageBo.getData());

        PageInfo page = new PageInfo(list);
        JsGridVO<AlarmReceipt> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @GetMapping("/getLatestDay")
    public String getLatestDay() {
        return alarmReceiptService.getLatestDay();
    }

    @PostMapping("/insertAlarm")
    public int insertAlarm(@RequestBody AlarmReceipt alarmReceipt) {
        return alarmReceiptService.insert(alarmReceipt);
    }

    @PutMapping("/updateAlarm")
    public int updateAlarm(@RequestBody AlarmReceipt alarmReceipt) {
        return alarmReceiptService.update(alarmReceipt);
    }

    @DeleteMapping("/deleteAlarm")
    public int updateAlarm(@RequestParam String clueId) {
        return alarmReceiptService.delete(clueId);
    }

    @PostMapping("/upload")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return alarmReceiptService.dealUpload(file);
    }


}
