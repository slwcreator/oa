package com.slwer.oa.service;

import com.slwer.oa.entity.Notice;
import com.slwer.oa.mapper.NoticeMapper;
import com.slwer.oa.utils.MyBatisUtils;

import java.util.List;

public class NoticeService {
    public List<Notice> getNoticeList(Long receiverId) {
        return (List<Notice>) MyBatisUtils.executeQuery(sqlSession -> {
            NoticeMapper mapper = sqlSession.getMapper(NoticeMapper.class);
            return mapper.selectByReceiverId(receiverId);
        });
    }
}
