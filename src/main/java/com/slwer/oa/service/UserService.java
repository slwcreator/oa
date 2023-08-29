package com.slwer.oa.service;

import com.slwer.oa.entity.User;
import com.slwer.oa.mapper.UserMapper;
import com.slwer.oa.service.exception.LoginException;

public class UserService {
    private UserMapper userMapper = new UserMapper();

    /**
     * 根据前台输入进行登录校验
     *
     * @param userName 前台输入的用户名
     * @param password 前台输入的密码
     * @return 校验通过后，包含对应用户数据的 User 实体类
     * @throws LoginException 用户登陆异常
     */
    public User checkLogin(String userName, String password) {
        User user = userMapper.selectByUserName(userName);
        if (user == null) {
            throw new LoginException("用户名不存在");
        }
        if (!password.equals(user.getPassword())) {
            throw new LoginException("密码错误");
        }
        return user;
    }
}
