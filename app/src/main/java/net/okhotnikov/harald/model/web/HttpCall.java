package net.okhotnikov.harald.model.web;

import net.okhotnikov.harald.protocols.Action;

import java.util.HashMap;
import java.util.Map;
import static net.okhotnikov.harald.model.Literals.*;

public class HttpCall <S,R> {
    private static final Map<String,String> DEFAULT_HEADERS = new HashMap<>();
    public ApiEndpoint endpoint;
    public HttpMethod method;
    public S toSend;
    public Action<R> callBack;
    public Action<Error> error;
    public Map<String,String> headers;

    static {
        DEFAULT_HEADERS.put(CONTENT,APPLICATION_JSON);
    }

    public HttpCall() {
    }

    public HttpCall(ApiEndpoint endpoint, HttpMethod method, S toSend, Action<R> callBack, Action<Error> error) {
        this.endpoint = endpoint;
        this.method = method;
        this.toSend = toSend;
        this.callBack = callBack;
        this.error = error;
        headers = DEFAULT_HEADERS;
    }

    public HttpCall(ApiEndpoint endpoint, HttpMethod method, S toSend, Action<R> callBack, Action<Error> error, Map<String, String> headers) {
        this.endpoint = endpoint;
        this.method = method;
        this.toSend = toSend;
        this.callBack = callBack;
        this.error = error;
        this.headers = headers;
    }

    public ApiEndpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(ApiEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public S getToSend() {
        return toSend;
    }

    public void setToSend(S toSend) {
        this.toSend = toSend;
    }

    public Action<R> getCallBack() {
        return callBack;
    }

    public void setCallBack(Action<R> callBack) {
        this.callBack = callBack;
    }

    public Action<Error> getError() {
        return error;
    }

    public void setError(Action<Error> error) {
        this.error = error;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
