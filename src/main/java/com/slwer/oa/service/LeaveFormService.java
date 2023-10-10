package com.slwer.oa.service;

import com.slwer.oa.entity.Employee;
import com.slwer.oa.entity.LeaveForm;
import com.slwer.oa.entity.ProcessFlow;
import com.slwer.oa.mapper.EmployeeMapper;
import com.slwer.oa.mapper.LeaveFormMapper;
import com.slwer.oa.mapper.ProcessFlowMapper;
import com.slwer.oa.utils.MyBatisUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class LeaveFormService {
    private EmployeeService employeeService = new EmployeeService();

    /**
     * 创建请假单
     *
     * @param leaveForm 前端输入的请假单数据
     * @return 持久化后的请假单对象
     */
    public LeaveForm createLeaveForm(LeaveForm leaveForm) {
        return (LeaveForm) MyBatisUtils.executeUpdate(sqlSession -> {
            //1.持久化 leaveForm 表单数据,8级以下员工表单状态为 processing,8级(总经理)状态为 approved
            EmployeeMapper employeeMapper = sqlSession.getMapper(EmployeeMapper.class);
            Employee employee = employeeMapper.selectById(leaveForm.getEmployeeId());
            Integer level = employee.getLevel();
            if (level < 8) {
                leaveForm.setState("processing");
            } else {
                leaveForm.setState("approved");
            }
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            leaveFormMapper.insert(leaveForm);

            //2.增加第一条流程数据,说明表单已提交,状态为 complete
            ProcessFlow flow1 = new ProcessFlow();
            flow1.setFormId(leaveForm.getFormId());
            flow1.setOperatorId(employee.getEmployeeId());
            flow1.setAction("apply");
            flow1.setCreateTime(new Date());
            flow1.setOrderNo(1);
            flow1.setState("complete");
            flow1.setIsLast(0);
            ProcessFlowMapper processFlowMapper = sqlSession.getMapper(ProcessFlowMapper.class);
            processFlowMapper.insert(flow1);

            //3.分情况创建其余流程数据
            //3.1 7级以下员工,生成部门经理审批任务,请假时间大于等于72小时,还需生成总经理审批任务
            if (level < 7) {
                Employee dmanager = employeeService.selectLeader(employee.getEmployeeId());
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(leaveForm.getFormId());
                flow2.setOperatorId(dmanager.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                long diff = leaveForm.getEndTime().getTime() - leaveForm.getStartTime().getTime();
                float hours = diff * 1f / (1000 * 60 * 60);
                if (hours >= 72) {
                    flow2.setIsLast(0);
                    processFlowMapper.insert(flow2);
                    ProcessFlow flow3 = new ProcessFlow();
                    flow3.setFormId(leaveForm.getFormId());
                    Employee manager = employeeService.selectLeader(dmanager.getEmployeeId());
                    flow3.setOperatorId(manager.getEmployeeId());
                    flow3.setAction("audit");
                    flow3.setCreateTime(new Date());
                    flow3.setOrderNo(3);
                    flow3.setState("ready");
                    flow3.setIsLast(1);
                    processFlowMapper.insert(flow3);
                } else {
                    flow2.setIsLast(1);
                    processFlowMapper.insert(flow2);
                }
            } else if (level == 7) {
                //3.2 7级员工,仅生成总经理审批任务
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(leaveForm.getFormId());
                Employee manager = employeeService.selectLeader(employee.getEmployeeId());
                flow2.setOperatorId(manager.getEmployeeId());
                flow2.setAction("audit");
                flow2.setCreateTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("process");
                flow2.setIsLast(1);
                processFlowMapper.insert(flow2);
            } else if (level == 8) {
                //3.3 8级员工,生成总经理审批任务,系统自动通过
                ProcessFlow flow2 = new ProcessFlow();
                flow2.setFormId(leaveForm.getFormId());
                flow2.setOperatorId(employee.getEmployeeId());
                flow2.setAction("audit");
                flow2.setResult("approved");
                flow2.setReason("自动通过");
                flow2.setCreateTime(new Date());
                flow2.setAuditTime(new Date());
                flow2.setOrderNo(2);
                flow2.setState("complete");
                flow2.setIsLast(1);
                processFlowMapper.insert(flow2);
            }
            return leaveForm;
        });
    }

    /**
     * 获取指定任务状态及指定经办人对应的请假单列表
     *
     * @param pfState    ProcessFlow任务状态
     * @param operatorId 经办人编号
     * @return 请假单及相关数据列表
     */
    public List<Map<String, Object>> getLeaveFormList(String pfState, Long operatorId) {
        return (List<Map<String, Object>>) MyBatisUtils.executeQuery(sqlSession -> {
            LeaveFormMapper mapper = sqlSession.getMapper(LeaveFormMapper.class);
            List<Map<String, Object>> maps = mapper.selectByParams(pfState, operatorId);
            return maps;
        });
    }
}
