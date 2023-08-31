package com.slwer.oa.service;

import com.slwer.oa.entity.Node;
import com.slwer.oa.mapper.RbacMapper;

import java.util.List;

public class RbacService {
    private RbacMapper rbacMapper = new RbacMapper();

    public List<Node> selectNodeByUserId(Long userId) {
        return rbacMapper.selectNodeByUserId(userId);
    }
}
