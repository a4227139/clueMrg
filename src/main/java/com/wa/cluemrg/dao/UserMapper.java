package com.wa.cluemrg.dao;

import com.wa.cluemrg.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MessageUserDAO继承基类
 */
@Repository
public interface UserMapper extends MyBatisBaseDao<User, String> {

    int insert(User user);

    int delete(int id);

    int update(User user);

    List<User> selectAll(User user);

    User findByUsername(String username);
}