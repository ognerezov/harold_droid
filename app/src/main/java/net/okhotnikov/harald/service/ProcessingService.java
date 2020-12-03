package net.okhotnikov.harald.service;

import android.util.Log;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.model.processing.HartData;
import net.okhotnikov.harald.model.processing.RRProcessor;
import net.okhotnikov.harald.protocols.BluetoothStateListener;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ProcessingService implements Runnable {
    private final MainActivity activity;
    private final int capacity;
    private final RRProcessor processor;
    private final double RR_STEP = 0.05;
    private final BluetoothStateListener listener;
    private final BlockingQueue<HartData> blockingQueue;
    private boolean runnig = true;
    private final Thread thread;

    public ProcessingService(MainActivity activity, int capacity) {
        this.activity = activity;
        this.capacity = capacity;
        this.listener = activity;
        this.blockingQueue = new LinkedBlockingQueue<>(capacity);
        this.processor = new RRProcessor(capacity,
                RR_STEP,
                aDouble -> activity.
                        getHandler().
                        post(()-> listener.onStressIndex(aDouble)));
        this.thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        while (!add(HartData.NaN));
        runnig = false;
    }

    public boolean add(HartData data){
        return blockingQueue.offer(data);
    }

    public void add(List<HartData> list){
        for(HartData data: list){
            while (!add(data));
        }
//        Log.d("added to queue", String.valueOf(list.size()));
//        Log.d("queue size", String.valueOf(blockingQueue.size()));
    }

    @Override
    public void run() {
        while (runnig){
            try {
                HartData value = blockingQueue.take();
//                Log.d("from thread", value.toString());

                if (!value.isNan())
                    processor.add(value.rr);
            } catch (InterruptedException e) {
                runnig = false;
                Thread.currentThread().interrupt();
            }
        }
    }
}
