package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.AllDbQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface AllDbQueryMapper extends MyBatisBaseDao<AllDbQuery, String> {
    int insert(AllDbQuery allDbQuery);

    int delete(String applicationId);

    int update(AllDbQuery allDbQuery);

    AllDbQuery selectById(String allDbQuery);

    List<AllDbQuery> selectAll(AllDbQuery allDbQuery);

    int batchInsertOrUpdate(List<AllDbQuery> allDbQueryList);
}