package com.wa.cluemrg.service;

import com.wa.cluemrg.dao.RequisitionMapper;
import com.wa.cluemrg.entity.Requisition;
import com.wa.cluemrg.entity.SimpleIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequisitionService {

    @Autowired
    private RequisitionMapper caseMapper;

    public Requisition select(int requisitionId) {
        return caseMapper.selectById(requisitionId);
    }

    public List<Requisition> selectAll(Requisition requisition) {
        return caseMapper.selectAll(requisition);
    }

    public int insert(Requisition requisition) {
        return caseMapper.insert(requisition);
    }

    public int update(Requisition requisition) {
        return caseMapper.update(requisition);
    }

    public int delete(int requisitionId) {
        return caseMapper.delete(requisitionId);
    }

    public int batchInsertOrUpdate(List<Requisition> list) {
        return caseMapper.batchInsertOrUpdate(list);
    }

}
