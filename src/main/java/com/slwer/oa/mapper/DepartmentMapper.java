package com.slwer.oa.mapper;

import com.slwer.oa.entity.Department;

public interface DepartmentMapper {
    Department selectById(Long departmentId);
}
