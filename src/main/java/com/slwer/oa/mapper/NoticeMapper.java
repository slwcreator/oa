package com.slwer.oa.mapper;

import com.slwer.oa.entity.Notice;

import java.util.List;

public interface NoticeMapper {
    void insert(Notice notice);

    List<Notice> selectByReceiverId(Long receiverId);
}
