package top.tangtian.designpattern.factory.samplefactory;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;
import top.tangtian.designpattern.factory.samplefactory.factory.HttpClientFactory;

import java.util.HashMap;
import java.util.Map;

public class HttpClientDemo {
public static void main(String[] args) {
    // 测试1: 支付场景（使用安全客户端）
    testPaymentScenario();

    // 测试2: 短信场景（使用可重试客户端）
    testSMSScenario();

    // 测试3: 秒杀场景（使用快速客户端）
    testSeckillScenario();

    // 测试4: 内部服务调用（使用标准客户端）
    testInternalScenario();

    // 测试5: 自定义配置
    testCustomConfig();
}

private static void testPaymentScenario() {
    System.out.println("\n\n" + "█".repeat(70));
    System.out.println("TEST 1: Payment Scenario (Alipay API)");
    System.out.println("█".repeat(70));

    HttpClient client = HttpClientFactory.createForPayment();

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Authorization", "Bearer alipay_token_12345");

    String requestBody = "{\"amount\":99.99,\"orderId\":\"ORDER123\"}";

    try {
        String response = client.post(
                "https://api.alipay.com/gateway.do",
                requestBody,
                headers
        );
        System.out.println("\nResponse: " + response);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    } finally {
        client.close();
    }
}

private static void testSMSScenario() {
    System.out.println("\n\n" + "█".repeat(70));
    System.out.println("TEST 2: SMS Scenario (Aliyun SMS)");
    System.out.println("█".repeat(70));

    HttpClient client = HttpClientFactory.createForSMS();

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    String requestBody = "{\"phone\":\"13800138000\",\"code\":\"1234\"}";

    try {
        String response = client.post(
                "http://sms.aliyuncs.com/send",
                requestBody,
                headers
        );
        System.out.println("\nResponse: " + response);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    } finally {
        client.close();
    }
}

private static void testSeckillScenario() {
    System.out.println("\n\n" + "█".repeat(70));
    System.out.println("TEST 3: Seckill Scenario (High Concurrency)");
    System.out.println("█".repeat(70));

    HttpClient client = HttpClientFactory.createForSeckill();

    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");

    String requestBody = "{\"productId\":\"PROD999\",\"quantity\":1}";

    try {
        String response = client.post(
                "http://seckill-api.example.com/order",
                requestBody,
                headers
        );
        System.out.println("\nResponse: " + response);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    } finally {
        client.close();
    }
}

private static void testInternalScenario() {
    System.out.println("\n\n" + "█".repeat(70));
    System.out.println("TEST 4: Internal Service Call");
    System.out.println("█".repeat(70));

    HttpClient client = HttpClientFactory.createForInternal();

    Map<String, String> headers = new HashMap<>();
    headers.put("X-Service-Name", "order-service");

    try {
        String response = client.get(
                "http://user-service:8080/api/users/123",
                headers
        );
        System.out.println("\nResponse: " + response);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    } finally {
        client.close();
    }
}

private static void testCustomConfig() {
    System.out.println("\n\n" + "█".repeat(70));
    System.out.println("TEST 5: Custom Configuration");
    System.out.println("█".repeat(70));

    // 自定义配置
    HttpClientConfig config = new HttpClientConfig();
    config.setConnectTimeout(3000);
    config.setReadTimeout(5000);
    config.setMaxRetries(5);
    config.setMaxConnections(50);

    HttpClient client = HttpClientFactory.createClient(HttpClientFactory.ClientType.RETRYABLE, config);

    Map<String, String> headers = new HashMap<>();

    try {
        String response = client.get(
                "http://unstable-api.example.com/data",
                headers
        );
        System.out.println("\nResponse: " + response);
    } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
    } finally {
        client.close();
    }
}
}