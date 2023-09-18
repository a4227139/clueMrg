package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AlarmReceiptIndex {
    protected String dateChinese;
    protected Date date;
    protected int count;
    protected float countRate;
    protected String countRateFormat;
    protected int registerCount;
    protected int solveCount;
    protected float lossMoney;
    //单位万元
    protected String lossMoneyFormat;
    protected float lossMoneyRate;
    protected String lossMoneyRateFormat;
    float stopPaymentMoney;
    protected String stopPaymentMoneyFormat;
    protected int stopPaymentAccount;

}
