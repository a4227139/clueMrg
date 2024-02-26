package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.Requisition;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface RequisitionMapper extends MyBatisBaseDao<Requisition, String> {
    int insert(Requisition requisition);

    int delete(int requisitionId);

    int update(Requisition requisition);

    Requisition selectById(int requisitionId);

    List<Requisition> selectAll(Requisition requisition);

    int batchInsertOrUpdate(List<Requisition> requisitionList);
}