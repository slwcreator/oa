package com.slwer.oa.utils;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResponseUtilsTest {

    @Test
    public void put() {
        ResponseUtils responseUtils = new ResponseUtils("LoginException","密码错误").put("info","test");
        System.out.println(responseUtils.toJsonString());
    }
}