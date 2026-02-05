package top.tangtian.designpattern.factory.samplefactory.client.impl;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;

import java.util.Map;

/**
 * 快速HTTP客户端
 * 适用场景：高并发、低延迟要求的场景（如秒杀、抢购）
 * 特点：连接池大、超时时间短、使用HTTP/2
 */
public class FastHttpClient implements HttpClient {
    private HttpClientConfig config;

    public FastHttpClient(HttpClientConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing FastHttpClient...");
        System.out.println("  - Connect Timeout: " + config.getConnectTimeout() + "ms (optimized)");
        System.out.println("  - Read Timeout: " + config.getReadTimeout() + "ms (optimized)");
        System.out.println("  - Max Connections: " + config.getMaxConnections());
        System.out.println("  - HTTP/2 Enabled: true");
        System.out.println("  - Keep-Alive: enabled");
        System.out.println("  - Connection Pool: aggressive reuse");
    }

    @Override
    public String get(String url, Map<String, String> headers) {
        System.out.println("\n[FastHttpClient] GET: " + url);
        System.out.println("  Using connection from pool (HTTP/2)");

        // 快速客户端，延迟更低
        simulateNetworkDelay(30);

        return "{\"status\":\"success\",\"data\":\"Fast GET response\",\"latency\":\"30ms\"}";
    }

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        System.out.println("\n[FastHttpClient] POST: " + url);
        System.out.println("  Using connection from pool (HTTP/2)");

        simulateNetworkDelay(50);

        return "{\"status\":\"success\",\"data\":\"Fast POST response\",\"latency\":\"50ms\"}";
    }

    @Override
    public void close() {
        System.out.println("Closing FastHttpClient and draining connection pool...");
    }

    @Override
    public String getClientType() {
        return "FAST";
    }

    private void simulateNetworkDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}