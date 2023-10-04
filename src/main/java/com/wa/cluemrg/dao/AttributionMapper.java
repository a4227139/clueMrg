package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.Attribution;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface AttributionMapper extends MyBatisBaseDao<Attribution, String> {
    int insert(Attribution attribution);

    int delete(int seq);

    int update(Attribution attribution);

    Attribution select(int seq);

    List<Attribution> selectAll(Attribution attribution);

}