package top.tangtian.designpattern.builder.email;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: springboot-tech-stack
 * @description: 邮箱对象
 * @author: tangtian
 * @create: 2026-02-09 17:01
 **/
public class Email {
    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String body;
    private final boolean isHtml;
    private final List<String> attachments;
    private final Priority priority;
    private final LocalDateTime scheduledTime;

    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }

    private Email(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.cc = builder.cc;
        this.bcc = builder.bcc;
        this.subject = builder.subject;
        this.body = builder.body;
        this.isHtml = builder.isHtml;
        this.attachments = builder.attachments;
        this.priority = builder.priority;
        this.scheduledTime = builder.scheduledTime;
    }

    // Getters
    public String getFrom() { return from; }
    public List<String> getTo() { return new ArrayList<>(to); }
    public List<String> getCc() { return new ArrayList<>(cc); }
    public List<String> getBcc() { return new ArrayList<>(bcc); }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public boolean isHtml() { return isHtml; }
    public List<String> getAttachments() { return new ArrayList<>(attachments); }
    public Priority getPriority() { return priority; }
    public LocalDateTime getScheduledTime() { return scheduledTime; }

    /**
     * 发送邮件（模拟）
     */
    public void send() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Sending Email...");
        System.out.println("=".repeat(60));
        System.out.println("From: " + from);
        System.out.println("To: " + to);
        if (!cc.isEmpty()) {
            System.out.println("CC: " + cc);
        }
        if (!bcc.isEmpty()) {
            System.out.println("BCC: " + bcc);
        }
        System.out.println("Subject: " + subject);
        System.out.println("Priority: " + priority);
        System.out.println("Format: " + (isHtml ? "HTML" : "Plain Text"));
        if (!attachments.isEmpty()) {
            System.out.println("Attachments: " + attachments);
        }
        if (scheduledTime != null) {
            System.out.println("Scheduled: " + scheduledTime);
        }
        System.out.println("\nBody:");
        System.out.println("-".repeat(60));
        System.out.println(body);
        System.out.println("-".repeat(60));
        System.out.println("✓ Email sent successfully!");
        System.out.println("=".repeat(60));
    }

    /**
     * 建造者
     */
    public static class Builder {
        // 必需参数
        private String from;
        private List<String> to = new ArrayList<>();
        private String subject;
        private String body;

        // 可选参数
        private List<String> cc = new ArrayList<>();
        private List<String> bcc = new ArrayList<>();
        private boolean isHtml = false;
        private List<String> attachments = new ArrayList<>();
        private Priority priority = Priority.NORMAL;
        private LocalDateTime scheduledTime;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String... recipients) {
            for (String recipient : recipients) {
                if (isValidEmail(recipient)) {
                    this.to.add(recipient);
                } else {
                    throw new IllegalArgumentException("Invalid email: " + recipient);
                }
            }
            return this;
        }

        public Builder cc(String... recipients) {
            for (String recipient : recipients) {
                if (isValidEmail(recipient)) {
                    this.cc.add(recipient);
                }
            }
            return this;
        }

        public Builder bcc(String... recipients) {
            for (String recipient : recipients) {
                if (isValidEmail(recipient)) {
                    this.bcc.add(recipient);
                }
            }
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            this.isHtml = false;
            return this;
        }

        public Builder htmlBody(String html) {
            this.body = html;
            this.isHtml = true;
            return this;
        }

        public Builder attachment(String filePath) {
            this.attachments.add(filePath);
            return this;
        }

        public Builder attachments(String... filePaths) {
            for (String path : filePaths) {
                this.attachments.add(path);
            }
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder highPriority() {
            this.priority = Priority.HIGH;
            return this;
        }

        public Builder urgent() {
            this.priority = Priority.URGENT;
            return this;
        }

        public Builder scheduleAt(LocalDateTime dateTime) {
            this.scheduledTime = dateTime;
            return this;
        }

        public Email build() {
            // 验证必需字段
            if (from == null || from.trim().isEmpty()) {
                throw new IllegalStateException("From address is required");
            }
            if (to.isEmpty()) {
                throw new IllegalStateException("At least one recipient is required");
            }
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalStateException("Subject is required");
            }
            if (body == null || body.trim().isEmpty()) {
                throw new IllegalStateException("Body is required");
            }

            return new Email(this);
        }

        private boolean isValidEmail(String email) {
            return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
        }
    }
}