package top.tangtian.designpattern.chain.approval.handler.impl;

import top.tangtian.designpattern.chain.approval.handler.AbstractApprovalHandler;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;
import top.tangtian.designpattern.chain.approval.model.OrderStatus;
//最终批准
public class FinalApprovalHandler extends AbstractApprovalHandler {

    public FinalApprovalHandler() {
        super("Final Approval");
    }

    @Override
    protected ApprovalResult doApprove(Order order) {
        order.setStatus(OrderStatus.APPROVED);

        System.out.println("  - All checks passed!");
        System.out.println("  - Order approved and ready for processing");

        return ApprovalResult.approve(
                "Order fully approved and confirmed", handlerName);
    }
}