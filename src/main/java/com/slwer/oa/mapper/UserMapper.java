package com.slwer.oa.mapper;

import com.slwer.oa.entity.User;
import com.slwer.oa.utils.MyBatisUtils;

public class UserMapper {
    public User selectByUserName(String userName) {
        return (User) MyBatisUtils.executeQuery(sqlSession ->
                sqlSession.selectOne("userMapper.selectByUserName", userName));
    }
}
