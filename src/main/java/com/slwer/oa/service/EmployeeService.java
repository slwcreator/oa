package com.slwer.oa.service;

import com.slwer.oa.entity.Employee;
import com.slwer.oa.mapper.EmployeeMapper;
import com.slwer.oa.utils.MyBatisUtils;

public class EmployeeService {
    public Employee selectById(Long employeeId) {
        return (Employee) MyBatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            return employeeMapper.selectById(employeeId);
        });
    }
}
