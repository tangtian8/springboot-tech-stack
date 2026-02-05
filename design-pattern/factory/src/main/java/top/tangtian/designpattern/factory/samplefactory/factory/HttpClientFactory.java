package top.tangtian.designpattern.factory.samplefactory.factory;

import top.tangtian.designpattern.factory.samplefactory.client.HttpClient;
import top.tangtian.designpattern.factory.samplefactory.client.impl.FastHttpClient;
import top.tangtian.designpattern.factory.samplefactory.client.impl.RetryableHttpClient;
import top.tangtian.designpattern.factory.samplefactory.client.impl.SecureHttpClient;
import top.tangtian.designpattern.factory.samplefactory.client.impl.StandardHttpClient;
import top.tangtian.designpattern.factory.samplefactory.config.HttpClientConfig;

/**
 * HTTP客户端简单工厂
 *
 * 为什么用简单工厂？
 * 1. 客户端类型固定（4种），不会频繁增加
 * 2. 创建逻辑简单，只需传入配置
 * 3. 统一的创建入口，方便管理
 * 4. 使用频繁，静态方法更方便
 */
public class HttpClientFactory {

    /**
     * 客户端类型枚举
     */
    public enum ClientType {
        STANDARD,    // 标准客户端
        FAST,        // 快速客户端
        SECURE,      // 安全客户端
        RETRYABLE    // 可重试客户端
    }

    /**
     * 创建HTTP客户端
     *
     * @param type 客户端类型
     * @param config 配置
     * @return HTTP客户端实例
     */
    public static HttpClient createClient(ClientType type, HttpClientConfig config) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Creating HttpClient: " + type);
        System.out.println("=".repeat(60));

        HttpClient client;

        switch (type) {
            case STANDARD:
                client = new StandardHttpClient(config);
                break;

            case FAST:
                // 快速客户端需要更大的连接池
                if (config.getMaxConnections() < 200) {
                    config.setMaxConnections(200);
                }
                // 更短的超时时间
                if (config.getConnectTimeout() > 2000) {
                    config.setConnectTimeout(2000);
                }
                client = new FastHttpClient(config);
                break;

            case SECURE:
                // 强制启用SSL
                config.setEnableSSL(true);
                // 更长的超时时间（SSL握手需要时间）
                if (config.getConnectTimeout() < 10000) {
                    config.setConnectTimeout(10000);
                }
                client = new SecureHttpClient(config);
                break;

            case RETRYABLE:
                // 设置默认重试次数
                if (config.getMaxRetries() == 0) {
                    config.setMaxRetries(3);
                }
                client = new RetryableHttpClient(config);
                break;

            default:
                throw new IllegalArgumentException("Unknown client type: " + type);
        }

        System.out.println("=".repeat(60));
        return client;
    }

    /**
     * 根据场景创建客户端（便捷方法）
     */
    public static HttpClient createForPayment() {
        System.out.println("\n>>> Creating client for PAYMENT scenario");
        HttpClientConfig config = new HttpClientConfig();
        config.setConnectTimeout(10000);
        config.setReadTimeout(30000);
        config.setEnableSSL(true);
        return createClient(ClientType.SECURE, config);
    }

    public static HttpClient createForSMS() {
        System.out.println("\n>>> Creating client for SMS scenario");
        HttpClientConfig config = new HttpClientConfig();
        config.setMaxRetries(3);
        config.setConnectTimeout(5000);
        config.setReadTimeout(10000);
        return createClient(ClientType.RETRYABLE, config);
    }

    public static HttpClient createForSeckill() {
        System.out.println("\n>>> Creating client for SECKILL scenario");
        HttpClientConfig config = new HttpClientConfig();
        config.setConnectTimeout(1000);
        config.setReadTimeout(3000);
        config.setMaxConnections(500);
        return createClient(ClientType.FAST, config);
    }

    public static HttpClient createForInternal() {
        System.out.println("\n>>> Creating client for INTERNAL scenario");
        HttpClientConfig config = new HttpClientConfig();
        return createClient(ClientType.STANDARD, config);
    }
}