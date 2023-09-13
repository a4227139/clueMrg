package com.wa.cluemrg.entity;

import lombok.Data;

@Data
public class Node {
    String id;
    String name;
    String value;
    String symbol;
    double symbolSize;
    int category;
    double x;
    double y;
    int level;

    public Node(String id, String name, String value, String symbol, double x, double y,int category,int level) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.symbol = symbol;
        this.x = x;
        this.y = y;
        this.category=category;
        this.level=level;
    }
}
