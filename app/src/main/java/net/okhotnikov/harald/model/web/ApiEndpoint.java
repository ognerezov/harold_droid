package net.okhotnikov.harald.model.web;

public enum ApiEndpoint {
    REGISTER("pub/register"),
    LOGIN("pub/login"),
    REFRESH("refresh"),
    STRESS("data/stress"),
    HEART("data/heart");

    public static final String SERVER_URL = "http://10.100.102.14:8888/";

    private String value;
    ApiEndpoint(String value) {
        this.value=value;
    }


    @Override
    public String toString() {
        return SERVER_URL + value;
    }
}
