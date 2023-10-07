package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class EffectIndex {

    String dateChinese;
    int detention;
    int sue;
    Map<String,Integer> detentionMap=new HashMap<>();
    Map<String,Integer> sueMap=new HashMap<>();
    String situation;
}

