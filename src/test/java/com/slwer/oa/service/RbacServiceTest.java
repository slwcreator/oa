package com.slwer.oa.service;

import com.slwer.oa.entity.Node;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RbacServiceTest {

    @Test
    public void selectNodeByUserId() {
        RbacService rbacService = new RbacService();
        List<Node> nodes = rbacService.selectNodeByUserId(1L);
        for (Node n : nodes) {
            System.out.println(n.getNodeName());
        }
    }
}