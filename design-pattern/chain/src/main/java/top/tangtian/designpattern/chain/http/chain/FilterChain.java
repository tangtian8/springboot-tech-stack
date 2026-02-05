package top.tangtian.designpattern.chain.http.chain;

import top.tangtian.designpattern.chain.http.handler.RequestHandler;
import top.tangtian.designpattern.chain.http.handler.impl.*;
import top.tangtian.designpattern.chain.http.model.HttpRequest;
import top.tangtian.designpattern.chain.http.model.HttpResponse;

public class FilterChain {
    private RequestHandler firstHandler;

    public FilterChain() {
        buildChain();
    }

    private void buildChain() {
        LoggingHandler logging = new LoggingHandler();
        AuthenticationHandler auth = new AuthenticationHandler();
        AuthorizationHandler authz = new AuthorizationHandler();
        RateLimitHandler rateLimit = new RateLimitHandler();
        BusinessHandler business = new BusinessHandler();

        logging.setNext(auth);
        auth.setNext(authz);
        authz.setNext(rateLimit);
        rateLimit.setNext(business);

        this.firstHandler = logging;
    }

    public HttpResponse execute(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        firstHandler.handle(request, response);
        return response;
    }
}