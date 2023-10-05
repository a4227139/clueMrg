package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.VictimMapper;
import com.wa.cluemrg.entity.Victim;
import com.wa.cluemrg.listener.VictimListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class VictimService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private VictimMapper victimMapper;

    public List<Victim> selectAll(Victim victim) {
        if (victim.getTimeStart()!=null){
            victim.setTimeStart(formatDateToStart(victim.getTimeStart()));
        }
        if (victim.getTimeEnd()!=null){
            victim.setTimeEnd(formatDateToEnd(victim.getTimeEnd()));
        }
        return victimMapper.selectAll(victim);
    }

    public int insert(Victim victim) {
        return victimMapper.insert(victim);
    }

    public int update(Victim victim) {
        return victimMapper.update(victim);
    }

    public int delete(int seq) {
        return victimMapper.delete(seq);
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new VictimListener(Victim.class,victimMapper,message)).headRowNumber(2).sheet().doRead();
        return message.get();
    }

    public Date formatDateToStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        // Set the time components (hour, minute, second) to zero
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public Date formatDateToEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }
}
