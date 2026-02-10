package top.tangtian.designpattern.builder.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: springboot-tech-stack
 * @description: http请求对象
 * @author: tangtian
 * @create: 2026-02-09 17:00
 **/
public class HttpRequest {
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final Map<String, String> queryParams;
    private final String body;
    private final int connectTimeout;
    private final int readTimeout;
    private final boolean followRedirects;

    private HttpRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.queryParams = builder.queryParams;
        this.body = builder.body;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.followRedirects = builder.followRedirects;
    }

    // Getters
    public String getUrl() { return url; }
    public String getMethod() { return method; }
    public Map<String, String> getHeaders() { return new HashMap<>(headers); }
    public Map<String, String> getQueryParams() { return new HashMap<>(queryParams); }
    public String getBody() { return body; }
    public int getConnectTimeout() { return connectTimeout; }
    public int getReadTimeout() { return readTimeout; }
    public boolean isFollowRedirects() { return followRedirects; }

    /**
     * 获取完整URL（包含query参数）
     */
    public String getFullUrl() {
        if (queryParams.isEmpty()) {
            return url;
        }

        StringBuilder fullUrl = new StringBuilder(url);
        fullUrl.append("?");

        queryParams.forEach((key, value) ->
                fullUrl.append(key).append("=").append(value).append("&")
        );

        // 移除最后的&
        fullUrl.deleteCharAt(fullUrl.length() - 1);

        return fullUrl.toString();
    }

    @Override
    public String toString() {
        return String.format("HttpRequest{method=%s, url=%s, headers=%s, body=%s}",
                method, getFullUrl(), headers, body);
    }

    /**
     * 建造者
     */
    public static class Builder {
        // 必需参数
        private final String url;
        private String method = "GET";

        // 可选参数
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> queryParams = new HashMap<>();
        private String body;
        private int connectTimeout = 5000;
        private int readTimeout = 10000;
        private boolean followRedirects = true;

        public Builder(String url) {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL cannot be empty");
            }
            this.url = url;
        }

        public Builder method(String method) {
            this.method = method.toUpperCase();
            return this;
        }

        public Builder get() {
            this.method = "GET";
            return this;
        }

        public Builder post() {
            this.method = "POST";
            return this;
        }

        public Builder put() {
            this.method = "PUT";
            return this;
        }

        public Builder delete() {
            this.method = "DELETE";
            return this;
        }

        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder contentType(String contentType) {
            return header("Content-Type", contentType);
        }

        public Builder authorization(String token) {
            return header("Authorization", "Bearer " + token);
        }

        public Builder basicAuth(String username, String password) {
            String auth = username + ":" + password;
            String encodedAuth = java.util.Base64.getEncoder()
                    .encodeToString(auth.getBytes());
            return header("Authorization", "Basic " + encodedAuth);
        }

        public Builder queryParam(String name, String value) {
            this.queryParams.put(name, value);
            return this;
        }

        public Builder queryParams(Map<String, String> params) {
            this.queryParams.putAll(params);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder jsonBody(String json) {
            this.body = json;
            return contentType("application/json");
        }

        public Builder connectTimeout(int milliseconds) {
            if (milliseconds < 0) {
                throw new IllegalArgumentException("Timeout must be non-negative");
            }
            this.connectTimeout = milliseconds;
            return this;
        }

        public Builder readTimeout(int milliseconds) {
            if (milliseconds < 0) {
                throw new IllegalArgumentException("Timeout must be non-negative");
            }
            this.readTimeout = milliseconds;
            return this;
        }

        public Builder followRedirects(boolean follow) {
            this.followRedirects = follow;
            return this;
        }

        public HttpRequest build() {
            // 验证
            if ("POST".equals(method) || "PUT".equals(method)) {
                if (body == null) {
                    System.out.println("Warning: " + method + " request without body");
                }
            }

            return new HttpRequest(this);
        }
    }
}