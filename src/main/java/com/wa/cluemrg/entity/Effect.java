package com.wa.cluemrg.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class Effect {

    private Integer seq;
    private String department;
    private LocalDate date;
    private Integer detention;
    private Integer sue;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dateEnd;

    @Override
    public String toString() {
        return "Effect{" +
                "department='" + department + '\'' +
                ", date=" + date +
                ", detention=" + detention +
                ", sue=" + sue +
                '}';
    }
}

