package com.wa.cluemrg.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.entity.CallLog;
import com.wa.cluemrg.entity.CallLogSimple;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MergeExcel {

    public static List<CallLog> callLogList = new ArrayList<>();
    public static Set<String> allPhoneImeiSet = new HashSet<>();
    public static Set<String> allPhoneImsiSet = new HashSet<>();

    public static void main(String[] args) {
        String folderPath = "C:\\Users\\Admin\\Desktop\\话单和线索\\广电";
        File folder = new File(folderPath);
        List<String> excelList = traverseFolder(folder);
        for (String fileName:excelList){
            System.out.println("deal "+fileName);
            EasyExcelFactory.read(fileName, new MergeExcelListener(CallLog.class)).headRowNumber(1).sheet().doRead();
        }

        String fileName = "C:\\Users\\Admin\\Desktop\\话单和线索\\话单\\mergeGD.xlsx";
        // 这里 需要指定写用哪个class去读，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        EasyExcel.write(fileName, CallLogSimple.class).sheet("话单").doWrite(callLogList);
        System.out.println(allPhoneImeiSet);
        System.out.println(allPhoneImsiSet);
    }

    public static List<String> traverseFolder(File folder) {
        List<String> excelList = new ArrayList<>();
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        traverseFolder(file);
                    } else {
                        if (file.getName().endsWith(".xlsx")){
                            excelList.add(file.getAbsolutePath());
                            System.out.println(file.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            System.out.println(folder.getAbsolutePath());
        }
        return excelList;
    }

}
