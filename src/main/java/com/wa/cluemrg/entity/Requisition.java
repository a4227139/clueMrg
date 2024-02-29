package com.wa.cluemrg.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Requisition {
    private String id;
    private String cityCode;
    private String bankName;
    private Integer lockState;
    private String cardNum;
    private String[] cardNums;
    private Double transactionAmount;
    private String cityCodeLy;
    private String secrit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date lastContactTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date ecreateTime;
    private Integer state;
    private String jiechuState;
    private Integer doQzh;
    private String bankCode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date firstZfTime;
    private String applicantIp;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date nextZfTime;
    private Integer zfTime;
    private String applicant;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date insertTime;
    private String jobId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date lastZfTime;
    private String spr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createTime;
    private String phone;
    private Integer wantToQzhZf;
    private String name;
    private String approvalCityCode;
    private String idnum;
    private String gjfzFeedbackRemark;
    private String gjfzFailureCause;
    private String gjfzBalance;
    private String gjfzCreatTime;
}

