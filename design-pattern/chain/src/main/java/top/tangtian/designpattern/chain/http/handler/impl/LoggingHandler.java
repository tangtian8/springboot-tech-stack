package top.tangtian.designpattern.chain.http.handler.impl;

import top.tangtian.designpattern.chain.http.handler.AbstractRequestHandler;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public class LoggingHandler extends AbstractRequestHandler {
    @Override
    protected void doHandle(HttpRequest request, HttpResponse response) {
        System.out.println("[LOG] " + request.getMethod() + " " + request.getPath());
        System.out.println("[LOG] Headers: " + request.getHeaders());
    }
}