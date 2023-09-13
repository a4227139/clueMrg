package com.wa.cluemrg.service;

import com.alibaba.excel.EasyExcelFactory;
import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.dao.CallLogMapper;
import com.wa.cluemrg.dao.PhoneImeiMapper;
import com.wa.cluemrg.dao.PhoneImsiMapper;
import com.wa.cluemrg.entity.CallLog;
import com.wa.cluemrg.listener.CallLogListener;
import com.wa.cluemrg.listener.PrintListener;
import com.wa.cluemrg.util.UploadUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CallLogService {

    UploadUtil uploadUtil = new UploadUtil();

    @Autowired
    private CallLogMapper callLogMapper;
    @Autowired
    private PhoneImeiMapper phoneImeiMapper;
    @Autowired
    private PhoneImsiMapper phoneImsiMapper;

    public int selectCount(CallLogBo callLog) {
        return callLogMapper.selectCount(callLog);
    }

    public List<CallLogBo> selectAll(CallLogBo callLog) {
        return callLogMapper.selectAll(callLog);
    }

    public List<CallLogBo> exportAll(CallLogBo callLog) {
        return callLogMapper.exportAll(callLog);
    }

    public int insert(CallLog callLog) {
        return callLogMapper.insert(callLog);
    }

    public int update(CallLog callLog) {
        return callLogMapper.update(callLog);
    }

    public int delete(String callLogId) {
        return callLogMapper.delete(callLogId);
    }

    public String dealUpload(MultipartFile file){
        String fileName = uploadUtil.saveFile(file);
        ThreadLocal<String> message = new ThreadLocal<>();
        EasyExcelFactory.read(fileName, new CallLogListener(CallLog.class,callLogMapper,phoneImeiMapper,phoneImsiMapper,message)).headRowNumber(1).sheet().doRead();
        return message.get();
    }

}
