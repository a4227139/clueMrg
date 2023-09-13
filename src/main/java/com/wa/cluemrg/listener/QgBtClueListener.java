package com.wa.cluemrg.listener;

import com.wa.cluemrg.dao.QgBtClueMapper;
import com.wa.cluemrg.entity.BtClue;
import lombok.extern.log4j.Log4j2;

import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class QgBtClueListener extends CustomizeListener<BtClue> {

    QgBtClueMapper qgBtClueMapper;

    ThreadLocal<String> message;
    Pattern pattern = Pattern.compile("[a-fA-F]");

    public QgBtClueListener(Class<?> classType) {
        super(classType);
    }

    public QgBtClueListener(Class<?> classType, QgBtClueMapper qgBtClueMapper, ThreadLocal<String> message) {
        super(classType);
        this.qgBtClueMapper=qgBtClueMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        int success = qgBtClueMapper.batchInsertOrUpdate(list);
        String result = "导入成功数："+success+" 导入失败数："+(list.size()-success);
        message.set(result);
        log.info("dealBtClue: "+result);
        return result;
    }


    public static String formatSecondsToMinutesSeconds(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        if(minutes==0){
            return remainingSeconds+"秒";
        }
        return String.format("%d分%d秒", minutes, remainingSeconds);
    }



}
