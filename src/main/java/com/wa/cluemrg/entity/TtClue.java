package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TtClue {
    @ExcelProperty(value={"线索编号","线索编号（JZ,YD,LT,DX）"})
    protected String clueId;
    @ExcelProperty(value={"漫游地"})
    protected String roaming;
    @ExcelProperty(value={"运营商"})
    protected String operator;
    @ExcelProperty(value={"涉案电话"})
    protected String phone;
    @ExcelProperty(value={"案件类别","涉诈类型"})
    protected String caseCategory;
    @ExcelProperty(value={"IMEI码（多个用#分隔）","IMEI码"})
    protected String imeis;
    @ExcelProperty(value={"基站名称"})
    protected String bsName;
    @ExcelProperty(value={"基站经度"})
    protected String longitude;
    @ExcelProperty(value={"基站纬度"})
    protected String latitude;
    @ExcelProperty(value={"lac"})
    protected String lac;
    @ExcelProperty(value={"ci"})
    protected String ci;
    @ExcelProperty(value={"机主信息","开户人姓名"})
    protected String owner;
    @ExcelProperty(value={"证件号码"})
    protected String ownerId;
    @ExcelProperty(value={"开户人住址"})
    protected String ownerAddress;
    @ExcelProperty(value={"落位时间","落位时间（YYYY-MM-DD HH:MM:dd）"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:dd")
    protected Date clueTime;
    @ExcelProperty(value={"状态"})
    protected String state;
    @ExcelProperty(value={"下发时间"})
    protected String issueTime;
    @ExcelProperty(value={"作案地点"})
    protected String jurisdiction;
    @ExcelProperty(value={"战果材料"})
    protected String material;
    @ExcelProperty(value={"号码归属地"})
    protected String attribution;
    @ExcelProperty(value={"打击情况"})
    protected String situation;
    @ExcelProperty(value={"抓获嫌疑人姓名"})
    protected String suspect;
    @ExcelProperty(value={"打击人数"})
    protected String suspectNum;
    @ExcelProperty(value={"技侦查询人"})
    protected String jzPerson;
    @ExcelProperty(value={"查询时间"})
    protected String jzInquiryTime;
    @ExcelProperty(value={"新网研判人"})
    protected String fzPerson;
    @ExcelProperty(value={"备注"})
    protected String remark;
    @ExcelProperty(value={"受害人号码"})
    protected String victimPhone;
    protected String[] phones;
}

