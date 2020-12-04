package net.okhotnikov.harald.service;

import android.os.Handler;

import net.okhotnikov.harald.protocols.Action;
import net.okhotnikov.harald.protocols.Job;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncService {
    public static final AsyncService instance = new AsyncService();
    private final ThreadPoolExecutor pool=new ThreadPoolExecutor(5,15,
            5, TimeUnit.MINUTES,new ArrayBlockingQueue<>(1000));

    private Handler handler;

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public <T,E> void perform(Job<T,E> job, Action<T> onSuccess, Action <E> onError){
        pool.execute(()-> job.action(
                result -> handler.post(()-> onSuccess.action(result)),
                error -> handler.post(()-> onError.action(error))
        ));
    }

    public <T,E> void execute(Job<T,E> job, Action<T> onSuccess, Action <E> onError){
        pool.execute(()-> job.action(
                onSuccess,
                onError
        ));
    }

    public void execute(Runnable r){
        pool.execute(r);
    }
}
