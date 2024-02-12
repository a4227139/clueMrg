package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CaseStatIndex {

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
    //破案数
    protected int solveCount;
    //损失金额
    protected float lossMoney;
    protected String lossMoneyFormat;
    //历史金额
    protected float lossMoneyHistory;
    //金额同比
    protected String lossMoneyRatio;
    //破案率
    protected float solveRate;
    protected String solveRateFormat;
    protected float solveRateRatio;

    protected List<CaseStatSubIndex> caseStatSubList = new ArrayList<>();

}
