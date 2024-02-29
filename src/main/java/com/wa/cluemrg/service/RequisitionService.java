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
    private RequisitionMapper requisitionMapper;

    public Requisition select(int requisitionId) {
        return requisitionMapper.selectById(requisitionId);
    }

    public List<Requisition> selectAll(Requisition requisition) {
        return requisitionMapper.selectAll(requisition);
    }

    public List<Requisition> selectAllDbQuery(List<String> list) {
        return requisitionMapper.selectAllDbQuery(list);
    }

    public int insert(Requisition requisition) {
        return requisitionMapper.insert(requisition);
    }

    public int update(Requisition requisition) {
        return requisitionMapper.update(requisition);
    }

    public int delete(int requisitionId) {
        return requisitionMapper.delete(requisitionId);
    }

    public int batchInsertOrUpdate(List<Requisition> list) {
        return requisitionMapper.batchInsertOrUpdate(list);
    }

}
