package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.dao.QgDkClueMapper;
import com.wa.cluemrg.entity.DkClue;
import com.wa.cluemrg.listener.QgDkClueListener;
import com.wa.cluemrg.util.UploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class QgDkClueService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private QgDkClueMapper qgDkClueMapper;

    public List<DkClue> selectAll(DkClue btClue) {
        return qgDkClueMapper.selectAll(btClue);
    }

    public int insert(DkClue btClue) {
        return qgDkClueMapper.insert(btClue);
    }

    public int update(DkClue btClue) {
        return qgDkClueMapper.update(btClue);
    }

    public int delete(String clueId) {
        return qgDkClueMapper.delete(clueId);
    }

    public String getLatestDay() {
        String clueId = qgDkClueMapper.getLatestClue();
        if (clueId.startsWith("X")){
            return clueId.substring(1,5)+"-"+clueId.substring(5,7)+"-"+clueId.substring(7,9);
        }else {
            return clueId.substring(2,6)+"-"+clueId.substring(6,8)+"-"+clueId.substring(8,10);
        }
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new QgDkClueListener(DkClue.class,qgDkClueMapper,message)).headRowNumber(1).doReadAll();
        return message.get();
    }
}
