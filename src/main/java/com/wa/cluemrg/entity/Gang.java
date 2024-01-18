package com.wa.cluemrg.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Gang {
    int id;
    String name;
    String phone;
    String imei;
    String person;
    String clue;
    String caseNo;
    String jurisdiction;
    LocalDateTime issueTime;
    LocalDateTime lastReviseTime;


    List<String> phoneList = new ArrayList<>();
    List<String> imeiList = new ArrayList<>();
    List<String> personList = new ArrayList<>();
    List<String> clueList = new ArrayList<>();
    List<String> caseNoList = new ArrayList<>();

    public void init(){
        phone=String.join(", ", phoneList);
        imei=String.join(", ", imeiList);
        person=String.join(", ", personList);
        clue=String.join(", ", clueList);
        caseNo=String.join(", ", caseNoList);
        lastReviseTime=LocalDateTime.now();
    }
}
