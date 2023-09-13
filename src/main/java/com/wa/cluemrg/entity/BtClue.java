package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BtClue {
    @ExcelProperty(value={"线索编号"})
    protected String clueId;
    @ExcelProperty(value={"漫游地"})
    protected String roaming;
    @ExcelProperty(value={"运营商"})
    protected String operator;
    @ExcelProperty(value={"涉案电话"})
    protected String phone;
    @ExcelProperty(value={"立案单位"})
    protected String caseUnit;
    @ExcelProperty(value={"案件编号"})
    protected String caseNo;
    @ExcelProperty(value={"案件类别"})
    protected String caseCategory;
    @ExcelProperty(value={"涉案金额"})
    protected String money;
    @ExcelProperty(value={"发案时间"})
    protected String time;
    @ExcelProperty(value={"简要案情"})
    protected String caseBrief;
    @ExcelProperty(value={"受害人电话"})
    protected String victimPhone;
    @ExcelProperty(value={"机主信息","开户人姓名"})
    protected String owner;
    @ExcelProperty(value={"证件号码"})
    protected String ownerId;
    @ExcelProperty(value={"开户人住址"})
    protected String ownerAddress;
    @ExcelProperty(value={"作案地点"})
    protected String jurisdiction;
    @ExcelProperty(value={"技侦查询人"})
    protected String jzPerson;
    @ExcelProperty(value={"新网研判人"})
    protected String fzPerson;
    @ExcelProperty(value={"状态"})
    protected String state;
    @ExcelProperty(value={"下发时间"})
    protected String issueTime;
    @ExcelProperty(value={"联络人"})
    protected String liaisonOfficer;
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
    @ExcelProperty(value={"查询时间"})
    protected String jzInquiryTime;
    @ExcelProperty(value={"备注"})
    protected String remark;

}

