package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class CallLog {
    private Integer id;
    @ExcelProperty(value={"用户号码","主叫号码","己方号码","业务号码","本机号码"})
    private String phone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = {"起始时间","开始时间","通话起始时间","截获时间*","通话开始时间","通话日期"})
    private Date startTime;
    @ExcelProperty(value = {"通话时间"})
    private String startTime2;//处理分离式的第二段例如 21:49:51 或 214951
    @ExcelProperty(value = {"通信地点","通话地点","本机通话地","己方通话地*","漫游城市"})
    private String communicationLocation;
    @ExcelProperty(value = {"通信方式","事件类型名称","呼叫类型","呼叫类型*","协议代码","话单方向"})
    private String communicationType;
    @ExcelProperty("通信类型")
    private String businessType;
    @ExcelProperty(value = {"对方号码","被叫号码","对端号码"})
    private String oppositePhone;
    @ExcelProperty(value = {"通信时长","拨打秒数","通话时长","时长","通话时长（s）"})
    private String duration;
    @ExcelProperty(value = {"基站小区代码","蜂窝号","基站ID","己方小区","主叫小区","小区"})
    private String ci;
    @ExcelProperty(value = {"小区号","主叫基站","己方位置区","基站"})
    private String lac;
    @ExcelProperty(value = {"手机串号","串号","己方机身码","imei","IMEI","机身码"})
    private String imei;
    @ExcelProperty(value = {"IMSI","己方卡号","主叫imsi"})
    private String imsi;
    @ExcelProperty(value = {"己方名称","己方基站名称","基站信息","通话基站"})
    private String address;
}

