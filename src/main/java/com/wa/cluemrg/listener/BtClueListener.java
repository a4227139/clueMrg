package com.wa.cluemrg.listener;

import com.alibaba.excel.util.StringUtils;
import com.wa.cluemrg.dao.BtClueMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.entity.PhoneImsi;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class BtClueListener extends CustomizeListener<BtClue> {

    BtClueMapper btClueMapper;

    ThreadLocal<String> message;
    Pattern pattern = Pattern.compile("[a-fA-F]");

    public BtClueListener(Class<?> classType) {
        super(classType);
    }

    public BtClueListener(Class<?> classType, BtClueMapper btClueMapper, ThreadLocal<String> message) {
        super(classType);
        this.btClueMapper=btClueMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        int success = btClueMapper.batchInsertOrUpdate(list);
        String result = "导入成功数："+(success<=list.size()?success:list.size())+" 导入失败数："+((list.size()-success)>=0?(list.size()-success):0);
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
