package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.PhoneImei;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface PhoneImeiMapper extends MyBatisBaseDao<PhoneImei, String> {

    int insert(PhoneImei phoneImei);

    int delete(int id);

    int update(PhoneImei phoneImei);

    List<PhoneImei> selectAll(PhoneImei phoneImei);

    int batchInsert(List<PhoneImei> list);

}