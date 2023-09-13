package com.wa.cluemrg.bo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Null;

@Getter
@Setter
public class PageBO<T> {

    private T data;

    private String sortField;

    private String sortOrder;

    private int pageIndex = 1;

    private int pageSize = 10;
}
