package top.tangtian.designpattern.factory.samplefactory.client.impl;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;

import java.util.Map;

/**
 * 标准HTTP客户端
 * 适用场景：内部服务调用、性能要求不高的场景
 */
public class StandardHttpClient implements HttpClient {
    private HttpClientConfig config;

    public StandardHttpClient(HttpClientConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing StandardHttpClient...");
        System.out.println("  - Connect Timeout: " + config.getConnectTimeout() + "ms");
        System.out.println("  - Read Timeout: " + config.getReadTimeout() + "ms");
        System.out.println("  - Max Connections: " + config.getMaxConnections());
    }

    @Override
    public String get(String url, Map<String, String> headers) {
        System.out.println("\n[StandardHttpClient] GET: " + url);
        System.out.println("  Headers: " + headers);

        // 模拟HTTP请求
        simulateNetworkDelay(100);

        return "{\"status\":\"success\",\"data\":\"Standard GET response\"}";
    }

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        System.out.println("\n[StandardHttpClient] POST: " + url);
        System.out.println("  Headers: " + headers);
        System.out.println("  Body: " + body);

        simulateNetworkDelay(150);

        return "{\"status\":\"success\",\"data\":\"Standard POST response\"}";
    }

    @Override
    public void close() {
        System.out.println("Closing StandardHttpClient...");
    }

    @Override
    public String getClientType() {
        return "STANDARD";
    }

    private void simulateNetworkDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}