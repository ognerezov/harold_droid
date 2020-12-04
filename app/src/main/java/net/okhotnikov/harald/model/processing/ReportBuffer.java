package net.okhotnikov.harald.model.processing;

import net.okhotnikov.harald.protocols.Action;
import net.okhotnikov.harald.protocols.SourceHolder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReportBuffer<T> implements SourceHolder<T> {
    private final int capacity;
    private final LinkedList<List<T>> queue;
    private List<T> current;
    private final Action<SourceHolder<T>> reporter;
    public static final int MAX_SIZE = 1000;


    public ReportBuffer(int capacity, Action<SourceHolder<T>> reporter) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
        createCurrent();
        this.reporter = reporter;
    }

    private void createCurrent() {
        current = new ArrayList<>(capacity);
        queue.add(current);
        if(queue.size() > MAX_SIZE){
            queue.poll();
        }
    }

    public void add(T t){
        current.add(t);
        if(current.size() >= capacity){
            reporter.action(this);
            createCurrent();
        }

    }

    public int currentLoad(){
        return (queue.size() -1) * capacity + current.size();
    }

    @Override
    public List<T> take() {
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    @Override
    public void restore(List<T> list) {
        queue.addFirst(list);
    }
}
