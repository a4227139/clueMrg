package com.wa.cluemrg.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Case {
    protected String seq;
    @ExcelProperty(value={"案件编号"})
    protected String caseNo;
    @ExcelProperty(value={"案件名称"})
    protected String caseName;
    @ExcelProperty(value={"立案单位"})
    protected String caseUnit;
    @ExcelProperty(value={"立案时间"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date registerDate;
    @ExcelProperty(value={"辖区"})
    protected String jurisdiction;
    @ExcelProperty(value={"主办单位"})
    protected String organizer;
    @ExcelProperty(value={"破案时间"})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected Date solveDate;
    @ExcelProperty(value={"案别"})
    protected String type;
    @ExcelProperty(value={"专案标识"})
    protected String projectId;
    @ExcelProperty(value={"主办人"})
    protected String organiser;
    @ExcelProperty(value={"协办人"})
    protected String coOrganiser;
    @ExcelProperty(value={"损失金额"})
    protected float money;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date registerDateStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date registerDateEnd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date solveDateStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date solveDateEnd;
}
