package com.slwer.oa.controller;

import com.slwer.oa.entity.Node;
import com.slwer.oa.service.RbacService;
import com.slwer.oa.utils.ResponseUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/user_info")
public class UserInfoServlet extends HttpServlet {
    private RbacService rbacService = new RbacService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uid = request.getParameter("uid");
        List<Node> nodes = rbacService.selectNodeByUserId(Long.parseLong(uid));
        List<Map<String, Object>> treeList = new ArrayList<>();
        Map<String, Object> module = null;
        for (Node node : nodes) {
            if (node.getNodeType() == 1) {
                module = new LinkedHashMap<>();
                module.put("node", node);
                module.put("children", new ArrayList<Node>());
                treeList.add(module);
            } else if (node.getNodeType() == 2) {
                List<Node> children = (List<Node>) module.get("children");
                children.add(node);
            }
        }

        String json = new ResponseUtils().put("nodeList", treeList).toJsonString();
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
