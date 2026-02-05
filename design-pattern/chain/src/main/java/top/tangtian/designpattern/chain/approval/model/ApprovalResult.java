package top.tangtian.designpattern.chain.approval.model;

public class ApprovalResult {
    private boolean approved;
    private String message;
    private String handlerName;

    public ApprovalResult(boolean approved, String message, String handlerName) {
        this.approved = approved;
        this.message = message;
        this.handlerName = handlerName;
    }

    public static ApprovalResult approve(String message, String handlerName) {
        return new ApprovalResult(true, message, handlerName);
    }

    public static ApprovalResult reject(String message, String handlerName) {
        return new ApprovalResult(false, message, handlerName);
    }

    // Getters
    public boolean isApproved() { return approved; }
    public String getMessage() { return message; }
    public String getHandlerName() { return handlerName; }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s",
                handlerName, approved ? "APPROVED" : "REJECTED", message);
    }
}