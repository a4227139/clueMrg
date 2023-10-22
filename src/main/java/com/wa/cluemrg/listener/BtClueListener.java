package com.wa.cluemrg.listener;

import com.alibaba.excel.util.StringUtils;
import com.wa.cluemrg.dao.BtClueMapper;
import com.wa.cluemrg.dao.NodeTagMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.NodeTag;
import com.wa.cluemrg.entity.PhoneImei;
import com.wa.cluemrg.entity.PhoneImsi;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        int success = btClueMapper.batchInsertOrUpdate(list);
        String result = "导入成功数："+(success<=list.size()?success:list.size())+" 导入失败数："+((list.size()-success)>=0?(list.size()-success):0);
        message.set(result);
        log.info("dealBtClue: "+result);
        addTag();
        return result;
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



}
