package top.tangtian.designpattern.factory.samplefactory.client;

import java.util.Map;

/**
 * HTTP客户端接口
 */
public interface HttpClient {

    /**
     * GET请求
     */
    String get(String url, Map<String, String> headers);

    /**
     * POST请求
     */
    String post(String url, String body, Map<String, String> headers);

    /**
     * 关闭客户端
     */
    void close();

    /**
     * 获取客户端类型
     */
    String getClientType();
}