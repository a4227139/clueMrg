package com.wa.cluemrg.entity;

import lombok.Data;

@Data
public class CaseIndex{

    protected String jurisdiction;
    protected String startDate;
    protected String endDate;
    protected int count;
    protected int solveCount;
    protected float lossMoney;
    protected float solveRate;
    protected String solveRateFormat;
    //单位万元
    protected String averageLossMoney;
    protected String dRatio;
    protected String wRatio;
    protected String mRatio;
    protected String yRatio;
    //单位万元
    protected String lossMoneyFormat;
}
