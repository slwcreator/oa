package com.slwer.oa.mapper;

import com.slwer.oa.entity.ProcessFlow;

import java.util.List;

public interface ProcessFlowMapper {
    void insert(ProcessFlow processFlow);

    void update(ProcessFlow processFlow);

    List<ProcessFlow> selectByFormId(Long formId);
}
