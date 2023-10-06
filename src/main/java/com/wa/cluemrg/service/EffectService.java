package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.EffectMapper;
import com.wa.cluemrg.entity.Effect;
import com.wa.cluemrg.util.DateUtil;
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
            effect.setDateStart(DateUtil.formatDateToStart(effect.getDateStart()));
        }
        if (effect.getDateEnd()!=null){
            effect.setDateEnd(DateUtil.formatDateToEnd(effect.getDateEnd()));
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

}
