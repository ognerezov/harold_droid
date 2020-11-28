package net.okhotnikov.harald.protocols;

public interface Action <T> {
    void perform(T t);
}
