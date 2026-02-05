package top.tangtian.designpattern.chain.http.handler;

import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public interface RequestHandler {
    void setNext(RequestHandler next);
    void handle(HttpRequest request, HttpResponse response);
}
