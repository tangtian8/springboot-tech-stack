package top.tangtian.designpattern.chain.approval.handler;

import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;

public interface ApprovalHandler {
    void setNext(ApprovalHandler next);
    ApprovalResult approve(Order order);
}
