package top.tangtian.designpattern.chain.approval.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private User user;
    private double totalAmount;
    private int itemCount;
    private List<String> productIds;
    private OrderStatus status;
    private LocalDateTime createTime;
    private List<String> approvalLogs;  // 审批日志

    public Order(String orderId, User user, double totalAmount,
                 int itemCount, List<String> productIds) {
        this.orderId = orderId;
        this.user = user;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
        this.productIds = productIds;
        this.status = OrderStatus.PENDING;
        this.createTime = LocalDateTime.now();
        this.approvalLogs = new ArrayList<>();
    }

    public void addLog(String log) {
        approvalLogs.add(String.format("[%s] %s",
                LocalDateTime.now().toString(), log));
    }

    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public List<String> getProductIds() { return productIds; }
    public void setProductIds(List<String> productIds) { this.productIds = productIds; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreateTime() { return createTime; }

    public List<String> getApprovalLogs() { return approvalLogs; }

    @Override
    public String toString() {
        return String.format("Order{id='%s', user='%s', amount=%.2f, status=%s}",
                orderId, user.getUserName(), totalAmount, status);
    }
}
