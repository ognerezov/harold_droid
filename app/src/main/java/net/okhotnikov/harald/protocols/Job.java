package net.okhotnikov.harald.protocols;

public interface Job <T,E> {
    void action(Action<T> onSuccess, Action <E> onError);
}
