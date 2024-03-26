package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.BtClue;
import com.wa.cluemrg.entity.BtClue;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface BtClueMapper extends MyBatisBaseDao<BtClue, String> {
    int insert(BtClue btClue);

    int delete(String clueId);

    int update(BtClue btClue);

    BtClue select(String clueId);

    List<BtClue> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    List<BtClue> selectAll(BtClue btClue);

    String getLatestClue();

    int batchInsert(List<BtClue> btClueList);

    int batchInsertOrUpdate(List<BtClue> btClueList);

    int batchInsertOrUpdate2(List<BtClue> btClueList);

}