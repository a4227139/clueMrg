package com.wa.cluemrg.entity;

import lombok.Data;

@Data
public class Link {
    private String id;
    private String source;
    private String target;
    private String value;
    private Label label;

    public Link(String id, String source, String target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }
}
