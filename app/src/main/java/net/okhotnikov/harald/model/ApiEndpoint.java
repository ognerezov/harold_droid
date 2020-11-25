package net.okhotnikov.harald.model;

/**
 * Created by ogner on 23-Jan-18.
 */

public enum ApiEndpoint {
    REGISTER("pub/register"),
    LOGIN("pub/login"),
    REFRESH("refresh"),
    GET_UPLOAD_LINKS("get-upload-links"),
    TASK_LIST("get-task-list"),
    TASKS("usr/tasks"),
    BALANCE("usr/balance"),
    MY_TASKS("usr/tasks/my");


    private String value;
    ApiEndpoint(String value) {
        this.value=value;
    }


    @Override
    public String toString() {
        return value;
    }
}
