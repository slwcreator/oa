package com.slwer.oa.service;

import com.slwer.oa.entity.LeaveForm;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LeaveFormServiceTest {
    LeaveFormService leaveFormService = new LeaveFormService();

    /**
     * 市场部员工请假单（72小时以上）测试用例
     *
     * @throws ParseException
     */
    @Test
    public void createLeaveForm1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(8L);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023101009"));
        leaveForm.setEndTime(sdf.parse("2023101518"));
        leaveForm.setReason("市场部员工请假单（72小时以上）");
        leaveForm.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    /**
     * 市场部员工请假单（72小时以内）测试用例
     *
     * @throws ParseException
     */
    @Test
    public void createLeaveForm2() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(8L);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023101009"));
        leaveForm.setEndTime(sdf.parse("2023101118"));
        leaveForm.setReason("市场部员工请假单（72小时以内）");
        leaveForm.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    /**
     * 研发部部门经理请假单测试用例
     *
     * @throws ParseException
     */
    @Test
    public void createLeaveForm3() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(2L);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023101009"));
        leaveForm.setEndTime(sdf.parse("2023101118"));
        leaveForm.setReason("研发部部门经理请假单");
        leaveForm.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    /**
     * 总经理请假单测试用例
     *
     * @throws ParseException
     */
    @Test
    public void createLeaveForm4() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(1L);
        leaveForm.setFormType(1);
        leaveForm.setStartTime(sdf.parse("2023101009"));
        leaveForm.setEndTime(sdf.parse("2023101118"));
        leaveForm.setReason("总经理请假单");
        leaveForm.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(leaveForm);
        System.out.println(savedForm.getFormId());
    }

    /**
     * 情况1: 72小时以上请假,部门经理同意,总经理同意,流程结束
     *
     * @throws ParseException
     */
    @Test
    public void audit1() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3L);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020040118"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以上)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(form);
        System.out.println(savedForm.getFormId());
        leaveFormService.audit(savedForm.getFormId(), 2L, "approved", "部门经理同意");
        leaveFormService.audit(savedForm.getFormId(), 1L, "approved", "总经理同意");
    }

    /**
     * 情况2: 72小时以上请假,部门经理拒绝,流程结束
     *
     * @throws ParseException
     */
    @Test
    public void audit2() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3L);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020040118"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以上)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(form);
        leaveFormService.audit(savedForm.getFormId(), 2L, "refused", "部门经理拒绝");
    }

    /**
     * 情况3: 72小时以内请假,部门经理同意,流程结束
     *
     * @throws ParseException
     */
    @Test
    public void audit3() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        LeaveForm form = new LeaveForm();
        form.setEmployeeId(3L);
        form.setStartTime(sdf.parse("2020032608"));
        form.setEndTime(sdf.parse("2020032718"));
        form.setFormType(1);
        form.setReason("研发部员工王美美请假单(72小时以内)");
        form.setCreateTime(new Date());
        LeaveForm savedForm = leaveFormService.createLeaveForm(form);
        System.out.println(savedForm.getFormId());
        leaveFormService.audit(savedForm.getFormId(), 2L, "approved", "部门经理同意");
    }
}