package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;
import top.tangtian.designpattern.chain.approval.model.User;
//信用审核
public class CreditCheckHandler extends AbstractApprovalHandler {
    private static final int MIN_CREDIT_SCORE = 600;  // 最低信用分

    public CreditCheckHandler() {
        super("Credit Check");
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.CREDIT_CHECK);

        User user = order.getUser();

        // 检查信用分
        if (user.getCreditScore() < MIN_CREDIT_SCORE) {
            return ApprovalResult.reject(
                    String.format("Credit score %d is below minimum %d",
                            user.getCreditScore(), MIN_CREDIT_SCORE),
                    handlerName);
        }

        // 检查信用额度
        if (order.getTotalAmount() > user.getCreditLimit()) {
            return ApprovalResult.reject(
                    String.format("Order amount $%.2f exceeds credit limit $%.2f",
                            order.getTotalAmount(), user.getCreditLimit()),
                    handlerName);
        }

        System.out.println("  - User: " + user.getUserName());
        System.out.println("  - Credit Score: " + user.getCreditScore());
        System.out.println("  - Credit Limit: $" + user.getCreditLimit());
        System.out.println("  - Order Amount: $" + order.getTotalAmount());

        return ApprovalResult.approve(
                "Credit check passed", handlerName);
    }
}