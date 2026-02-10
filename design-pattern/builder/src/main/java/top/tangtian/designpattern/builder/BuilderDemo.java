package top.tangtian.designpattern.builder;

import top.tangtian.designpattern.builder.email.Email;
import top.tangtian.designpattern.builder.http.HttpRequest;
import top.tangtian.designpattern.builder.query.SqlQuery;

import java.time.LocalDateTime;

/**
 * Hello world!
 *
 */
public class BuilderDemo
{
    public static void main(String[] args) {
        // 测试1: SQL查询构建器
        testSqlQueryBuilder();

        // 测试2: HTTP请求构建器
        testHttpRequestBuilder();

        // 测试3: 邮件构建器
        testEmailBuilder();
    }

    private static void testSqlQueryBuilder() {
        System.out.println("\n" + "█".repeat(70));
        System.out.println("TEST 1: SQL Query Builder");
        System.out.println("█".repeat(70));

        // 示例1: 简单查询
        System.out.println("\n--- Example 1: Simple Query ---");
        SqlQuery query1 = new SqlQuery.Builder("users")
                .select("id", "name", "email")
                .where("age", ">", 18)
                .where("status", "=", "active")
                .orderBy("name")
                .limit(10)
                .build();

        System.out.println(query1.toSql());

        // 示例2: 复杂查询（带JOIN）
        System.out.println("\n--- Example 2: Complex Query with JOIN ---");
        SqlQuery query2 = new SqlQuery.Builder("orders")
                .select("orders.id", "orders.total", "users.name", "users.email")
                .join("users", "orders.user_id = users.id")
                .where("orders.status", "=", "completed")
                .where("orders.total", ">", 100)
                .orderByDesc("orders.created_at")
                .limit(20)
                .build();

        System.out.println(query2.toSql());

        // 示例3: 分页查询
        System.out.println("\n--- Example 3: Pagination Query ---");
        SqlQuery query3 = new SqlQuery.Builder("products")
                .select("id", "name", "price", "category")
                .where("category", "=", "electronics")
                .where("price", "<", 1000)
                .orderBy("price")
                .page(2, 15)  // 第2页，每页15条
                .build();

        System.out.println(query3.toSql());

        // 示例4: GROUP BY查询
        System.out.println("\n--- Example 4: GROUP BY Query ---");
        SqlQuery query4 = new SqlQuery.Builder("sales")
                .select("category", "COUNT(*) as count", "SUM(amount) as total")
                .where("date", ">=", "2024-01-01")
                .groupBy("category")
                .orderByDesc("total")
                .build();

        System.out.println(query4.toSql());
    }

    private static void testHttpRequestBuilder() {
        System.out.println("\n\n" + "█".repeat(70));
        System.out.println("TEST 2: HTTP Request Builder");
        System.out.println("█".repeat(70));

        // 示例1: GET请求
        System.out.println("\n--- Example 1: GET Request ---");
        HttpRequest getRequest = new HttpRequest.Builder("https://api.example.com/users")
                .get()
                .queryParam("page", "1")
                .queryParam("limit", "20")
                .queryParam("sort", "name")
                .header("Accept", "application/json")
                .authorization("your-token-here")
                .connectTimeout(3000)
                .build();

        System.out.println(getRequest);
        System.out.println("Full URL: " + getRequest.getFullUrl());

        // 示例2: POST请求（JSON）
        System.out.println("\n--- Example 2: POST Request with JSON ---");
        String jsonBody = "{\n" +
                "  \"name\": \"John Doe\",\n" +
                "  \"email\": \"john@example.com\",\n" +
                "  \"age\": 30\n" +
                "}";

        HttpRequest postRequest = new HttpRequest.Builder("https://api.example.com/users")
                .post()
                .jsonBody(jsonBody)
                .authorization("your-token-here")
                .build();

        System.out.println(postRequest);

        // 示例3: PUT请求（Basic Auth）
        System.out.println("\n--- Example 3: PUT Request with Basic Auth ---");
        HttpRequest putRequest = new HttpRequest.Builder("https://api.example.com/users/123")
                .put()
                .basicAuth("admin", "password123")
                .contentType("application/json")
                .body("{\"status\":\"active\"}")
                .readTimeout(5000)
                .build();

        System.out.println(putRequest);

        // 示例4: DELETE请求
        System.out.println("\n--- Example 4: DELETE Request ---");
        HttpRequest deleteRequest = new HttpRequest.Builder("https://api.example.com/users/456")
                .delete()
                .header("X-Request-ID", "req-12345")
                .authorization("your-token-here")
                .followRedirects(false)
                .build();

        System.out.println(deleteRequest);
    }

    private static void testEmailBuilder() {
        System.out.println("\n\n" + "█".repeat(70));
        System.out.println("TEST 3: Email Builder");
        System.out.println("█".repeat(70));

        // 示例1: 简单邮件
        System.out.println("\n--- Example 1: Simple Email ---");
        Email email1 = new Email.Builder()
                .from("sender@example.com")
                .to("recipient@example.com")
                .subject("Welcome to Our Service")
                .body("Hello,\n\nThank you for signing up!\n\nBest regards,\nThe Team")
                .build();

        email1.send();

        // 示例2: HTML邮件（带抄送）
        System.out.println("\n--- Example 2: HTML Email with CC ---");
        String htmlContent = "<html><body>" +
                "<h1>Monthly Report</h1>" +
                "<p>Dear Team,</p>" +
                "<p>Please find the monthly sales report attached.</p>" +
                "<ul>" +
                "<li>Total Sales: $50,000</li>" +
                "<li>New Customers: 120</li>" +
                "<li>Growth Rate: 15%</li>" +
                "</ul>" +
                "<p>Best regards,<br/>Sales Department</p>" +
                "</body></html>";

        Email email2 = new Email.Builder()
                .from("sales@company.com")
                .to("manager@company.com", "director@company.com")
                .cc("team@company.com")
                .subject("Monthly Sales Report - January 2024")
                .htmlBody(htmlContent)
                .attachment("/reports/january_2024.pdf")
                .attachment("/reports/charts.xlsx")
                .priority(Email.Priority.HIGH)
                .build();

        email2.send();

        // 示例3: 紧急邮件（定时发送）
        System.out.println("\n--- Example 3: Urgent Scheduled Email ---");
        Email email3 = new Email.Builder()
                .from("support@company.com")
                .to("user@example.com")
                .bcc("archive@company.com")
                .subject("URGENT: System Maintenance Tonight")
                .body("Dear User,\n\n" +
                        "This is to inform you that we will be performing system maintenance tonight " +
                        "from 2:00 AM to 4:00 AM.\n\n" +
                        "Please save your work before 2:00 AM.\n\n" +
                        "We apologize for any inconvenience.\n\n" +
                        "Best regards,\nIT Support Team")
                .urgent()
                .scheduleAt(LocalDateTime.now().plusHours(2))
                .build();

        email3.send();

        // 示例4: 营销邮件（多收件人）
        System.out.println("\n--- Example 4: Marketing Email ---");
        Email email4 = new Email.Builder()
                .from("marketing@company.com")
                .to("customer1@example.com", "customer2@example.com", "customer3@example.com")
                .subject("🎉 Special Offer: 50% Off This Weekend!")
                .htmlBody("<html><body>" +
                        "<h1 style='color: red;'>Weekend Sale!</h1>" +
                        "<p>Get 50% off on all products this weekend only!</p>" +
                        "<a href='https://shop.company.com'>Shop Now</a>" +
                        "</body></html>")
                .attachment("/images/banner.jpg")
                .build();

        email4.send();
    }
}
