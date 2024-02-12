package com.wa.cluemrg.entity;

import lombok.Data;

@Data
public class CaseStatSubIndex{
    // 1-十万以下 2-十万 3-五十万 4-一百万
    int level;
    int count;
    int solveCount;
    int countHistory;
    //破案率
    protected float solveRate;
    protected String solveRateFormat;
    //案件数同比
    protected float countRate;
    protected String countRateFormat;

}
