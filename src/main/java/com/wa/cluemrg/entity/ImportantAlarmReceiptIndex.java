package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.List;

@Data
public class ImportantAlarmReceiptIndex {
    String department;
    List<AlarmReceipt> list;
}

