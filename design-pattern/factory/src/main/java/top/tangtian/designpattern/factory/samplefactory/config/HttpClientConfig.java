package top.tangtian.designpattern.factory.samplefactory.config;

public class HttpClientConfig {
    private int connectTimeout;    // 连接超时（毫秒）
    private int readTimeout;       // 读取超时（毫秒）
    private int maxRetries;        // 最大重试次数
    private boolean enableSSL;     // 是否启用SSL
    private int maxConnections;    // 最大连接数

    public HttpClientConfig() {
        // 默认配置
        this.connectTimeout = 5000;
        this.readTimeout = 10000;
        this.maxRetries = 0;
        this.enableSSL = false;
        this.maxConnections = 100;
    }

    // Getters and Setters
    public int getConnectTimeout() { return connectTimeout; }
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() { return readTimeout; }
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getMaxRetries() { return maxRetries; }
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public boolean isEnableSSL() { return enableSSL; }
    public void setEnableSSL(boolean enableSSL) {
        this.enableSSL = enableSSL;
    }

    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public String toString() {
        return String.format("HttpClientConfig{connectTimeout=%d, readTimeout=%d, " +
                        "maxRetries=%d, enableSSL=%b, maxConnections=%d}",
                connectTimeout, readTimeout, maxRetries,
                enableSSL, maxConnections);
    }
}