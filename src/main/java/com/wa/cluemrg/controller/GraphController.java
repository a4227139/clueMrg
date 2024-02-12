package com.wa.cluemrg.controller;

import com.wa.cluemrg.entity.*;
import com.wa.cluemrg.service.*;
import com.wa.cluemrg.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/graph")
@Secured("ROLE_LEVEL1")
public class GraphController {

    @Autowired
    AlarmReceiptService alarmReceiptService;
    @Autowired
    CaseService caseService;

    @GetMapping("/getAlarmReceiptTypeIndex")
    public List<SimpleIndex> getAlarmReceiptTypeIndex(@RequestParam("dateStart")  String dateStart,
                                                      @RequestParam("dateEnd") String dateEnd,
                                                      @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (org.springframework.util.StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认全年
        if (org.springframework.util.StringUtils.isEmpty(dateStart)){
            dateStart="2024-01-01";
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> alarmReceiptIndexList = alarmReceiptService.getAlarmReceiptCountByType(alarmReceipt);
        return alarmReceiptIndexList;
    }

    @GetMapping("/getAlarmReceiptCommunityIndex")
    public List<SimpleIndex> getAlarmReceiptCommunityIndex(@RequestParam("dateStart")  String dateStart,
                                                           @RequestParam("dateEnd") String dateEnd,
                                                           @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (org.springframework.util.StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认全年
        if (StringUtils.isEmpty(dateStart)){
            dateStart="2024-01-01";
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> alarmReceiptIndexList = alarmReceiptService.getAlarmReceiptCountByCommunity(alarmReceipt);
        return alarmReceiptIndexList;
    }

    @GetMapping("/getCaseLineIndex")
    public Map<String,List<SimpleIndex>> getCaseLineIndex(@RequestParam("dateStart")  String dateStart,
                                                          @RequestParam("dateEnd") String dateEnd,
                                                          @RequestParam(value = "jurisdiction",required = false) String jurisdiction) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (StringUtils.isEmpty(dateEnd)){
            dateEnd= LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        //默认一个月
        if (StringUtils.isEmpty(dateStart)){
            LocalDate date = LocalDate.parse(dateEnd);
            LocalDate oneMonthAgo = date.minusMonths(1);
            dateStart = oneMonthAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        Date start = dateFormat.parse(dateStart);
        Date end = dateFormat.parse(dateEnd);
        Case caseObj = new Case();
        caseObj.setRegisterDateStart(start);
        caseObj.setRegisterDateEnd(end);
        Case solveCase = new Case();
        solveCase.setSolveDateStart(start);
        solveCase.setSolveDateEnd(end);
        AlarmReceipt alarmReceipt = new AlarmReceipt();
        alarmReceipt.setAlarmTimeStart(start);
        alarmReceipt.setAlarmTimeEnd(end);
        List<SimpleIndex> caseIndexList = addZeroSimpleIndex(caseService.getCaseCountByDate(caseObj),dateStart,dateEnd);
        List<SimpleIndex> caseSolveIndexList = addZeroSimpleIndex(caseService.getCaseSolveCountByDate(solveCase),dateStart,dateEnd);
        List<SimpleIndex> alarmReceiptIndexList = addZeroSimpleIndex(alarmReceiptService.getAlarmReceiptCountByDate(alarmReceipt),dateStart,dateEnd);
        Map<String,List<SimpleIndex>> index = new HashMap<>();
        index.put("caseIndexList",caseIndexList);
        index.put("caseSolveIndexList",caseSolveIndexList);
        index.put("alarmReceiptIndexList",alarmReceiptIndexList);
        return index;
    }

    private List<SimpleIndex> addZeroSimpleIndex(List<SimpleIndex> list,String dateStart,String dateEnd){
        List<LocalDate> dateList = DateUtil.getDateRange(dateStart,dateEnd);
        List<LocalDate> zeroDateList=new ArrayList<>();
        List<SimpleIndex> zeroSimpleIndexList = new ArrayList<>();
        for (LocalDate date:dateList){
            boolean exist=false;
            for (SimpleIndex index:list){
                if (index.getDate().equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
                    exist=true;
                    break;
                }
            }
            if (!exist){
                zeroDateList.add(date);
            }
        }
        for (LocalDate date:zeroDateList){
            SimpleIndex simpleIndex = new SimpleIndex();
            simpleIndex.setDate(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            simpleIndex.setCount(0);
            list.add(simpleIndex);
        }
        // 创建一个自定义的Comparator，按照破案率升序排序
        // 使用Collections.sort()对列表进行排序
        Collections.sort(list, new StringFieldComparator());
        return list;
    }
}

class StringFieldComparator implements Comparator<SimpleIndex> {
    @Override
    public int compare(SimpleIndex obj1, SimpleIndex obj2) {
        // 按照 name 字段的字母顺序进行比较
        return obj1.getDate().compareTo(obj2.getDate());
    }
}
