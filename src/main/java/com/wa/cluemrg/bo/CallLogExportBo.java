package com.wa.cluemrg.bo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CallLogExportBo {

    @ExcelIgnore
    private Integer id;
    @ExcelProperty(value={"用户号码"})
    private String phone;
    @ExcelProperty(value = {"起始时间"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @ExcelProperty(value = {"通信地点"})
    private String communicationLocation;
    @ExcelProperty(value = {"通信方式"})
    private String communicationType;
    @ExcelProperty("通信类型")
    private String businessType;
    @ExcelProperty(value = {"对方号码"})
    private String oppositePhone;
    @ExcelProperty(value = {"通话时长"})
    private String duration;
    @ExcelProperty(value = {"IMEI"})
    private String imei;
    @ExcelProperty(value = {"IMSI"})
    private String imsi;
    @ExcelProperty(value = {"基站编码LAC"})
    private String lac;
    @ExcelProperty(value = {"基站编码CI"})
    private String ci;
    @ExcelProperty(value = {"基站地址"})
    private String location;
    @ExcelProperty(value = {"基站地址"})
    private String address;
    @ExcelProperty(value = {"经度"})
    private String longitude;
    @ExcelProperty(value = {"纬度"})
    private String latitude;

}
