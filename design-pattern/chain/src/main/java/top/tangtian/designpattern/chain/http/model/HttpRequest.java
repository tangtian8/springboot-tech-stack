package top.tangtian.designpattern.chain.http.model;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String path;
    private String method;
    private Map<String, String> headers;
    private String body;
    private Map<String, Object> attributes;

    public HttpRequest(String path, String method) {
        this.path = path;
        this.method = method;
        this.headers = new HashMap<>();
        this.attributes = new HashMap<>();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}