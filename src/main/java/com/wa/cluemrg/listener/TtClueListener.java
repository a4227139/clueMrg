package com.wa.cluemrg.listener;

import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.TtClueMapper;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.entity.TtClue;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class TtClueListener extends CustomizeListener<TtClue> {

    TtClueMapper ttClueMapper;
    PhoneImeiMapper phoneImeiMapper;

    ThreadLocal<String> message;
    Pattern pattern = Pattern.compile("[a-fA-F]");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public TtClueListener(Class<?> classType) {
        super(classType);
    }

    public TtClueListener(Class<?> classType, TtClueMapper ttClueMapper,PhoneImeiMapper phoneImeiMapper, ThreadLocal<String> message) {
        super(classType);
        this.ttClueMapper=ttClueMapper;
        this.phoneImeiMapper=phoneImeiMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        generateClueId(list);
        int success = ttClueMapper.batchInsertOrUpdate(list);
        dealImei(list);
        String result = "导入成功数："+(success<=list.size()?success:list.size())+" 导入失败数："+((list.size()-success)>=0?(list.size()-success):0);
        message.set(result);
        log.info("dealTtClue: "+result);
        return result;
    }

    public void dealImei(List<TtClue> ttClueList){
        List<PhoneImei> list = new ArrayList<>();
        for (TtClue ttClue:ttClueList){
            if (!StringUtils.isEmpty(ttClue.getImeis())){
                String imeis = ttClue.getImeis();
                String[] imeiArray = imeis.split("\\/|-|:|：|#|\\s+");
                String phone = ttClue.getPhone();
                for (String imei : imeiArray){
                    PhoneImei phoneImei = new PhoneImei();
                    phoneImei.setPhone(phone);
                    phoneImei.setImei(imei);
                    phoneImei.setCreateTime(LocalDateTime.now());
                    list.add(phoneImei);
                }
            }
        }
        if (!CollectionUtils.isEmpty(list)){
            int success =  phoneImeiMapper.batchInsert(list);
            log.info("新增"+success+"个imei关系");
        }
    }

    private void generateClueId(List<TtClue> ttClueList){
        int size = ttClueList.size();
        String base = simpleDateFormat.format(new Date());
        base="TT"+base;
        String seq;
        for (int i=1;i<=size;i++){
            if (i<10){
                seq="00"+i;
            }else if (i<100){
                seq="0"+i;
            }else {
                seq=i+"";
            }
            if (StringUtils.isEmpty(ttClueList.get(i-1).getClueId())){
                ttClueList.get(i-1).setClueId(base+seq);
            }
        }
    }

}
