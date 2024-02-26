package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AlarmReceipt {
    //@ExcelProperty(value={"序号"})
    private Integer seq;
    @ExcelProperty(value={"警情编号"})
    private String jqno;
    @ExcelProperty(value={"报警时间"})
    private Date alarmTime;
    @ExcelProperty(value={"受令单位","分县局"})
    private String department;
    @ExcelProperty(value={"报警电话"})
    private String phone;
    @ExcelProperty(value={"报警人"})
    private String victim;
    @ExcelProperty(value={"报警人身份证"})
    private String id;
    @ExcelProperty(value={"所属辖区"})
    private String jurisdiction;
    @ExcelProperty(value={"社区"})
    private String community;
    @ExcelProperty(value={"派出所","辖区派出所"})
    private String pcs;
    @ExcelProperty(value={"案发地址"})
    private String address;
    @ExcelProperty(value={"警情内容","报警内容","简要案情"})
    private String content;
    @ExcelProperty(value={"警情类别","警情分类"})
    private String type;
    @ExcelProperty(value={"损失金额(万元)","损失金额(万)","损失金额"})
    private Float lossMoney;
    @ExcelProperty(value={"止付情况"})
    private String stopPayment;
    @ExcelProperty(value={"一级卡情况"})
    private String oneLevel;
    @ExcelProperty(value={"二级卡情况"})
    private String secondLevel;
    @ExcelProperty(value={"取款情况"})
    private String withdraw;
    @ExcelProperty(value={"引流窝点"})
    private String drainage;
    @ExcelProperty(value={"抓获嫌疑人情况"})
    private String capture;
    @ExcelProperty(value={"是/否追赃挽损"})
    private String recover;
    @ExcelProperty(value={"是/否串并案"})
    private String jointCase;
    @ExcelProperty(value={"是/否境外"})
    private String outside;
    @ExcelProperty(value={"是/否安装反诈app"})
    private String app;
    @ExcelProperty(value={"是/否宣传"})
    private String advocate;
    @ExcelProperty(value={"备注"})
    private String remark;
    @ExcelProperty(value={"止付金额"})
    private Float stopPaymentMoney;
    @ExcelProperty(value={"止付账户"})
    private Integer stopPaymentAccount;
    @ExcelProperty(value={"案件状态（已立案、已破案）"})
    private String state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:SS", timezone = "Asia/Shanghai")
    private Date alarmTimeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:SS", timezone = "Asia/Shanghai")
    private Date alarmTimeEnd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastReviseTime;
}
