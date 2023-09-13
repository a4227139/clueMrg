package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.PhoneTag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface PhoneTagMapper extends MyBatisBaseDao<PhoneTag, String> {

    int insert(PhoneTag phoneTag);

    int delete(int id);

    int update(PhoneTag phoneTag);

    List<PhoneTag> selectAll(PhoneTag phoneTag);

    int batchInsert(List<PhoneTag> list);

    List<PhoneTag> selectAllByKeyList(List keyList);
}