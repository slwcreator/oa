package com.slwer.oa.mapper;

import com.slwer.oa.entity.LeaveForm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LeaveFormMapper {
    void insert(LeaveForm leaveForm);

    List<Map<String, Object>> selectByParams(@Param("pf_state") String pfState
            , @Param("pf_operator_id") Long pfOperatorId);

    void update(LeaveForm leaveForm);

    LeaveForm selectById(Long formId);
}
