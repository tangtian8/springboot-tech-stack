package top.tangtian.designpattern.factory.samplefactory.client.impl;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;

import java.util.Map;

/**
 * 可重试HTTP客户端
 * 适用场景：网络不稳定、第三方接口不可靠
 * 特点：自动重试、指数退避
 */
public class RetryableHttpClient implements HttpClient {
    private HttpClientConfig config;
    private int requestCount = 0;  // 模拟请求计数

    public RetryableHttpClient(HttpClientConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing RetryableHttpClient...");
        System.out.println("  - Max Retries: " + config.getMaxRetries());
        System.out.println("  - Retry Strategy: Exponential Backoff");
        System.out.println("  - Connect Timeout: " + config.getConnectTimeout() + "ms");
        System.out.println("  - Read Timeout: " + config.getReadTimeout() + "ms");
    }

    @Override
    public String get(String url, Map<String, String> headers) {
        return executeWithRetry(() -> doGet(url, headers), "GET", url);
    }

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        return executeWithRetry(() -> doPost(url, body, headers), "POST", url);
    }

    private String doGet(String url, Map<String, String> headers) {
        System.out.println("  Executing GET: " + url);

        // 模拟30%的失败率
        if (Math.random() < 0.3) {
            throw new RuntimeException("Network error (simulated)");
        }

        simulateNetworkDelay(100);
        return "{\"status\":\"success\",\"data\":\"Retryable GET response\"}";
    }

    private String doPost(String url, String body, Map<String, String> headers) {
        System.out.println("  Executing POST: " + url);

        // 模拟30%的失败率
        if (Math.random() < 0.3) {
            throw new RuntimeException("Network error (simulated)");
        }

        simulateNetworkDelay(150);
        return "{\"status\":\"success\",\"data\":\"Retryable POST response\"}";
    }

    private String executeWithRetry(RequestExecutor executor, String method, String url) {
        System.out.println("\n[RetryableHttpClient] " + method + ": " + url);

        int attempt = 0;
        Exception lastException = null;

        while (attempt <= config.getMaxRetries()) {
            try {
                if (attempt > 0) {
                    int delay = calculateBackoffDelay(attempt);
                    System.out.println("  ⏳ Retry attempt " + attempt + " after " + delay + "ms delay");
                    Thread.sleep(delay);
                }

                String response = executor.execute();

                if (attempt > 0) {
                    System.out.println("  ✓ Request succeeded on attempt " + (attempt + 1));
                }

                return response;

            } catch (Exception e) {
                lastException = e;
                System.out.println("  ✗ Attempt " + (attempt + 1) + " failed: " + e.getMessage());
                attempt++;
            }
        }

        System.out.println("  ✗ All " + (config.getMaxRetries() + 1) + " attempts failed");
        throw new RuntimeException("Request failed after " + (config.getMaxRetries() + 1) +
                " attempts", lastException);
    }

    /**
     * 计算指数退避延迟
     * 第1次重试: 1秒
     * 第2次重试: 2秒
     * 第3次重试: 4秒
     */
    private int calculateBackoffDelay(int attempt) {
        return (int) (Math.pow(2, attempt - 1) * 1000);
    }

    @Override
    public void close() {
        System.out.println("Closing RetryableHttpClient...");
    }

    @Override
    public String getClientType() {
        return "RETRYABLE";
    }

    private void simulateNetworkDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface RequestExecutor {
        String execute() throws Exception;
    }
}