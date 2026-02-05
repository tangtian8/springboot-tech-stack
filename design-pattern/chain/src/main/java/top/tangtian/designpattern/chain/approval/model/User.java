package top.tangtian.designpattern.chain.approval.model;

public class User {
    private String userId;
    private String userName;
    private int creditScore;      // 信用分 0-1000
    private double creditLimit;   // 信用额度
    private int orderCount;       // 历史订单数

    public User(String userId, String userName, int creditScore,
                double creditLimit, int orderCount) {
        this.userId = userId;
        this.userName = userName;
        this.creditScore = creditScore;
        this.creditLimit = creditLimit;
        this.orderCount = orderCount;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public int getCreditScore() { return creditScore; }
    public void setCreditScore(int creditScore) { this.creditScore = creditScore; }

    public double getCreditLimit() { return creditLimit; }
    public void setCreditLimit(double creditLimit) { this.creditLimit = creditLimit; }

    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s', creditScore=%d, creditLimit=%.2f}",
                userId, userName, creditScore, creditLimit);
    }
}