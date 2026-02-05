package top.tangtian.designpattern.chain.http.handler.impl;

import top.tangtian.designpattern.chain.http.handler.AbstractRequestHandler;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateLimitHandler extends AbstractRequestHandler {
    private Map<String, List<Long>> requestTimes;
    private static final int MAX_REQUESTS = 5;
    private static final long TIME_WINDOW = 10000; // 10秒

    public RateLimitHandler() {
        this.requestTimes = new HashMap<>();
    }

    @Override
    protected void doHandle(HttpRequest request, HttpResponse response) {
        String userId = (String) request.getAttribute("userId");
        long currentTime = System.currentTimeMillis();

        requestTimes.putIfAbsent(userId, new ArrayList<>());
        List<Long> times = requestTimes.get(userId);

        // 移除时间窗口外的请求
        times.removeIf(time -> currentTime - time > TIME_WINDOW);

        if (times.size() >= MAX_REQUESTS) {
            response.setStatusCode(429);
            response.setBody("Too Many Requests: Rate limit exceeded");
            response.setHandled(true);
            return;
        }

        times.add(currentTime);
        System.out.println("[RATE_LIMIT] Request count: " + times.size() + "/" + MAX_REQUESTS);
    }
}