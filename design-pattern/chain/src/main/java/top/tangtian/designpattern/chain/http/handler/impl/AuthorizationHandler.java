package top.tangtian.designpattern.chain.http.handler.impl;

import top.tangtian.designpattern.chain.http.handler.AbstractRequestHandler;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AuthorizationHandler extends AbstractRequestHandler {
    private Map<String, Set<String>> rolePermissions;

    public AuthorizationHandler() {
        rolePermissions = new HashMap<>();
        rolePermissions.put("admin", Set.of("/users", "/orders", "/products"));
        rolePermissions.put("user", Set.of("/orders", "/products"));
    }

    @Override
    protected void doHandle(HttpRequest request, HttpResponse response) {
        String userId = (String) request.getAttribute("userId");
        String role = getUserRole(userId);

        if (!hasPermission(role, request.getPath())) {
            response.setStatusCode(403);
            response.setBody("Forbidden: Insufficient permissions");
            response.setHandled(true);
            return;
        }

        System.out.println("[AUTHZ] Authorization passed for role: " + role);
    }

    private String getUserRole(String userId) {
        // TODO: 从数据库获取用户角色
        return userId.contains("admin") ? "admin" : "user";
    }

    private boolean hasPermission(String role, String path) {
        Set<String> permissions = rolePermissions.get(role);
        if (permissions == null) return false;

        return permissions.stream().anyMatch(path::startsWith);
    }
}
