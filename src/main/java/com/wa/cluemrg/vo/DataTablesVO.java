package com.wa.cluemrg.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DataTablesVO<T> {

    private List<T> data;

    private int page;

    private int pageSize;

    private long total;

    private int pages;

    private int draw;

    private long recordsTotal;

    private long recordsFiltered;

    public DataTablesVO(PageInfo page) {
        this.data = page.getList();
        this.page = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.draw = 1;
        this.recordsTotal = page.getTotal();
        this.recordsFiltered = page.getTotal();
    }


}
