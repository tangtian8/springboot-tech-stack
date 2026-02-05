package top.tangtian.designpattern.chain.http.handler.impl;

import top.tangtian.designpattern.chain.http.handler.AbstractRequestHandler;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public class AuthenticationHandler extends AbstractRequestHandler {
    @Override
    protected void doHandle(HttpRequest request, HttpResponse response) {
        String token = request.getHeaders().get("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatusCode(401);
            response.setBody("Unauthorized: Missing or invalid token");
            response.setHandled(true);
            return;
        }

        String jwtToken = token.substring(7);
        if (!isValidToken(jwtToken)) {
            response.setStatusCode(401);
            response.setBody("Unauthorized: Invalid token");
            response.setHandled(true);
            return;
        }

        String userId = extractUserId(jwtToken);
        request.setAttribute("userId", userId);
        System.out.println("[AUTH] User authenticated: " + userId);
    }

    private boolean isValidToken(String token) {
        // TODO: 实现真实的JWT验证逻辑
        return token.length() > 10;
    }

    private String extractUserId(String token) {
        // TODO: 从JWT中提取用户ID
        return "user_" + token.substring(0, 5);
    }
}