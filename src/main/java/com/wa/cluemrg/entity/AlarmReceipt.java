package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class AlarmReceipt {
    @ExcelProperty(value={"序号"})
    private Integer seq;
    @ExcelProperty(value={"报警时间"})
    private Date alarmTime;
    @ExcelProperty(value={"受令单位"})
    private String department;
    @ExcelProperty(value={"报警电话"})
    private String phone;
    @ExcelProperty(value={"报警人"})
    private String victim;
    @ExcelProperty(value={"报警人身份证"})
    private String id;
    @ExcelProperty(value={"所属辖区"})
    private String jurisdiction;
    @ExcelProperty(value={"所属辖区2"})
    private String jurisdiction2;
    @ExcelProperty(value={"案发地址"})
    private String address;
    @ExcelProperty(value={"警情内容"})
    private String content;
    @ExcelProperty(value={"警情类别"})
    private String type;
    @ExcelProperty(value={"损失金额(万元)"})
    private Double lossMoney;
    @ExcelProperty(value={"止付金额"})
    private Double stopPaymentMoney;
    @ExcelProperty(value={"止付账户"})
    private Integer stopPaymentAccount;
    @ExcelProperty(value={"案件状态（已立案、已破案）"})
    private String state;
    @ExcelProperty(value={"警情编号"})
    private String jqno;
}
