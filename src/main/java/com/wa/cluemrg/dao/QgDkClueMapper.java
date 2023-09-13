package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.DkClue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface QgDkClueMapper extends MyBatisBaseDao<DkClue, String> {
    int insert(DkClue btClue);

    int delete(String clueId);

    int update(DkClue btClue);

    DkClue select(String clueId);

    List<DkClue> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    List<DkClue> selectAll(DkClue btClue);

    String getLatestClue();

    int batchInsert(List<DkClue> btClueList);

    int batchInsertOrUpdate(List<DkClue> btClueList);
}