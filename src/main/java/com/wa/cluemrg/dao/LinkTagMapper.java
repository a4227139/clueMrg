package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.LinkTag;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface LinkTagMapper extends MyBatisBaseDao<LinkTag, String> {

    int insert(LinkTag linkTag);

    int delete(int id);

    int update(LinkTag linkTag);

    List<LinkTag> selectAll(LinkTag linkTag);

    List<LinkTag> selectAllByLinkList(List keyList);
}