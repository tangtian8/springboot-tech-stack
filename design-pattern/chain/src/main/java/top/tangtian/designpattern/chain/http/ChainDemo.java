package top.tangtian.designpattern.chain.http;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.tangtian.designpattern.chain.http.chain.FilterChain;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

@SpringBootApplication
public class ChainDemo {
    public static void main(String[] args) {
        FilterChain chain = new FilterChain();

        // 测试1: 正常请求
        System.out.println("=== Test 1: Normal Request ===");
        HttpRequest request1 = new HttpRequest("/products", "GET");
        request1.getHeaders().put("Authorization", "Bearer validtoken123456");
        HttpResponse response1 = chain.execute(request1);
        System.out.println("Response: " + response1.getStatusCode() + " - " + response1.getBody());
        System.out.println();

        // 测试2: 未授权请求
        System.out.println("=== Test 2: Unauthorized Request ===");
        HttpRequest request2 = new HttpRequest("/products", "GET");
        HttpResponse response2 = chain.execute(request2);
        System.out.println("Response: " + response2.getStatusCode() + " - " + response2.getBody());
        System.out.println();

        // 测试3: 权限不足
        System.out.println("=== Test 3: Forbidden Request ===");
        HttpRequest request3 = new HttpRequest("/users", "GET");
        request3.getHeaders().put("Authorization", "Bearer usertoken123456");
        HttpResponse response3 = chain.execute(request3);
        System.out.println("Response: " + response3.getStatusCode() + " - " + response3.getBody());
        System.out.println();

        // 测试4: 限流测试
        System.out.println("=== Test 4: Rate Limiting ===");
        for (int i = 0; i < 7; i++) {
            HttpRequest request4 = new HttpRequest("/products", "GET");
            request4.getHeaders().put("Authorization", "Bearer admintoken123456");
            HttpResponse response4 = chain.execute(request4);
            System.out.println("Request " + (i + 1) + " - Response: " +
                    response4.getStatusCode() + " - " + response4.getBody());
        }
    }
}