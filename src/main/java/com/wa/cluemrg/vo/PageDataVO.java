package com.wa.cluemrg.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class PageDataVO<T> {
    private List<T> list;

    private int pageNum;

    private int pageSize;

    private long total;

    private int pages;

    public PageDataVO(PageInfo page) {
        this.list = page.getList();
        this.pageNum = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
    }

    PageDataVO() {
    }

}
