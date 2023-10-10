package com.slwer.oa.service;

import com.slwer.oa.entity.Department;
import com.slwer.oa.mapper.DepartmentMapper;
import com.slwer.oa.utils.MyBatisUtils;

public class DepartmentService {
    public Department selectById(Long departmentId) {
        return (Department) MyBatisUtils.executeQuery(sqlSession ->
                sqlSession.getMapper(DepartmentMapper.class).selectById(departmentId));
    }
}
