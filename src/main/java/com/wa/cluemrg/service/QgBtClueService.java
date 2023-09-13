package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.QgBtClueMapper;
import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.listener.QgBtClueListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class QgBtClueService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private QgBtClueMapper qgBtClueMapper;

    public List<BtClue> selectAll(BtClue btClue) {
        return qgBtClueMapper.selectAll(btClue);
    }

    public int insert(BtClue btClue) {
        return qgBtClueMapper.insert(btClue);
    }

    public int update(BtClue btClue) {
        return qgBtClueMapper.update(btClue);
    }

    public int delete(String clueId) {
        return qgBtClueMapper.delete(clueId);
    }

    public String getLatestDay() {
        String clueId = qgBtClueMapper.getLatestClue();
        if (clueId.startsWith("X")){
            return clueId.substring(1,5)+"-"+clueId.substring(5,7)+"-"+clueId.substring(7,9);
        }else {
            return clueId.substring(2,6)+"-"+clueId.substring(6,8)+"-"+clueId.substring(8,10);
        }
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new QgBtClueListener(BtClue.class,qgBtClueMapper,message)).headRowNumber(2).doReadAll();
        return message.get();
    }
}
