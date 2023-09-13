package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.PhoneImsi;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface PhoneImsiMapper extends MyBatisBaseDao<PhoneImsi, String> {

    int insert(PhoneImsi phoneImsi);

    int delete(int id);

    int update(PhoneImsi phoneImsi);

    List<PhoneImsi> selectAll(PhoneImsi phoneImsi);

    int batchInsert(List<PhoneImsi> list);

}