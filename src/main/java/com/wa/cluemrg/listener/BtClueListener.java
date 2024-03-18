package com.wa.cluemrg.listener;

import com.alibaba.excel.util.StringUtils;
import com.wa.cluemrg.dao.BtClueMapper;
import com.wa.cluemrg.dao.NodeTagMapper;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.NodeTag;
import com.wa.cluemrg.service.ScheduledTaskService;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 导入处理监听
 */
@Log4j2
public class BtClueListener extends CustomizeListener<BtClue> {

    BtClueMapper btClueMapper;
    NodeTagMapper nodeTagMapper;

    ThreadLocal<String> message;
    Pattern pattern = Pattern.compile("[a-fA-F]");
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public BtClueListener(Class<?> classType) {
        super(classType);
    }

    public BtClueListener(Class<?> classType, BtClueMapper btClueMapper,NodeTagMapper nodeTagMapper, ThreadLocal<String> message) {
        super(classType);
        this.btClueMapper=btClueMapper;
        this.nodeTagMapper=nodeTagMapper;
        this.message=message;
    }

    @Override
    public String dataDeal() {
        //快打没有编号
        generateClueId(list);
        //只有漫游柳州的录入
        List<BtClue> lzList = new ArrayList<>();
        for (BtClue btClue:list){
            if (btClue.getRoaming()!=null&&btClue.getRoaming().contains("柳州")){
                lzList.add(btClue);
            }
        }
        int success = btClueMapper.batchInsertOrUpdate(lzList);
        String result = "导入成功数："+(success<=list.size()?success:list.size())+" 导入失败数："+((list.size()-success)>=0?(list.size()-success):0);
        message.set(result);
        log.info("dealBtClue: "+result);
        addTag();
        addTask();
        return result;
    }

    private void addTask(){
        Set<String> phoneSet = new HashSet<>();
        for (BtClue btClue:list){
            phoneSet.add(btClue.getPhone());
        }
        //添加到定时任务
        ScheduledTaskService.waitToAdd(phoneSet);
    }

    private void addTag(){
        List<NodeTag> nodeTags = new ArrayList<>();
        for (BtClue btClue:list){
            NodeTag nodeTag1 = new NodeTag(btClue.getPhone(),btClue.getOperator());
            nodeTags.add(nodeTag1);
            if (StringUtils.isNotBlank(btClue.getJurisdiction())){
                NodeTag nodeTag2 = new NodeTag(btClue.getPhone(),btClue.getJurisdiction());
                nodeTags.add(nodeTag2);
            }
            if (StringUtils.isNotBlank(btClue.getOwnerId())&&StringUtils.isNotBlank(btClue.getOwner())){
                NodeTag nodeTag3 = new NodeTag(btClue.getOwnerId(),btClue.getOwner());
                nodeTags.add(nodeTag3);
            }
            if (StringUtils.isNotBlank(btClue.getOwnerId())&&StringUtils.isNotBlank(btClue.getOwnerAddress())){
                NodeTag nodeTag4 = new NodeTag(btClue.getOwnerId(),btClue.getOwnerAddress());
                nodeTags.add(nodeTag4);
            }
        }
        nodeTagMapper.batchInsert(nodeTags);
    }


    public static String formatSecondsToMinutesSeconds(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        if(minutes==0){
            return remainingSeconds+"秒";
        }
        return String.format("%d分%d秒", minutes, remainingSeconds);
    }

    private void generateClueId(List<BtClue> btClueList){
        for (BtClue btClue:btClueList){
            if (StringUtils.isEmpty(btClue.getClueId())&&btClue.getRoaming()!=null&&btClue.getRoaming().contains("柳州")){
                Date clueTime = btClue.getClueTime();
                if (clueTime==null){
                    clueTime = new Date();
                }
                String clueId = "KD"+simpleDateFormat.format(clueTime);
                btClue.setClueId(clueId);
            }
        }
    }

}
