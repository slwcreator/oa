package com.slwer.oa.mapper;

import com.slwer.oa.entity.Employee;
import com.slwer.oa.utils.MyBatisUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class EmployeeMapperTest {

    @Test
    public void selectById() {
        Employee emp = (Employee) MyBatisUtils.executeQuery(sqlSession -> {
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(4L);
            System.out.println(employee);
            return employee;
        });
    }
}