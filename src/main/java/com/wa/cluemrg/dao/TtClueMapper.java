package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.TtClue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface TtClueMapper extends MyBatisBaseDao<TtClue, String> {
    int insert(TtClue ttClue);

    int delete(String clueId);

    int update(TtClue ttClue);

    TtClue select(String clueId);

    List<TtClue> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    List<TtClue> selectAll(TtClue ttClue);

    String getLatestClue();

    int batchInsert(List<TtClue> ttClueList);

    int batchInsertOrUpdate(List<TtClue> ttClueList);
}