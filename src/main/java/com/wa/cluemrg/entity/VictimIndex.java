package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class VictimIndex {

    String dateChinese;
    String employerSituation;
    String genderSituation;
    String ageSituation;
    String typeSituation;
}

