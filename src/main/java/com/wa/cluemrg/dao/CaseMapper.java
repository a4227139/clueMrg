package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.Case;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface CaseMapper extends MyBatisBaseDao<Case, String> {
    int insert(Case case1);

    int delete(String caseNo);

    int batchDelete(List<String> caseNo);

    int update(Case case1);

    Case select(String caseNo);

    List<Case> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    List<Case> selectAll(Case case1);

    String getLatestCase();

    int batchInsert(List<Case> caseList);

    int batchInsertOrUpdate(List<Case> caseList);

}