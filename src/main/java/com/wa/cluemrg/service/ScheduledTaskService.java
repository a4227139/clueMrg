package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.EffectMapper;
import com.wa.cluemrg.entity.Effect;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ScheduledTaskService {

    @Autowired
    EffectMapper effectMapper;
    String[] departments = new String[]{"城中分局","鱼峰分局","柳南分局","柳北分局","柳江分局","柳东分局","柳城县局","鹿寨县局","融安县局","融水县局","三江县局"};

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    public void initEffectEveryDay() {
        for (String depart:departments){
            Effect effect = new Effect();
            effect.setDate(LocalDate.now());
            effect.setDepartment(depart);
            effect.setSue(0);
            effect.setDetention(0);
            try{
                effectMapper.insert(effect);
            }catch (Exception e){
                log.info("后台任务：初始化战果表effect，插入失败："+effect);
            }
        }
    }
}