package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class DkClue {
    @ExcelProperty(value={"线索编号"})
    private String clueId;
    @ExcelProperty(value={"案件编号"})
    private String caseNo;
    @ExcelProperty(value={"涉案号码","账号"})
    private String num;
    @ExcelProperty(value={"开卡人"})
    private String person;
    @ExcelProperty(value={"证件号码"})
    private String id;
    @ExcelProperty(value={"运营商","银行"})
    private String institution;
    @ExcelProperty(value={"开户网点"})
    private String outlets;
    @ExcelProperty(value={"开户日期"})
    private String time;
    @ExcelProperty(value={"开户省"})
    private String province;
    @ExcelProperty(value={"开户市"})
    private String city;
    @ExcelProperty(value={"首次下发时间"})
    private Date issueTime;
    @ExcelProperty(value={"类型"})
    private String type;
}

