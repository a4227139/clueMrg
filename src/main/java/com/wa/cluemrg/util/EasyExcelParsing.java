package com.wa.cluemrg.util;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * easyExcel支持表头多名称解析
 */
public class EasyExcelParsing {

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static Calendar calendar = Calendar.getInstance();
    static int year = calendar.get(Calendar.YEAR);
    /**
     * 字段值赋值
     *
     * @param valueMap   值对应所在位置
     * @param obj        实体类
     * @param fieldValue 表头对应所在位置
     */
    public static void setFieldValue(Map<Integer, String> valueMap, Map<String, Integer> fieldValue, Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            //遍历每个属性
            if (field.isAnnotationPresent(ExcelProperty.class) && fieldValue.containsKey(field.getName())) {
                field.setAccessible(true);
                try {
                    if(field.getName().toLowerCase().contains("time")||field.getName().toLowerCase().contains("date")){
                        String dataString = valueMap.get(fieldValue.get(field.getName()));
                        dataString=TurnToUniDateFormat(dataString);
                        if (!StringUtils.isEmpty(dataString)&&!dataString.equals("\"\"")){
                            if (field.getType().equals(Date.class)){
                                Date date = simpleDateFormat.parse(dataString);
                                field.set(obj, date);
                                continue;
                            }else if(field.getType().equals(String.class)){
                                field.set(obj, dataString);
                                continue;
                            }
                        }
                    }
                    String fieldName = field.getName();
                    Integer index = fieldValue.get(field.getName());
                    String value = valueMap.get(index);
                    if (field.getType()==int.class||field.getType()==Integer.class){
                        field.set(obj, Integer.parseInt(valueMap.get(fieldValue.get(field.getName()))));
                    }else {
                        field.set(obj, valueMap.get(fieldValue.get(field.getName())));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String TurnToUniDateFormat(String dataString) {
        if (StringUtils.isEmpty(dataString)){
            return "";
        }
        String[] dateArray = dataString.split("\\/|-|:|：|\\s+");
        StringBuilder dateBuilder = new StringBuilder();
        if (dateArray.length==5){
            //2023-08-01 17:44
            if (dataString.startsWith("20")&&dataString.length()==16){
                dateBuilder.append(fillZero(dateArray[0])).append("-").append(fillZero(dateArray[1])).append("-").append(fillZero(dateArray[2])).append(" ")
                        .append(fillZero(dateArray[3])).append(":").append(fillZero(dateArray[4])).append(":").append("00");
            }else {// 08-01 17:44:01
                dateBuilder.append(year).append("-").append(fillZero(dateArray[0])).append("-").append(fillZero(dateArray[1])).append(" ")
                        .append(fillZero(dateArray[2])).append(":").append(fillZero(dateArray[3])).append(":").append(fillZero(dateArray[4]));
            }
        }else if (dateArray.length==6){
            dateBuilder.append(fillZero(dateArray[0])).append("-").append(fillZero(dateArray[1])).append("-").append(fillZero(dateArray[2])).append(" ")
                    .append(fillZero(dateArray[3])).append(":").append(fillZero(dateArray[4])).append(":").append(fillZero(dateArray[5]));
        }else {
            return "";
        }
        return dateBuilder.toString();
    }

    private static String fillZero(String s){
        if (s.length()<2){
            return 0+s;
        }
        return s;
    }


    /**
     * 根据表头获取列所在位置
     *
     * @param headMap 表头map
     * @param cla     对应解析类
     * @return
     */
    public static Map<String, Integer> fieldValueSet(Map<Integer, String> headMap, Class<?> cla) {
        Map<String, Integer> fieldValue = new HashMap<>();
        Field[] fields = cla.getDeclaredFields();
        for (Field field : fields) {
            //遍历每个属性
            if (field.isAnnotationPresent(ExcelProperty.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                for (Map.Entry<Integer, String> entry : headMap.entrySet()) {
                    if (Arrays.asList(excelProperty.value()).contains(entry.getValue())) {
                        fieldValue.put(field.getName(), entry.getKey());
                    }
                }
            }
        }
        return fieldValue;
    }
}