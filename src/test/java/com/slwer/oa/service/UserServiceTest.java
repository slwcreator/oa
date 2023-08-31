package com.slwer.oa.service;

import com.slwer.oa.entity.User;
import org.junit.Test;

public class UserServiceTest {
    private UserService userService = new UserService();

    @Test
    public void checkLogin() {
        User user = userService.checkLogin("m8", "test");
        System.out.println(user);
    }
}