package com.wa.cluemrg.bo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class GangBo {
    @ExcelProperty(value={"编号"})
    int id;
    @ExcelIgnore
    String name;
    @ExcelProperty(value={"涉案电话"})
    String phone;
    @ExcelProperty(value={"设备（IMEI码）"})
    String imei;
    @ExcelProperty(value={"人员（身份证）"})
    String person;
    @ExcelProperty(value={"线索编号"})
    String clue;
    @ExcelProperty(value={"案件编号"})
    String caseNo;
    @ExcelProperty(value={"分县局"})
    String jurisdiction;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ExcelProperty(value={"下发时间"})
    LocalDateTime issueTime;
    @ExcelIgnore
    LocalDateTime lastReviseTime;
    @ExcelIgnore
    String phones;
    @ExcelIgnore
    String subOffice;
    @ExcelIgnore
    String dateFormat;
    @ExcelIgnore
    String dateFormatToday;
}
