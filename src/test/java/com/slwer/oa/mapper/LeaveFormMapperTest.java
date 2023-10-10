package com.slwer.oa.mapper;

import com.slwer.oa.entity.LeaveForm;
import com.slwer.oa.utils.MyBatisUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class LeaveFormMapperTest {

    @Test
    public void insert() {
        MyBatisUtils.executeUpdate(sqlSession -> {
            LeaveFormMapper mapper = sqlSession.getMapper(LeaveFormMapper.class);
            LeaveForm leaveForm = new LeaveForm();
            leaveForm.setEmployeeId(4L);
            leaveForm.setFormType(1);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startTime = null;
            Date endTime = null;
            try {
                startTime = sdf.parse("2020-03-25 08:00:00");
                endTime = sdf.parse("2020-04-01 18:00:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            leaveForm.setStartTime(startTime);
            leaveForm.setEndTime(endTime);
            leaveForm.setReason("回家探亲");
            leaveForm.setCreateTime(new Date());
            leaveForm.setState("processing");
            mapper.insert(leaveForm);
            return null;
        });
    }

    @Test
    public void selectByParams() {
        MyBatisUtils.executeQuery(sqlSession -> {
            LeaveFormMapper mapper = sqlSession.getMapper(LeaveFormMapper.class);
            List<Map<String, Object>> list = mapper.selectByParams("process", 2L);
            System.out.println(list);
            return list;
        });
    }
}