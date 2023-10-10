package com.slwer.oa.service;

import com.slwer.oa.entity.Employee;
import com.slwer.oa.mapper.EmployeeMapper;
import com.slwer.oa.utils.MyBatisUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeService {
    public Employee selectById(Long employeeId) {
        return (Employee) MyBatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            return employeeMapper.selectById(employeeId);
        });
    }

    public Employee selectLeader(Long employeeId) {
        return (Employee) MyBatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(employeeId);
            Integer level = employee.getLevel();
            Map<String, Object> params = new HashMap<>();
            Employee leader = null;
            if (level < 7) {
                // 查询部门经理
                params.put("level", 7);
                params.put("departmentId", employee.getDepartmentId());
                List<Employee> employees = employeeMapper.selectByParams(params);
                leader = employees.get(0);
            } else if (level == 7) {
                // 查询总经理
                params.put("level", 8);
                List<Employee> employees = employeeMapper.selectByParams(params);
                leader = employees.get(0);
            } else if (level == 8) {
                leader = employee;
            }
            return leader;
        });
    }
}
