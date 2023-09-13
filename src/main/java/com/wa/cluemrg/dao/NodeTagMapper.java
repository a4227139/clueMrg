package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.NodeTag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface NodeTagMapper extends MyBatisBaseDao<NodeTag, String> {

    int insert(NodeTag nodeTag);

    int delete(int id);

    int update(NodeTag nodeTag);

    List<NodeTag> selectAll(NodeTag nodeTag);

    int batchInsert(List<NodeTag> list);

    List<NodeTag> selectAllByKeyList(List keyList);
}