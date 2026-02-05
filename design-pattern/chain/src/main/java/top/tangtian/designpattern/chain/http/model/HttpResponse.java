package top.tangtian.designpattern.chain.http.model;

public class HttpResponse {
    private int statusCode;
    private String body;
    private boolean handled;

    public HttpResponse() {
        this.statusCode = 200;
        this.handled = false;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }
}
