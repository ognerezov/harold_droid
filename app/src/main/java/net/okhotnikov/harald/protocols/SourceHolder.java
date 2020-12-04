package net.okhotnikov.harald.protocols;

import java.util.List;

public interface SourceHolder<T> {
    List<T> take();
    void restore(List<T> list);
}
