package com.wa.cluemrg.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.wa.cluemrg.dao.QgDkClueMapper;
import com.wa.cluemrg.entity.DkClue;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class QgDkClueListener extends CustomizeListener<DkClue> {

    QgDkClueMapper qgDkClueMapper;
    ThreadLocal<String> message;
    AtomicInteger success = new AtomicInteger(0);
    AtomicInteger fail = new AtomicInteger(0);

    public QgDkClueListener(Class<?> classType) {
        super(classType);
    }

    public QgDkClueListener(Class<?> classType, QgDkClueMapper qgDkClueMapper, ThreadLocal<String> message) {
        super(classType);
        this.qgDkClueMapper=qgDkClueMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        for (DkClue clue:list){
            if (clue.getNum().length()==11&&clue.getNum().startsWith("1")){
                clue.setType("电话卡");
            }else {
                clue.setType("银行卡");
            }
        }
        int currentSuccess = qgDkClueMapper.batchInsertOrUpdate(list);
        success.addAndGet(currentSuccess);
        fail.addAndGet(list.size()-currentSuccess);
        String result = "导入成功数："+success.toString()+" 导入失败数："+ fail.toString();
        message.set(result);
        log.info("dealDkClue: "+result);
        return result;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        dataDeal();
        success.set(0);
        fail.set(0);
        log.debug("断卡数据导入完成");
    }

}
