package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.BtClueMapper;
import com.wa.cluemrg.dao.NodeTagMapper;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.listener.BtClueListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class BtClueService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private BtClueMapper btClueMapper;

    @Autowired
    private NodeTagMapper nodeTagMapper;

    public BtClue select(String clueId) {
        return btClueMapper.select(clueId);
    }

    public List<BtClue> selectAll(BtClue btClue) {
        return btClueMapper.selectAll(btClue);
    }

    public int insert(BtClue btClue) {
        return btClueMapper.insert(btClue);
    }

    public int update(BtClue btClue) {
        if (StringUtils.isEmpty(btClue.getIssueTime())){
            btClue.setIssueTime(null);
        }
        if (StringUtils.isEmpty(btClue.getSuspectNum())){
            btClue.setSuspectNum("0");
        }
        if (StringUtils.isEmpty(btClue.getJzInquiryTime())){
            btClue.setJzInquiryTime(null);
        }
        return btClueMapper.update(btClue);
    }

    public int delete(String clueId) {
        return btClueMapper.delete(clueId);
    }

    public String getLatestDay() {
        String clueId = btClueMapper.getLatestClue();
        if (clueId.startsWith("X")){
            return clueId.substring(1,5)+"-"+clueId.substring(5,7)+"-"+clueId.substring(7,9);
        }else {
            return clueId.substring(2,6)+"-"+clueId.substring(6,8)+"-"+clueId.substring(8,10);
        }
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new BtClueListener(BtClue.class,btClueMapper,nodeTagMapper,message)).headRowNumber(1).sheet().doRead();
        return message.get();
    }
}
