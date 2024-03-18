package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.NodeTagMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.TtClueMapper;
import com.wa.cluemrg.entity.TtClue;
import com.wa.cluemrg.listener.TtClueListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class TtClueService {

    UploadUtil uploadUtil = new UploadUtil();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private TtClueMapper ttClueMapper;
    @Autowired
    private PhoneImeiMapper phoneImeiMapper;
    @Autowired
    private NodeTagMapper nodeTagMapper;

    public TtClue select(String clueId) {
        return ttClueMapper.select(clueId);
    }

    public List<TtClue> selectAll(TtClue ttClue) {
        return ttClueMapper.selectAll(ttClue);
    }

    public int insert(TtClue ttClue) {
        generateClueId(ttClue);
        //ttClue.setClueTime(simpleDateFormat2.format(new Date()));
        return ttClueMapper.insert(ttClue);
    }

    public int update(TtClue ttClue) {
        if (StringUtils.isEmpty(ttClue.getIssueTime())){
            ttClue.setIssueTime(null);
        }
        if (StringUtils.isEmpty(ttClue.getSuspectNum())){
            ttClue.setSuspectNum("0");
        }
        if (StringUtils.isEmpty(ttClue.getJzInquiryTime())){
            ttClue.setJzInquiryTime(null);
        }
        return ttClueMapper.update(ttClue);
    }

    public int delete(String clueId) {
        return ttClueMapper.delete(clueId);
    }

    public String getLatestDay() {
        String clueId = ttClueMapper.getLatestClue();
        return clueId.substring(2,6)+"-"+clueId.substring(6,8)+"-"+clueId.substring(8,10);
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new TtClueListener(TtClue.class,ttClueMapper,phoneImeiMapper,nodeTagMapper,message)).headRowNumber(1).sheet().doRead();
        return message.get();
    }

    private void generateClueId(TtClue ttClue){
        String base = simpleDateFormat.format(new Date());
        base="TT"+base+"001";
        ttClue.setClueId(base);
    }
}
