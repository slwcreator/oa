package com.slwer.oa.mapper;

import com.slwer.oa.entity.Employee;

public interface EmployeeMapper {
    Employee selectById(Long employeeId);
}
