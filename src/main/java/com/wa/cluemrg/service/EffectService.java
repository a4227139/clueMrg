package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.EffectMapper;
import com.wa.cluemrg.entity.Effect;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class EffectService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private EffectMapper effectMapper;

    public List<Effect> selectAll(Effect effect) {
        if (effect.getDateStart()!=null){
            effect.setDateStart(formatDateToStart(effect.getDateStart()));
        }
        if (effect.getDateEnd()!=null){
            effect.setDateEnd(formatDateToEnd(effect.getDateEnd()));
        }
        return effectMapper.selectAll(effect);
    }

    public int insert(Effect effect) {
        return effectMapper.insert(effect);
    }

    public int update(Effect effect) {
        return effectMapper.update(effect);
    }

    public int delete(int seq) {
        return effectMapper.delete(seq);
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
