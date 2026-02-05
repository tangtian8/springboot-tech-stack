package top.tangtian.designpattern.chain.http.handler.impl;

import top.tangtian.designpattern.chain.http.handler.AbstractRequestHandler;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public class BusinessHandler extends AbstractRequestHandler {
    @Override
    protected void doHandle(HttpRequest request, HttpResponse response) {
        String userId = (String) request.getAttribute("userId");

        String result = processBusinessLogic(request.getPath(), userId);

        response.setStatusCode(200);
        response.setBody(result);
        response.setHandled(true);

        System.out.println("[BUSINESS] Request processed successfully");
    }

    private String processBusinessLogic(String path, String userId) {
        return String.format("Success: %s accessed by %s", path, userId);
    }
}