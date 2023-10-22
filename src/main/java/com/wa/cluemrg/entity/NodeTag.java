package com.wa.cluemrg.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NodeTag {

    private int id;
    private String node;
    private String tag;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public NodeTag(String node, String tag) {
        this.node = node;
        this.tag = tag;
        this.createTime=LocalDateTime.now();
    }
}
