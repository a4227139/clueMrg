package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.AlarmReceipt;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface AlarmReceiptMapper extends MyBatisBaseDao<AlarmReceipt, String> {
    int insert(AlarmReceipt alarmReceipt);

    int delete(String clueId);

    int update(AlarmReceipt alarmReceipt);

    AlarmReceipt select(String clueId);

    List<AlarmReceipt> selectPage(@Param("offset") int offset, @Param("limit") int limit);

    List<AlarmReceipt> selectAll(AlarmReceipt alarmReceipt);

    String getLatestDate();

    int batchInsert(List<AlarmReceipt> alarmReceiptList);

    int batchInsertOrUpdate(List<AlarmReceipt> alarmReceiptList);
}