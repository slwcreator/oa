package com.slwer.oa.mapper;

import com.slwer.oa.entity.Node;
import com.slwer.oa.utils.MyBatisUtils;

import java.util.List;

public class RbacMapper {
    public List<Node> selectNodeByUserId(Long userId) {
        return (List) MyBatisUtils.executeQuery(sqlSession ->
                sqlSession.selectList("rbacMapper.selectNodeByUserId", userId));
    }
}
