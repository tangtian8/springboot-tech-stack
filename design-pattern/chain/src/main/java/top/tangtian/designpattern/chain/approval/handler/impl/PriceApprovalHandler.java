package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;
//价格审核
public class PriceApprovalHandler extends AbstractApprovalHandler {
    private static final double AUTO_APPROVE_LIMIT = 1000.0;   // 自动审批上限
    private static final double MANAGER_APPROVE_LIMIT = 5000.0; // 经理审批上限
    private static final double MAX_ORDER_LIMIT = 50000.0;      // 最大订单金额

    public PriceApprovalHandler() {
        super("Price Approval");
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.PRICE_REVIEW);

        double amount = order.getTotalAmount();

        // 订单金额超过最大限制
        if (amount > MAX_ORDER_LIMIT) {
            return ApprovalResult.reject(
                    String.format("Order amount %.2f exceeds maximum limit %.2f",
                            amount, MAX_ORDER_LIMIT),
                    handlerName);
        }

        // 根据金额确定审批级别
        String approvalLevel;
        if (amount <= AUTO_APPROVE_LIMIT) {
            approvalLevel = "AUTO";
        } else if (amount <= MANAGER_APPROVE_LIMIT) {
            approvalLevel = "MANAGER";
        } else {
            approvalLevel = "DIRECTOR";
        }

        System.out.println("  - Order amount: $" + amount);
        System.out.println("  - Approval level: " + approvalLevel);

        return ApprovalResult.approve(
                String.format("Price approved (Level: %s, Amount: $%.2f)",
                        approvalLevel, amount),
                handlerName);
    }
}
