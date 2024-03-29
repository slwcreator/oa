package com.slwer.oa.service;

import com.slwer.oa.entity.Employee;
import com.slwer.oa.entity.LeaveForm;
import com.slwer.oa.entity.Notice;
import com.slwer.oa.entity.ProcessFlow;
import com.slwer.oa.mapper.EmployeeMapper;
import com.slwer.oa.mapper.LeaveFormMapper;
import com.slwer.oa.mapper.NoticeMapper;
import com.slwer.oa.mapper.ProcessFlowMapper;
import com.slwer.oa.service.exception.LeaveFormException;
import com.slwer.oa.utils.MyBatisUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

            NoticeMapper noticeMapper = sqlSession.getMapper(NoticeMapper.class);
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");
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
                String notice1 = String.format("您的请假申请[%s-%s]已提交,请等待上级审批."
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime()));
                noticeMapper.insert(new Notice(employee.getEmployeeId(), notice1));

                String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                        , employee.getTitle()
                        , employee.getName()
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime()));
                noticeMapper.insert(new Notice(dmanager.getEmployeeId(), notice2));

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

                String notice1 = String.format("您的请假申请[%s-%s]已提交,请等待上级审批."
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime()));
                noticeMapper.insert(new Notice(employee.getEmployeeId(), notice1));

                //通知总经理审批消息
                String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                        , employee.getTitle()
                        , employee.getName()
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime()));
                noticeMapper.insert(new Notice(manager.getEmployeeId(), notice2));

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

                String notice = String.format("您的请假申请[%s-%s]系统已自动批准通过."
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime()));
                noticeMapper.insert(new Notice(employee.getEmployeeId(), notice));
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

    /**
     * 审核请假单
     *
     * @param formId     表单编号
     * @param operatorId 经办人(当前登录员工)
     * @param result     审批结果
     * @param reason     审批意见
     */
    public void audit(Long formId, Long operatorId, String result, String reason) {
        MyBatisUtils.executeUpdate(sqlSession -> {
            ProcessFlowMapper processFlowMapper = sqlSession.getMapper(ProcessFlowMapper.class);
            List<ProcessFlow> flowList = processFlowMapper.selectByFormId(formId);
            if (flowList.size() == 0) {
                throw new LeaveFormException("无效的审批流程");
            }
            //获取当前任务 ProcessFlow 对象
            List<ProcessFlow> processList = flowList.stream()
                    .filter(p -> Objects.equals(p.getOperatorId(), operatorId) && p.getState().equals("process"))
                    .collect(Collectors.toList());
            ProcessFlow processFlow = null;
            if (processList.size() == 0) {
                throw new LeaveFormException("未找到待处理任务节点");
            } else {
                processFlow = processList.get(0);
                processFlow.setState("complete");
                processFlow.setResult(result);
                processFlow.setReason(reason);
                processFlow.setAuditTime(new Date());
                processFlowMapper.update(processFlow);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH时");
            LeaveFormMapper leaveFormMapper = sqlSession.getMapper(LeaveFormMapper.class);
            NoticeMapper noticeMapper = sqlSession.getMapper(NoticeMapper.class);
            LeaveForm leaveForm = leaveFormMapper.selectById(formId);
            Employee operator = employeeService.selectById(operatorId);
            Employee employee = employeeService.selectById(leaveForm.getEmployeeId());
            //如果当前任务是最后一个节点,代表流程结束,更新请假单状态为对应的approved/refused
            if (processFlow.getIsLast() == 1) {
                leaveForm.setState(result);
                leaveFormMapper.update(leaveForm);
                String strResult = null;
                if (result.equals("approved")) {
                    strResult = "批准";
                } else if (result.equals("refused")) {
                    strResult = "驳回";
                }
                String notice1 = String.format("您的请假申请[%s-%s]%s%s已%s,审批意见:%s,审批流程已结束"
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime())
                        , operator.getTitle()
                        , operator.getName()
                        , strResult, reason);
                noticeMapper.insert(new Notice(leaveForm.getEmployeeId(), notice1));

                //发给审批人的通知
                String notice2 = String.format("%s-%s提起请假申请[%s-%s]您已%s,审批意见:%s,审批流程已结束"
                        , employee.getTitle(), employee.getName()
                        , sdf.format(leaveForm.getStartTime())
                        , sdf.format(leaveForm.getEndTime())
                        , strResult, reason);
                noticeMapper.insert(new Notice(operator.getEmployeeId(), notice2));

            } else {
                //readyList 包含所有后续任务节点
                List<ProcessFlow> readyList = flowList.stream().filter(p -> p.getState().equals("ready"))
                        .collect(Collectors.toList());
                //如果当前任务不是最后一个节点且审批通过,那下一个节点的状态从ready变为process
                if (result.equals("approved")) {
                    ProcessFlow readyProcess = readyList.get(0);
                    readyProcess.setState("process");
                    processFlowMapper.update(readyProcess);

                    //消息1: 通知表单提交人,部门经理已经审批通过,交由上级继续审批
                    String notice1 = String.format("您的请假申请[%s-%s]%s%s已批准,审批意见:%s ,请继续等待上级审批"
                            , sdf.format(leaveForm.getStartTime())
                            , sdf.format(leaveForm.getEndTime())
                            , operator.getTitle(), operator.getName(), reason);
                    noticeMapper.insert(new Notice(leaveForm.getEmployeeId(), notice1));

                    //消息2: 通知总经理有新的审批任务
                    String notice2 = String.format("%s-%s提起请假申请[%s-%s],请尽快审批"
                            , employee.getTitle(), employee.getName()
                            , sdf.format(leaveForm.getStartTime())
                            , sdf.format(leaveForm.getEndTime()));
                    noticeMapper.insert(new Notice(readyProcess.getOperatorId(), notice2));

                    //消息3: 通知部门经理(当前经办人),员工的申请单你已批准,交由上级继续审批
                    String notice3 = String.format("%s-%s提起请假申请[%s-%s]您已批准,审批意见:%s,申请转至上级领导继续审批"
                            , employee.getTitle(), employee.getName()
                            , sdf.format(leaveForm.getStartTime())
                            , sdf.format(leaveForm.getEndTime()), reason);
                    noticeMapper.insert(new Notice(operator.getEmployeeId(), notice3));
                } else if (result.equals("refused")) {
                    //如果当前任务不是最后一个节点且审批驳回,则后续所有任务状态变为 cancel,请假单状态变为 refused
                    for (ProcessFlow p : readyList) {
                        p.setState("cancel");
                        processFlowMapper.update(p);
                    }
                    leaveForm.setState("refused");
                    leaveFormMapper.update(leaveForm);

                    //消息1: 通知申请人表单已被驳回
                    String notice1 = String.format("您的请假申请[%s-%s]%s%s已驳回,审批意见:%s,审批流程已结束"
                            , sdf.format(leaveForm.getStartTime())
                            , sdf.format(leaveForm.getEndTime())
                            , operator.getTitle(), operator.getName(), reason);
                    noticeMapper.insert(new Notice(leaveForm.getEmployeeId(), notice1));

                    //消息2: 通知经办人表单"您已驳回"
                    String notice2 = String.format("%s-%s提起请假申请[%s-%s]您已驳回,审批意见:%s,审批流程已结束"
                            , employee.getTitle(), employee.getName()
                            , sdf.format(leaveForm.getStartTime())
                            , sdf.format(leaveForm.getEndTime()), reason);
                    noticeMapper.insert(new Notice(operator.getEmployeeId(), notice2));
                }
            }
            return null;
        });
    }
}
