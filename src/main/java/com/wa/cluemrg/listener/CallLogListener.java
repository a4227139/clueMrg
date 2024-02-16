package com.wa.cluemrg.listener;

import com.alibaba.excel.util.StringUtils;
import com.wa.cluemrg.dao.CallLogMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.CallLog;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.entity.PhoneImsi;
import com.wa.cluemrg.service.ScheduledTaskService;
import com.wa.cluemrg.util.IMEICalculator;
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
public class CallLogListener extends CustomizeListener<CallLog> {

    CallLogMapper callLogMapper;
    PhoneImeiMapper phoneImeiMapper;
    PhoneImsiMapper phoneImsiMapper;

    ThreadLocal<String> message;

    String pattern4G = "^[a-fA-F0-9]{7}$";
    String pattern5G = "^[a-fA-F0-9]{9}$";
    Pattern pattern = Pattern.compile("[a-fA-F]");

    public CallLogListener(Class<?> classType) {
        super(classType);
    }

    public CallLogListener(Class<?> classType, CallLogMapper callLogMapper, PhoneImeiMapper phoneImeiMapper, PhoneImsiMapper phoneImsiMapper, ThreadLocal<String> message) {
        super(classType);
        this.callLogMapper=callLogMapper;
        this.phoneImeiMapper=phoneImeiMapper;
        this.phoneImsiMapper=phoneImsiMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        Set<String> phoneImeiSet = new HashSet<>();
        Set<String> phoneImsiSet = new HashSet<>();
        Set<String> phoneSet = new HashSet<>();
        for (CallLog callLog:list){
            phoneSet.add(callLog.getPhone());
            //基站处理
            /*if (!StringUtils.isEmpty(callLog.getLac())){

                if (StringUtils.isEmpty(callLog.getCi())&&!pattern.matcher(callLog.getLac()).find()&&
                (callLog.getLac().matches("^\\d+$")&&Integer.parseInt(callLog.getLac())<65535)){
                    int temp=Integer.parseInt(callLog.getLac());
                    String lac = Integer.toHexString(temp);
                    callLog.setLac(lac.toUpperCase());
                }//本身已经是16进制，不用转换
                else if(pattern.matcher(callLog.getCi()).find()||pattern.matcher(callLog.getLac()).find()
                        ||(callLog.getLac().matches("^\\d+$")&&Integer.parseInt(callLog.getLac())>65535)){
                    // do noting
                }else {
                    int temp=Integer.parseInt(callLog.getLac());
                    String lac = Integer.toHexString(temp);
                    try {
                        temp = Integer.parseInt(callLog.getCi());
                        String ci = Integer.toHexString(temp);
                        callLog.setCi(ci.toUpperCase());
                        callLog.setLac(lac.toUpperCase());
                    }catch (NumberFormatException e){ //联通数字太大 例如117042241540
                        // do noting
                    }
                }
            }else */if (!StringUtils.isEmpty(callLog.getCi())&&callLog.getCi().contains("|")){
                String temp = callLog.getCi().replace("|"," ");
                String lac = temp.split("\\s")[0];
                String ci = temp.split("\\s")[1];
                callLog.setLac(lac);
                callLog.setCi(ci);
            }
            //处理通话时长，没有时分秒
            if (!StringUtils.isEmpty(callLog.getDuration())&&!callLog.getDuration().contains("时")&&!callLog.getDuration().contains("分")&&!callLog.getDuration().contains("秒")){
                int second = Integer.parseInt(callLog.getDuration());
                String duration = formatSecondsToMinutesSeconds(second);
                callLog.setDuration(duration);
            }
            //处理IMEI关系
            if (!StringUtils.isEmpty(callLog.getImei())){
                String phoneImei=callLog.getPhone()+":"+ IMEICalculator.calculateCheckDigit(callLog.getImei());
                if (phoneImei.length()>=27){//11+1+15
                    phoneImeiSet.add(phoneImei);
                }
            }
            //处理IMSI关系
            if (!StringUtils.isEmpty(callLog.getImsi())){
                String phoneImsi=callLog.getPhone()+":"+callLog.getImsi();
                phoneImsiSet.add(phoneImsi);
            }
            //处理oppositePhone
            if (!StringUtils.isEmpty(callLog.getOppositePhone())
                    &&callLog.getOppositePhone().startsWith("0086")){
                callLog.setOppositePhone(callLog.getOppositePhone().replaceFirst("0086",""));
            }
        }
        int success = callLogMapper.batchInsertCallLog(list);
        String result = "导入成功数："+success+" 导入失败数："+(list.size()-success);
        message.set(result);
        log.info("dealCallLog: "+result);
        if(phoneImeiSet.size()>0){
            log.info("dealPhoneImei: "+dealPhoneImei(phoneImeiSet));
        }
        if(phoneImsiSet.size()>0){
            log.info("dealPhoneImsi: "+dealPhoneImsi(phoneImsiSet));
        }
        //添加到定时任务
        ScheduledTaskService.waitToAdd(phoneSet);
        return result;
        /*list.forEach(item->{
            System.out.println(JSON.toJSONString(item));
        });*/
    }

    public String  dealPhoneImei(Set<String> phoneImeiSet){
        List<PhoneImei> list = new ArrayList<>();
        for(String phoneImeiString:phoneImeiSet){
            PhoneImei phoneImei = new PhoneImei();
            phoneImei.setPhone(phoneImeiString.split(":")[0]);
            phoneImei.setImei(phoneImeiString.split(":")[1]);
            phoneImei.setCreateTime(LocalDateTime.now());
            list.add(phoneImei);
        }
        int success =  phoneImeiMapper.batchInsert(list);
        String result = "导入成功数："+success+" 导入失败数："+(list.size()-success);
        return result;
    }

    public String  dealPhoneImsi(Set<String> phoneImsiSet){
        List<PhoneImsi> list = new ArrayList<>();
        for(String phoneImsiString:phoneImsiSet){
            PhoneImsi phoneImsi = new PhoneImsi();
            phoneImsi.setPhone(phoneImsiString.split(":")[0]);
            phoneImsi.setImsi(phoneImsiString.split(":")[1]);
            phoneImsi.setCreateTime(LocalDateTime.now());
            list.add(phoneImsi);
        }
        int success =  phoneImsiMapper.batchInsert(list);
        String result = "导入成功数："+success+" 导入失败数："+(list.size()-success);
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
