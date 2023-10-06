package com.wa.cluemrg.listener;

import com.wa.cluemrg.dao.AlarmReceiptMapper;
import com.wa.cluemrg.entity.AlarmReceipt;
import com.wa.cluemrg.util.RegexMatcher;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class AlarmReceiptListener extends CustomizeListener<AlarmReceipt> {

    AlarmReceiptMapper alarmReceiptMapper;

    ThreadLocal<String> message;
    Pattern pattern = Pattern.compile("[a-fA-F]");

    public AlarmReceiptListener(Class<?> classType) {
        super(classType);
    }

    public AlarmReceiptListener(Class<?> classType, AlarmReceiptMapper alarmReceiptMapper, ThreadLocal<String> message) {
        super(classType);
        this.alarmReceiptMapper=alarmReceiptMapper;
        this.message=message;
    }

    /*@Override
    public String dataDeal() {//导入三台合一的文件
        List<AlarmReceipt> dzList = new ArrayList<>();
        for (AlarmReceipt alarmReceipt:list){
            if (!StringUtils.isEmpty(alarmReceipt.getJqno())&&alarmReceipt.getType().contains("电信网络诈骗")){
                String[] typeArray = alarmReceipt.getType().split("/");
                if (typeArray.length>0){
                    String type = typeArray[typeArray.length-1];
                    alarmReceipt.setType(type);
                }
                if (!StringUtils.isEmpty(alarmReceipt.getJurisdiction())){
                    String[] jurisdictionArray = alarmReceipt.getJurisdiction().split("/");
                    if (jurisdictionArray.length>0){
                        String jurisdiction = jurisdictionArray[0];
                        alarmReceipt.setJurisdiction(jurisdiction);
                        if (jurisdictionArray.length>1){
                            alarmReceipt.setPcs(jurisdictionArray[1]);
                        }else {
                            alarmReceipt.setPcs("未知");
                        }
                        if (jurisdictionArray.length>2){
                            alarmReceipt.setCommunity(jurisdictionArray[2]);
                        }else {
                            alarmReceipt.setCommunity("未知");
                        }
                    }
                }
                if (StringUtils.isEmpty(alarmReceipt.getVictim())){
                    alarmReceipt.setVictim(RegexMatcher.matchNameRegex(alarmReceipt.getContent()));
                }
                if (StringUtils.isEmpty(alarmReceipt.getId())){
                    alarmReceipt.setId(RegexMatcher.matchIdRegex(alarmReceipt.getContent()));
                }
                dzList.add(alarmReceipt);
            }
        }
        int success = alarmReceiptMapper.batchInsert(dzList);
        String result = "导入成功数："+success+" 导入失败数："+(dzList.size()-success);
        message.set(result);
        log.info("dealBtClue: "+result);
        return result;
    }*/

    @Override
    public String dataDeal() {
        List<AlarmReceipt> dzList = new ArrayList<>();
        Date today = new Date();
        for (AlarmReceipt alarmReceipt:list){
            if (StringUtils.isEmpty(alarmReceipt.getContent())||alarmReceipt.getAlarmTime()==null){
                continue;
            }
            //超过一天的历史数据不收
            /*if (today.getTime()-alarmReceipt.getAlarmTime().getTime()>86400*1000){
                continue;
            }*/
            alarmReceipt.setLastReviseTime(LocalDateTime.now());
            dzList.add(alarmReceipt);
        }
        int success = alarmReceiptMapper.batchInsert(dzList);
        String result = "导入成功数："+success+" 导入失败数："+(dzList.size()-success);
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
