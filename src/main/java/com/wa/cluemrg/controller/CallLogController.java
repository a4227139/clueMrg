package com.wa.cluemrg.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.bo.CallLogExportBo;
import com.wa.cluemrg.bo.PageBO;
import com.wa.cluemrg.entity.*;
import com.wa.cluemrg.response.ResponseResult;
import com.wa.cluemrg.service.*;
import com.wa.cluemrg.util.UnderlineToCamelUtils;
import com.wa.cluemrg.vo.JsGridVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;


@RestController
@RequestMapping("/callLog")
@Secured("ROLE_LEVEL1")
public class CallLogController {

    @Autowired
    CallLogService callLogService;
    @Autowired
    BtClueService btClueService;
    @Autowired
    PhoneImeiService phoneImeiService;
    @Autowired
    PhoneImsiService phoneImsiService;
    @Autowired
    NodeTagService nodeTagService;
    @Autowired
    LinkTagService linkTagService;
    @Autowired
    TtClueService ttClueService;
    @Autowired
    BSLocationService bsLocationService;

    Map<String,String> bsLocationMap = new HashMap<>(50000);
    @PostConstruct
    public void init() throws IOException {
        BSLocation param = new BSLocation();
        List<BSLocation> list = bsLocationService.selectAll(param);
        for (BSLocation bsLocation:list){
            String lac = StringUtils.isEmpty(bsLocation.getLac())?"":bsLocation.getLac();
            String ci = StringUtils.isEmpty(bsLocation.getCi())?"":bsLocation.getCi();
            bsLocationMap.put(lac+"-"+ci,bsLocation.getLocation());
        }
    }

    /**
     * 分页获取线索列表
     *
     * @param
     * @return com.response.Response<com.wa.cluemrg.entity.CallLog>
     */
    @PostMapping("/getCallLogList")
    public JsGridVO getCallLogList(@RequestBody PageBO<CallLogBo> pageBo) {
        /*if (StringUtils.isEmpty(pageBo)) {
            throw new BusinessException("所传参数错误", "200");
        }*/
        pageBo.getData().setPageIndex(pageBo.getPageIndex());
        pageBo.getData().setPageSize(pageBo.getPageSize());
        String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        if (StringUtils.isEmpty(sortField)) {
            sortField="PHONE";
        }
        String sortOrder = pageBo.getSortOrder();
        if (StringUtils.isEmpty(sortOrder)) {
            sortOrder="ASC";
        }
        pageBo.getData().setSortField(sortField);
        pageBo.getData().setSortOrder(sortOrder);
        pageBo.getData().setOffset(pageBo.getPageSize()*(pageBo.getPageIndex()-1));
        //PageHelper.startPage(pageBo.getPageIndex(), pageBo.getPageSize());
        //String sortField = UnderlineToCamelUtils.camelToUnderline(pageBo.getSortField());
        //String sortOrder = pageBo.getSortOrder();
        //PageHelper.orderBy(sortField+" "+sortOrder);

        List<CallLogBo> list = fillCallLog(callLogService.selectAll(pageBo.getData()));

        PageInfo page = new PageInfo(list);
        int total = callLogService.selectCount(pageBo.getData());
        int pages = (total/ pageBo.getPageSize())+1;
        page.setPages(pages);
        page.setTotal(total);
        JsGridVO<CallLogBo> vo = new JsGridVO(page);

        ResponseResult response = new ResponseResult();
        response.setObject(vo);
        return vo;
    }

    @Secured("ROLE_LEVEL2")
    @PostMapping("/insert")
    public int insert(@RequestBody CallLog callLog) {
        return callLogService.insert(callLog);
    }

    @Secured("ROLE_LEVEL2")
    @PutMapping("/update")
    public int update(@RequestBody CallLog callLog) {
        return callLogService.update(callLog);
    }

    @Secured("ROLE_LEVEL2")
    @DeleteMapping("/delete")
    public int delete(@RequestParam String id) {
        return callLogService.delete(id);
    }

    @Secured("ROLE_LEVEL2")
    @PostMapping("/upload")
    public String upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");
        if(!file.getOriginalFilename().endsWith(".xlsx")){
            return "仅支持xlsx文件，请转换后再上传";
        }
        return callLogService.dealUpload(file);
    }

    @GetMapping("exportExcel")
    public void exportExcel(CallLogBo callLogBo,HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(callLogBo.getPhone()+"话单", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            List<CallLogBo> list = fillCallLog(callLogService.exportAll(callLogBo));
            /*for (CallLogBo callLog:list){
                callLog.setId(null);
            }*/
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), CallLogExportBo.class)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .autoCloseStream(Boolean.FALSE).sheet("话单")
                    .doWrite(list);
        } catch (Exception e) {
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Map<String, String> map = MapUtils.newHashMap();
            map.put("status", "failure");
            map.put("message", "下载文件失败" + e.getMessage());
            response.getWriter().println(JSON.toJSONString(map));
        }
    }

    public List<CallLogBo> fillCallLog(List<CallLogBo> list){
        for (CallLogBo callLogBo:list){
            String lac = StringUtils.isEmpty(callLogBo.getLac())?"":callLogBo.getLac();
            String ci = StringUtils.isEmpty(callLogBo.getCi())?"":callLogBo.getCi();
            callLogBo.setLocation(bsLocationMap.get(lac+"-"+ci));
        }
        return list;
    }

    @Data
    class Item{
        private String data;
        private String type;

        Item(String data,String type){
            this.data=data;
            this.type=type;
        }
    }

}
