package top.tangtian.designpattern.factory.samplefactory.client.impl;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全HTTP客户端
 * 适用场景：支付接口、敏感数据传输
 * 特点：强制HTTPS、证书验证、请求签名
 */
public class SecureHttpClient implements HttpClient {
    private HttpClientConfig config;

    public SecureHttpClient(HttpClientConfig config) {
        this.config = config;
        initialize();
    }

    private void initialize() {
        System.out.println("Initializing SecureHttpClient...");
        System.out.println("  - SSL/TLS: enabled (TLS 1.2+)");
        System.out.println("  - Certificate Validation: strict");
        System.out.println("  - Request Signing: enabled");
        System.out.println("  - Response Verification: enabled");
        System.out.println("  - Connect Timeout: " + config.getConnectTimeout() + "ms");
        System.out.println("  - Read Timeout: " + config.getReadTimeout() + "ms");
    }

    @Override
    public String get(String url, Map<String, String> headers) {
        System.out.println("\n[SecureHttpClient] GET: " + url);

        // 添加安全相关的header
        Map<String, String> secureHeaders = addSecurityHeaders(headers);
        System.out.println("  Secure Headers: " + secureHeaders);

        // 验证URL必须是HTTPS
        if (!url.startsWith("https://")) {
            throw new SecurityException("SecureHttpClient requires HTTPS URLs");
        }

        // 验证SSL证书
        verifyCertificate(url);

        simulateNetworkDelay(200); // SSL握手需要更多时间

        return "{\"status\":\"success\",\"data\":\"Secure GET response\",\"encrypted\":true}";
    }

    @Override
    public String post(String url, String body, Map<String, String> headers) {
        System.out.println("\n[SecureHttpClient] POST: " + url);

        if (!url.startsWith("https://")) {
            throw new SecurityException("SecureHttpClient requires HTTPS URLs");
        }

        // 对请求体签名
        String signature = signRequest(body);
        Map<String, String> secureHeaders = addSecurityHeaders(headers);
        secureHeaders.put("X-Signature", signature);

        System.out.println("  Secure Headers: " + secureHeaders);
        System.out.println("  Request Signature: " + signature);

        verifyCertificate(url);
        simulateNetworkDelay(250);

        return "{\"status\":\"success\",\"data\":\"Secure POST response\",\"encrypted\":true}";
    }

    @Override
    public void close() {
        System.out.println("Closing SecureHttpClient and clearing security contexts...");
    }

    @Override
    public String getClientType() {
        return "SECURE";
    }

    private Map<String, String> addSecurityHeaders(Map<String, String> headers) {
        Map<String, String> secureHeaders = new HashMap<>(headers);
        secureHeaders.put("X-Security-Version", "1.0");
        secureHeaders.put("X-Timestamp", String.valueOf(System.currentTimeMillis()));
        return secureHeaders;
    }

    private void verifyCertificate(String url) {
        System.out.println("  ✓ SSL Certificate verified for: " + url);
    }

    private String signRequest(String body) {
        // 简化的签名逻辑（实际应使用HMAC-SHA256等）
        return "SHA256:" + Integer.toHexString(body.hashCode());
    }

    private void simulateNetworkDelay(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}