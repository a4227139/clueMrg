package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.EffectMapper;
import com.wa.cluemrg.entity.Effect;
import com.wa.cluemrg.entity.Gang;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Log4j2
public class ScheduledTaskService {

    @Autowired
    EffectMapper effectMapper;
    String[] departments = new String[]{"城中分局","鱼峰分局","柳南分局","柳北分局","柳江分局","柳东分局","柳城县局","鹿寨县局","融安县局","融水县局","三江县局"};

    @Autowired
    GangService gangService;

    static List<String> waitToAdd = new ArrayList<>();

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

    /*@Scheduled(cron = "0 0 3 * * *") // 3 o'clock 0 0 3 * * *
    public void findAllGang() {
        int id=1;
        List<Gang> gangList = new ArrayList<>();
        List<String> phoneList = gangService.selectAllPhone();
        Set<String> visited = new HashSet<>();
        for (String phone : phoneList){
            if (visited.contains(phone)){
                continue;
            }
            Gang gang = gangService.genarateGang(phone);
            visited.addAll(gang.getPhoneList());
            if (gang.getPhoneList().size()>2){
                gang.setId(id);
                gangList.add(gang);
                gang.setLastReviseTime(new Date());
                id++;
            }
        }
        gangService.deleteAll();
        gangService.batchInsert(gangList);
    }*/

    @Scheduled(cron = "0 0 3 * * *") // 3 o'clock
    public void findAllGang() {
        List<String> phoneList = gangService.selectAllPhone();
        for (String phone : phoneList){
            waitToAdd.add(phone);
        }
    }

    public static void waitToAdd(String element){
        waitToAdd.add(element);
    }

    public static void waitToAdd(Collection<String> elements){
        waitToAdd.addAll(elements);
    }

    @Scheduled(cron = "*/10 * * * * *") // 10 seconds
    public void addElementToGang() {
        Set<String> added = new HashSet<>();
        List<Gang> insertList = new ArrayList<>();
        for (String element:waitToAdd){
            if (added.contains(element)){
                continue;
            }
            Gang gang = gangService.genarateGang(element);
            Set<String> phoneSet = new HashSet<>();
            phoneSet.addAll(gang.getPhoneList());
            added.addAll(gang.getPhoneList());
            Set<Gang> originalGangSet = new HashSet<>();
            for (String phone:phoneSet){
                if (StringUtils.isEmpty(phone)){
                    continue;
                }
                Gang originalGang = gangService.selectByPhone(phone);
                if (originalGang!=null){
                    originalGangSet.add(originalGang);
                }
            }
            for (Gang originalGang:originalGangSet){
                Set<String> originalPhoneSet = new HashSet<>();
                originalPhoneSet.addAll(originalGang.getPhoneList());
                //判断是否子集，是则更新即可
                if (phoneSet.containsAll(originalPhoneSet)){
                    gang.setId(originalGang.getId());
                    gang.setLastReviseTime(LocalDateTime.now());
                    gang.setName(originalGang.getName());
                    gang.setJurisdiction(originalGang.getJurisdiction());
                    gang.setIssueTime(originalGang.getIssueTime());
                    gangService.update(gang);
                    break;
                }else {//得到phone差集，删除gang，重新生成新gang
                    Set<String> difference = new HashSet<>(originalPhoneSet);
                    difference.removeAll(phoneSet);
                    waitToAdd.addAll(difference);
                    gangService.delete(originalGang.getId());
                }
            }
            if (originalGangSet.isEmpty()&&gang.getPhoneList().size()>=2){
                gang.setLastReviseTime(LocalDateTime.now());
                insertList.add(gang);
            }
        }
        if (!insertList.isEmpty()){
            gangService.batchInsert(insertList);
        }
        waitToAdd.clear();
    }

}