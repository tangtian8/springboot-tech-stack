package top.tangtian.designpattern.chain.http.handler;

import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public abstract class AbstractRequestHandler implements RequestHandler {
    protected RequestHandler next;

    @Override
    public void setNext(RequestHandler next) {
        this.next = next;
    }

    protected void passToNext(HttpRequest request, HttpResponse response) {
        if (next != null && !response.isHandled()) {
            next.handle(request, response);
        }
    }

    protected abstract void doHandle(HttpRequest request, HttpResponse response);

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        doHandle(request, response);
        passToNext(request, response);
    }
}
