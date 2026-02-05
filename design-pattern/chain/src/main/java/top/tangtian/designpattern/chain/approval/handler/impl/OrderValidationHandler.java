package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;

public class OrderValidationHandler extends AbstractApprovalHandler {

    public OrderValidationHandler() {
        super("Order Validation");
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.VALIDATING);

        // 验证订单ID
        if (order.getOrderId() == null || order.getOrderId().trim().isEmpty()) {
            return ApprovalResult.reject("Order ID is missing", handlerName);
        }

        // 验证用户信息
        if (order.getUser() == null) {
            return ApprovalResult.reject("User information is missing", handlerName);
        }

        // 验证订单金额
        if (order.getTotalAmount() <= 0) {
            return ApprovalResult.reject("Invalid order amount: " + order.getTotalAmount(), handlerName);
        }

        // 验证商品数量
        if (order.getItemCount() <= 0) {
            return ApprovalResult.reject("Invalid item count: " + order.getItemCount(), handlerName);
        }

        // 验证商品列表
        if (order.getProductIds() == null || order.getProductIds().isEmpty()) {
            return ApprovalResult.reject("Product list is empty", handlerName);
        }

        return ApprovalResult.approve("Order validation passed", handlerName);
    }
}