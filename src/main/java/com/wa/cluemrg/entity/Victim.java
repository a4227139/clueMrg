package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Victim {

    private Integer seq;
    @ExcelProperty(value={"姓名"})
    private String name;
    @ExcelProperty(value={"性别"})
    private String gender;
    @ExcelProperty(value={"身份证号"})
    private String id;
    @ExcelProperty(value={"年龄"})
    private int age;
    @ExcelProperty(value={"户籍"})
    private String householdRegistration;
    @ExcelProperty(value={"受教育程度（小学、初中、高中、大专、本科、硕士）"})
    private String education;
    @ExcelProperty(value={"职业"})
    private String job;
    @ExcelProperty(value={"工作单位"})
    private String employer;
    @ExcelProperty(value={"发案地点（工作地、家中、娱乐场所、其他）"})
    private String location;
    @ExcelProperty(value={"发案时间段（0时-8时、8时-12时、12时-18时、18时-24时）"})
    private String period;
    @ExcelProperty(value={"案件类型"})
    private String type;
    @ExcelProperty(value={"诈骗信息来源（电话、短信、QQ、微信、APP、网页、其他）"})
    private String source;
    @ExcelProperty(value={"国家反诈中心APP安装及预警功能开启情况"})
    private String appFunction;
    @ExcelProperty(value={"预警劝阻情况（见面、电话、短信劝阻或未预警）"})
    private String dissuade;
    @ExcelProperty(value={"此前受害人接受反诈宣传情况（简要文字描述）"})
    private String publicity;
    @ExcelProperty(value={"针对此类人群的防范措施及防范工作开展情况（简要文字描述）"})
    private String suggestion;
    @ExcelProperty(value={"备注"})
    private String remark;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime time;
    @ExcelProperty(value={"分县局"})
    private String department;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date timeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date timeEnd;
}

