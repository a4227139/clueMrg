package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CaseStatIndex {
    String department;
    int total;
    int solveCount;
    float lossMoney;
    int capture;
    Date start;
    Date end;
}
