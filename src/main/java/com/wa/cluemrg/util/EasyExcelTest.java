package com.wa.cluemrg.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.CallLog;
import com.wa.cluemrg.listener.CallLogListener;
import com.wa.cluemrg.listener.PrintListener;
import lombok.extern.log4j.Log4j2;
import com.alibaba.excel.ExcelReader;

@Log4j2
public class EasyExcelTest {

    public static void main(String[] args) {
        EasyExcelTest test = new EasyExcelTest();
        //test.simpleRead();
        test.repeatedRead();
    }

    /**
     * 最简单的读
     * <p>
     * 1. 创建excel对应的实体对象 参照{@link CallLog}
     * <p>
     * 2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link CallLogListener}
     * <p>
     * 3. 直接读即可
     */
    public void simpleRead() {
        // 写法1：JDK8+ ,不用额外写一个CallLogListener
        // since: 3.0.0-beta1
        String fileName = "";
        /*fileName="E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\72011374-202303161759192053078127-13457208620.xlsx";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
        EasyExcel.read(fileName, CallLog.class, new PageReadListener<CallLog>(dataList -> {
            for (CallLog demoData : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(demoData));
            }
        })).sheet().doRead();*/

        // 写法2：
        // 匿名内部类 不用额外写一个CallLogListener
        /*fileName = "E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\72011374-202303161759192053078127-13457208620.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, CallLog.class, new ReadListener<CallLog>() {
            *//**
         * 单次缓存的数据量
         *//*
            public static final int BATCH_COUNT = 100;
            *//**
         *临时存储
         *//*
            private List<CallLog> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

            @Override
            public void invoke(CallLog data, AnalysisContext context) {
                cachedDataList.add(data);
                log.info("读取到一条数据{}", JSON.toJSONString(data));
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    // 存储完成清理 list
                    cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                saveData();
            }

            *//**
         * 加上存储数据库
         *//*
            private void saveData() {
                log.info("{}条数据，开始存储数据库！", cachedDataList.size());
                log.info("存储数据库成功！");
            }
        }).sheet().doRead();*/

        /*// 有个很重要的点 CallLogListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
        // 写法3：
        fileName = "E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\72011374-202303161759192053078127-13457208620.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, CallLog.class, new CallLogListener()).sheet().doRead();

        // 写法4
        fileName = "E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\72011374-202303161759192053078127-13457208620.xlsx";
        // 一个文件一个reader
        try (ExcelReader excelReader = EasyExcel.read(fileName, CallLog.class, new CallLogListener()).build()) {
            // 构建一个sheet 这里可以指定名字或者no
            ReadSheet readSheet = EasyExcel.readSheet(0).build();
            // 读取一个sheet
            excelReader.read(readSheet);
        }*/

        fileName = "E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-05-03\\72011374-202303161759192053078127-13457208620.xlsx";
        EasyExcelFactory.read(fileName, new PrintListener(CallLog.class)).headRowNumber(1).sheet().doRead();
    }


    public void repeatedRead() {
        String fileName = "E:\\Git\\clue-mrg\\src\\main\\resources\\static\\excel\\2023-08-07\\2023年08月07日公安部打击治理电信网络新型违法犯罪专项行动办公室涉案线索核查表.xlsx";
        //可用
        //EasyExcelFactory.read(fileName, new PrintListener(BtClue.class)).headRowNumber(2).sheet().doRead();
        EasyExcelFactory.read(fileName, new PrintListener(BtClue.class)).headRowNumber(2).doReadAll();
        // 读取全部sheet
        // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
        //EasyExcel.read(fileName, BtClue.class, new PrintListener(BtClue.class)).headRowNumber(2).doReadAll();
        //EasyExcelFactory.read(fileName, new PrintListener(BtClue.class)).headRowNumber(2).doReadAll();
        // 写法1
        /*try (ExcelReader excelReader = EasyExcel.read(fileName).build()) {
            // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
            ReadSheet readSheet1 =
                    EasyExcel.readSheet(0).headRowNumber(1).registerReadListener(new PrintListener(BtClue.class)).build();
            ReadSheet readSheet2 =
                    EasyExcel.readSheet(1).headRowNumber(2).registerReadListener(new PrintListener(BtClue.class)).build();
            // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
            excelReader.read(readSheet1,readSheet2);
        }*/
    }
}
