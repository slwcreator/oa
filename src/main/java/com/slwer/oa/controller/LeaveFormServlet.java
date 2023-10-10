package com.slwer.oa.controller;

import com.slwer.oa.entity.LeaveForm;
import com.slwer.oa.service.LeaveFormService;
import com.slwer.oa.utils.ResponseUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/api/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormService leaveFormService = new LeaveFormService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        String uri = request.getRequestURI();
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        switch (methodName) {
            case "create":
                this.create(request, response);
                break;
            case "list":
                this.list(request, response);
                break;
            case "audit":

                break;
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String strEmployeeId = request.getParameter("eid");
        String formType = request.getParameter("formType");
        // 1970 年到现在的毫秒数
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String reason = request.getParameter("reason");
        LeaveForm leaveForm = new LeaveForm();
        leaveForm.setEmployeeId(Long.parseLong(strEmployeeId));
        leaveForm.setFormType(Integer.parseInt(formType));
        leaveForm.setStartTime(new Date(Long.parseLong(startTime)));
        leaveForm.setEndTime(new Date(Long.parseLong(endTime)));
        leaveForm.setReason(reason);
        leaveForm.setCreateTime(new Date());
        ResponseUtils resp = null;
        try {
            leaveFormService.createLeaveForm(leaveForm);
            resp = new ResponseUtils();
        } catch (Exception e) {
            e.printStackTrace();
            resp = new ResponseUtils(e.getClass().getSimpleName(), e.getMessage());
        }
        response.getWriter().println(resp.toJsonString());
    }

    private void list(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String employeeId = request.getParameter("eid");
        ResponseUtils resp = null;
        try {
            List<Map<String, Object>> formList = leaveFormService.getLeaveFormList("process", Long.parseLong(employeeId));
            //将从数据库读取的时间格式 LocalDateTime 转换成 Timestamp 时间戳
            for (Map<String, Object> map : formList) {
                for (String key : map.keySet()) {
                    if (key.equals("start_time") || key.equals("end_time") || key.equals("create_time")) {
                        LocalDateTime localDateTime = (LocalDateTime) map.get(key);
                        long timestamp = localDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
                        map.put(key, timestamp);
                    }
                }
            }
            resp = new ResponseUtils().put("list", formList);
        } catch (Exception e) {
            e.printStackTrace();
            resp = new ResponseUtils(e.getClass().getSimpleName(), e.getMessage());
        }
        response.getWriter().println(resp.toJsonString());
    }
}
