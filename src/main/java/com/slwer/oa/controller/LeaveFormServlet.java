package com.slwer.oa.controller;

import com.slwer.oa.entity.LeaveForm;
import com.slwer.oa.service.LeaveFormService;
import com.slwer.oa.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@WebServlet("/api/leave/*")
public class LeaveFormServlet extends HttpServlet {
    private LeaveFormService leaveFormService = new LeaveFormService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=utf-8");
        String uri = request.getRequestURI();
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        switch (methodName) {
            case "create":
                this.create(request, response);
                break;
            case "list":

                break;
            case "audit":

                break;
        }
    }

    private void create(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
}
