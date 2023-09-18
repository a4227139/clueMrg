package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.AlarmReceiptMapper;
import com.wa.cluemrg.entity.AlarmReceipt;
import com.wa.cluemrg.entity.AlarmReceiptIndex;
import com.wa.cluemrg.entity.SimpleIndex;
import com.wa.cluemrg.listener.AlarmReceiptListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AlarmReceiptService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private AlarmReceiptMapper alarmReceiptMapper;

    public List<AlarmReceipt> selectAll(AlarmReceipt alarmReceipt) {
        return alarmReceiptMapper.selectAll(alarmReceipt);
    }

    public int insert(AlarmReceipt alarmReceipt) {
        return alarmReceiptMapper.insert(alarmReceipt);
    }

    public int update(AlarmReceipt alarmReceipt) {
        return alarmReceiptMapper.update(alarmReceipt);
    }

    public int delete(String clueId) {
        return alarmReceiptMapper.delete(clueId);
    }

    public String getLatestDay() {
        String latestDay = alarmReceiptMapper.getLatestDate();
        return latestDay;
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new AlarmReceiptListener(AlarmReceipt.class,alarmReceiptMapper,message)).headRowNumber(2).doReadAll();
        return message.get();
    }

    public List<SimpleIndex> getAlarmReceiptCountByDate(AlarmReceipt alarmReceipt){
        return alarmReceiptMapper.getAlarmReceiptCountByDate(alarmReceipt);
    }

    public List<SimpleIndex> getAlarmReceiptCountByType(AlarmReceipt alarmReceipt){
        return alarmReceiptMapper.getAlarmReceiptCountByType(alarmReceipt);
    }

    public List<SimpleIndex> getAlarmReceiptCountByCommunity(AlarmReceipt alarmReceipt){
        return alarmReceiptMapper.getAlarmReceiptCountByCommunity(alarmReceipt);
    }

    public List<AlarmReceiptIndex> getAlarmReceiptIndex(AlarmReceipt alarmReceipt){
        return alarmReceiptMapper.getAlarmReceiptIndex(alarmReceipt);
    }
}
