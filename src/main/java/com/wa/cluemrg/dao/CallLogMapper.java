package com.wa.cluemrg.dao;

import com.wa.cluemrg.bo.CallLogBo;
import com.wa.cluemrg.entity.CallLog;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface CallLogMapper extends MyBatisBaseDao<CallLog, String> {
    int insert(CallLog callLog);

    int delete(String callLogId);

    int update(CallLog callLog);

    CallLog selectById(String callLogId);

    List<CallLogBo> selectAll(CallLogBo callLog);

    int selectCount(CallLogBo callLog);

    List<CallLogBo> exportAll(CallLogBo callLog);

    int batchInsertCallLog(List<CallLog> callLogList);

}