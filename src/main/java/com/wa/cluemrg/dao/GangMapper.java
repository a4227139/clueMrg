package com.wa.cluemrg.dao;

import com.wa.cluemrg.bo.GangBo;
import com.wa.cluemrg.entity.Gang;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface GangMapper extends MyBatisBaseDao<Gang, String> {

    int insert(Gang gang);

    int delete(int id);

    int update(Gang gang);

    List<Gang> selectAll(Gang gang);

    List<GangBo> exportAll(GangBo gang);

    /**
     * 应该只有一个gang
     * @param phone
     * @return
     */
    Gang selectByPhone(String phone);

    List<String> selectAllPhone();

    int batchInsert(List<Gang> list);

    int deleteAll();

    Gang get(int id);
}