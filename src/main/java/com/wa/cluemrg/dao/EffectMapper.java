package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.Effect;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface EffectMapper extends MyBatisBaseDao<Effect, String> {

    int insert(Effect effect);

    int delete(int id);

    int update(Effect effect);

    List<Effect> selectAll(Effect effect);

    int batchInsert(List<Effect> list);

}