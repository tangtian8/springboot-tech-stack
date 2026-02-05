package top.tangtian.designpattern.chain.approval.model;

public enum OrderStatus {
    PENDING,        // 待审批
    VALIDATING,     // 验证中
    INVENTORY_CHECK,// 库存检查中
    PRICE_REVIEW,   // 价格审核中
    CREDIT_CHECK,   // 信用检查中
    RISK_REVIEW,    // 风控审核中
    APPROVED,       // 已批准
    REJECTED        // 已拒绝
}
