package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;
import top.tangtian.designpattern.chain.approval.model.User;
//风控审核
public class RiskControlHandler extends AbstractApprovalHandler {
    private static final double HIGH_RISK_AMOUNT = 3000.0;  // 高风险金额阈值
    private static final int MIN_ORDER_HISTORY = 5;         // 新用户订单数阈值

    public RiskControlHandler() {
        super("Risk Control");
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.RISK_REVIEW);

        User user = order.getUser();
        double amount = order.getTotalAmount();
        int riskScore = calculateRiskScore(order);

        System.out.println("  - Risk Score: " + riskScore);

        // 高风险订单
        if (riskScore >= 80) {
            return ApprovalResult.reject(
                    String.format("High risk order detected (Risk Score: %d)", riskScore),
                    handlerName);
        }

        // 中风险订单 - 需要人工审核
        if (riskScore >= 50) {
            System.out.println("  - Medium risk: Manual review required");
            return ApprovalResult.approve(
                    String.format("Medium risk order approved with manual review (Risk Score: %d)",
                            riskScore),
                    handlerName);
        }

        // 低风险订单
        return ApprovalResult.approve(
                String.format("Low risk order (Risk Score: %d)", riskScore),
                handlerName);
    }

    private int calculateRiskScore(Order order) {
        int score = 0;
        User user = order.getUser();
        double amount = order.getTotalAmount();

        // 大额订单增加风险
        if (amount > HIGH_RISK_AMOUNT) {
            score += 30;
        }

        // 新用户增加风险
        if (user.getOrderCount() < MIN_ORDER_HISTORY) {
            score += 25;
        }

        // 低信用分增加风险
        if (user.getCreditScore() < 700) {
            score += 20;
        }

        // 订单金额超过信用额度80%
        if (amount > user.getCreditLimit() * 0.8) {
            score += 15;
        }

        return score;
    }
}
