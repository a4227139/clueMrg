package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class CallLogSimple {
    private Integer id;
    @ExcelProperty(value={"用户号码"})
    private String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = {"起始时间"})
    private Date startTime;
    @ExcelProperty(value = {"通信地点"})
    private String communicationLocation;
    @ExcelProperty(value = {"通信方式"})
    private String communicationType;
    @ExcelProperty("通信类型")
    private String businessType;
    @ExcelProperty(value = {"对方号码"})
    private String oppositePhone;
    @ExcelProperty(value = {"通信时长"})
    private String duration;
    @ExcelProperty(value = {"大区"})
    private String bsId;
    @ExcelProperty(value = {"小区"})
    private String honeycomb;
    @ExcelProperty(value = {"IMEI"})
    private String imei;
    @ExcelProperty(value = {"IMSI"})
    private String imsi;

}

