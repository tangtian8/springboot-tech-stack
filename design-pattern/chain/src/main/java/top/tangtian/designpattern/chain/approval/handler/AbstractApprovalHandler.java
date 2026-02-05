package top.tangtian.designpattern.chain.approval.handler;

import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;

public abstract class AbstractApprovalHandler implements ApprovalHandler {
    protected ApprovalHandler next;
    protected String handlerName;

    public AbstractApprovalHandler(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public void setNext(ApprovalHandler next) {
        this.next = next;
    }

    protected ApprovalResult passToNext(Order order) {
        if (next != null) {
            return next.approve(order);
        }
        // 链条末端，默认通过
        return ApprovalResult.approve("All approvals passed", "Chain End");
    }

    @Override
    public ApprovalResult approve(Order order) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Processing: " + handlerName);
        System.out.println("Order: " + order);

        ApprovalResult result = doApprove(order);

        if (result.isApproved()) {
            System.out.println("✓ " + result.getMessage());
            order.addLog(handlerName + ": " + result.getMessage());
            return passToNext(order);
        } else {
            System.out.println("✗ " + result.getMessage());
            order.addLog(handlerName + ": REJECTED - " + result.getMessage());
            order.setStatus(OrderStatus.REJECTED);
            return result;
        }
    }

    // 子类实现具体的审批逻辑
    protected abstract ApprovalResult doApprove(Order order);
}