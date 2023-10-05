package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.Victim;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface VictimMapper extends MyBatisBaseDao<Victim, String> {

    int insert(Victim victim);

    int delete(int id);

    int update(Victim victim);

    List<Victim> selectAll(Victim victim);

    int batchInsert(List<Victim> list);

}