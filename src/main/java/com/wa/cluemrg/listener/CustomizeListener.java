package com.wa.cluemrg.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.wa.cluemrg.util.EasyExcelParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入处理监听
 */
public class CustomizeListener<T> extends AnalysisEventListener<Map<Integer, String>> {

    private static final int BATCH_COUNT = 1000;
    private Map<String, Integer> fieldValue = new HashMap<>();
    public List<T> list = new ArrayList<>();
    public Class<?> classType;

    public CustomizeListener(Class<?> classType) {
        this.classType = classType;
    }

    public CustomizeListener() {
    }

    /**
     * 数据表头获取，表头位置对应
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        fieldValue.putAll(EasyExcelParsing.fieldValueSet(headMap, classType));
        super.invokeHeadMap(headMap, context);
    }

    /**
     * 数据一条一条解析
     *
     * @param data
     * @param analysisContext
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext analysisContext) {
        if (fieldValue.isEmpty()) {
            throw new ExcelAnalysisException("模板错误");
        }

        T obj = null;
        try {
            obj = (T)classType.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        EasyExcelParsing.setFieldValue(data, fieldValue, obj);
        list.add(obj);
        if (list.size() >= BATCH_COUNT) {
            dataDeal();
            list.clear();
        }

    }

    public String dataDeal() {
        return null;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        dataDeal();
    }

}
