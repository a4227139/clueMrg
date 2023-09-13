package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.BSLocation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface BSLocationMapper extends MyBatisBaseDao<BSLocation, String> {
    int insert(BSLocation bsLocation);

    int delete(String bsLocationId);

    int update(BSLocation bsLocation);

    List<BSLocation> selectAll(BSLocation bsLocation);

}