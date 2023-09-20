package com.slwer.oa.mapper;

import com.slwer.oa.entity.Notice;
import com.slwer.oa.utils.MyBatisUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoticeMapperTest {

    @Test
    public void insert() {
        MyBatisUtils.executeUpdate(sqlSession -> {
            NoticeMapper mapper = sqlSession.getMapper(NoticeMapper.class);
            mapper.insert(new Notice(2L, "测试消息"));
            return null;
        });
    }
}