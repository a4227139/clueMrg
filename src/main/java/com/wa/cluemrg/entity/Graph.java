package com.wa.cluemrg.entity;

import lombok.Data;

import java.util.List;

@Data
public class Graph {

    List<Node> nodes;
    List<Link> links;
}
