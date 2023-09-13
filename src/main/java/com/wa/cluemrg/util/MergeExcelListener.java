package com.wa.cluemrg.util;

import com.alibaba.excel.util.StringUtils;
import com.wa.cluemrg.dao.CallLogMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.CallLog;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.entity.PhoneImsi;
import com.wa.cluemrg.listener.CustomizeListener;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.wa.cluemrg.util.MergeExcel.*;

/**
 * 导入处理监听
 */
@Log4j2
public class MergeExcelListener extends CustomizeListener<CallLog> {

    CallLogMapper callLogMapper;
    PhoneImeiMapper phoneImeiMapper;
    PhoneImsiMapper phoneImsiMapper;

    ThreadLocal<String> message;

    String pattern4G = "^[a-fA-F0-9]{7}$";
    String pattern5G = "^[a-fA-F0-9]{9}$";
    Pattern pattern = Pattern.compile("[a-fA-F]");

    public MergeExcelListener(Class<?> classType) {
        super(classType);
    }



    @Override
    public String dataDeal() {
        Set<String> phoneImeiSet = new HashSet<>();
        Set<String> phoneImsiSet = new HashSet<>();
        for (CallLog callLog:list){
            if (!StringUtils.isEmpty(callLog.getLac())){
                //本身已经是16进制，不用转换
                if(pattern.matcher(callLog.getCi()).find()||pattern.matcher(callLog.getLac()).find()
                        ||(callLog.getCi().matches("^\\d+$")&&Integer.parseInt(callLog.getCi())>65535)){
                    callLog.setCi(callLog.getCi()+" "+callLog.getLac());
                }else {
                    int id=Integer.parseInt(callLog.getCi());
                    String bsId = Integer.toHexString(id);
                    try {
                        int honeycombInteger = Integer.parseInt(callLog.getLac());
                        String honeycomb = Integer.toHexString(honeycombInteger);
                        bsId = bsId+" "+honeycomb;
                        callLog.setCi(bsId.toUpperCase());
                    }catch (NumberFormatException e){ //联通数字太大 例如117042241540
                        callLog.setCi(callLog.getLac());
                    }
                }
            }else if (!StringUtils.isEmpty(callLog.getCi())){
                String bsId = callLog.getCi().split("\\|")[0];
                callLog.setCi(bsId);
            }
            //处理通话时长，没有时分秒
            if (!StringUtils.isEmpty(callLog.getDuration())&&!callLog.getDuration().contains("时")&&!callLog.getDuration().contains("分")&&!callLog.getDuration().contains("秒")){
                int second = Integer.parseInt(callLog.getDuration());
                String duration = formatSecondsToMinutesSeconds(second);
                callLog.setDuration(duration);
            }
            //处理IMEI关系
            if (!StringUtils.isEmpty(callLog.getImei())){
                String phoneImei=callLog.getPhone()+":"+callLog.getImei();
                phoneImeiSet.add(phoneImei);
            }
            //处理IMSI关系
            if (!StringUtils.isEmpty(callLog.getImsi())){
                String phoneImsi=callLog.getPhone()+":"+callLog.getImsi();
                phoneImsiSet.add(phoneImsi);
            }
        }
        log.info(list);

        if(phoneImeiSet.size()>0){
            log.info(phoneImeiSet);
        }
        if(phoneImsiSet.size()>0){
            log.info(phoneImsiSet);
        }
        callLogList.addAll(list);
        allPhoneImeiSet.addAll(phoneImeiSet);
        allPhoneImsiSet.addAll(phoneImsiSet);
        return "";
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
