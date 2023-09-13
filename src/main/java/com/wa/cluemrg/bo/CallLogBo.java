package com.wa.cluemrg.bo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class CallLogBo {

    @ExcelIgnore
    private Integer id;
    @ExcelProperty(value={"用户号码","主叫号码","己方号码","业务号码"})
    private String phone;
    @ExcelProperty(value = {"起始时间","开始时间","通话起始时间","截获时间*","通话开始时间"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTimeEnd;
    @ExcelProperty(value = {"通信地点","通话地点","本机通话地","己方通话地*","漫游城市"})
    private String communicationLocation;
    @ExcelProperty(value = {"通信方式","事件类型名称","呼叫类型","协议代码","话单方向"})
    private String communicationType;
    @ExcelProperty("通信类型")
    private String businessType;
    @ExcelProperty(value = {"对方号码","被叫号码","对端号码"})
    private String oppositePhone;
    @ExcelProperty(value = {"通信时长","拨打秒数","通话时长","时长","通话时长（s）"})
    private String duration;
    @ExcelProperty(value = {"基站小区代码","蜂窝号","基站ID","己方小区","主叫小区"})
    private String ci;
    @ExcelProperty(value = {"小区号","主叫基站","己方位置区"})
    private String lac;
    @ExcelProperty(value = {"手机串号","串号","己方机身码","imei"})
    private String imei;
    @ExcelProperty(value = {"IMSI","己方卡号","主叫imsi"})
    private String imsi;
    @ExcelProperty(value = {"基站地址"})
    private String location;
    @ExcelProperty(value = {"经度"})
    private String longitude;
    @ExcelProperty(value = {"纬度"})
    private String latitude;
    @ExcelProperty(value = {"己方名称","己方基站名称","基站信息"})
    private String address;

    private int pageIndex = 1;
    private int pageSize = 10;
    private int offset;
    private String sortField;
    private String sortOrder;

}
