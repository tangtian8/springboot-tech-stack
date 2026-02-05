package top.tangtian.designpattern.chain.approval.chain;

import top.tangtian.designpattern.chain.approval.handler.ApprovalHandler;
import top.tangtian.designpattern.chain.approval.handler.impl.*;
import top.tangtian.designpattern.chain.approval.model.ApprovalResult;
import top.tangtian.designpattern.chain.approval.model.Order;

public class ApprovalChain {
    private ApprovalHandler firstHandler;

    public ApprovalChain() {
        buildChain();
    }

    private void buildChain() {
        // 创建所有处理器
        OrderValidationHandler validation = new OrderValidationHandler();
        InventoryCheckHandler inventory = new InventoryCheckHandler();
        PriceApprovalHandler price = new PriceApprovalHandler();
        CreditCheckHandler credit = new CreditCheckHandler();
        RiskControlHandler risk = new RiskControlHandler();
        FinalApprovalHandler finalApproval = new FinalApprovalHandler();

        // 构建责任链
        validation.setNext(inventory);
        inventory.setNext(price);
        price.setNext(credit);
        credit.setNext(risk);
        risk.setNext(finalApproval);

        this.firstHandler = validation;
    }

    public ApprovalResult process(Order order) {
        System.out.println("\n" + "█".repeat(60));
        System.out.println("Starting approval process for: " + order.getOrderId());
        System.out.println("█".repeat(60));

        ApprovalResult result = firstHandler.approve(order);

        System.out.println("\n" + "█".repeat(60));
        System.out.println("Final Result: " + (result.isApproved() ? "✓ APPROVED" : "✗ REJECTED"));
        System.out.println("Message: " + result.getMessage());
        System.out.println("█".repeat(60));

        return result;
    }

    public void printApprovalLogs(Order order) {
        System.out.println("\n--- Approval Logs ---");
        order.getApprovalLogs().forEach(System.out::println);
    }
}
