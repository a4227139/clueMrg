package com.wa.cluemrg.listener;

import com.alibaba.fastjson.JSON;

/**
 * 导入处理监听
 */
public class PrintListener extends CustomizeListener {

    public PrintListener(Class<?> classType) {
        super(classType);
    }

    @Override
    public String dataDeal() {
        list.forEach(item->{
            System.out.println(JSON.toJSONString(item));
        });
        return "";
    }
}
