package com.wa.cluemrg.entity;

import lombok.Data;

@Data
public class CaseIndex{

    int seq;
    protected String jurisdiction;
    protected String startDate;
    protected String endDate;
    protected String startDateChinese;
    protected String endDateChinese;
    protected int count;
    protected int countHistory;
    //万人发案数
    protected float countPerW;
    protected String countPerWFormat;
    protected String yCountRatio;
    protected int solveCount;
    protected float lossMoney;
    protected float lossMoneyHistory;
    protected String yLossMoneyRatio;
    protected float solveRate;
    protected String solveRateFormat;
    //单位万元
    protected float averageLossMoney;
    protected String averageLossMoneyFormat;
    protected String dRatio;
    protected String wRatio;
    protected String mRatio;
    protected String yRatio;
    //单位万元
    protected String lossMoneyFormat;
    //单位亿元
    protected String lossMoneyFormat2;
    //单位万元
    protected String lossMoneyHistoryFormat;
    //单位亿元
    protected String lossMoneyHistoryFormat2;
}
