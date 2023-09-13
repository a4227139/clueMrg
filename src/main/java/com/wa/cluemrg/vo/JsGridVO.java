package com.wa.cluemrg.vo;

import com.github.pagehelper.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JsGridVO<T> {

    private List<T> data;

    private int pageIndex;

    private int pageSize;

    private long itemsCount;

    private int pages;

    public JsGridVO(PageInfo page) {
        this.data = page.getList();
        this.pageIndex = page.getPageNum();
        this.pageSize = page.getPageSize();
        this.itemsCount = page.getTotal();
        this.pages = page.getPages();
    }


}
