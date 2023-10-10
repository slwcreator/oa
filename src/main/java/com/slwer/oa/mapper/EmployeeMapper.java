package com.slwer.oa.mapper;

import com.slwer.oa.entity.Employee;

import java.util.List;
import java.util.Map;

public interface EmployeeMapper {
    Employee selectById(Long employeeId);

    List<Employee> selectByParams(Map<String, Object> params);
}
