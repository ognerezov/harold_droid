package net.okhotnikov.harald.model.processing;

import net.okhotnikov.harald.protocols.Action;

import java.util.function.Consumer;

public class RRProcessor {
    private final DoubleRing ring;
    private final int capacity;
    private final double rrStep;
    private final Action<Double> onProcessed;

    public RRProcessor(int capacity, double rrStep, Action<Double> onProcessed) {
        this.ring = new DoubleRing(capacity);
        this.capacity = capacity;
        this.rrStep = rrStep;
        this.onProcessed = onProcessed;
    }

    public void add(double rr){
        ring.add(rr);

        if (ring.isFull()){
            process();
        }
    }

    public void process() {
        double [] data = ring.sortedSnapshot();

        double Mo = data[0];
        int n = 0;
        int N = data.length;
        int nCurrent = 0;

        double current = Mo;
        double first = current;

        double next = current + rrStep;
        int i = 0;


        while (i < N){
            if (current <= data[i] && data[i] < next){
                nCurrent ++;
                i ++;
            } else{
                if (nCurrent >= n){
                    n = nCurrent;
                    Mo = current;
                }
                nCurrent = 0;
                current = next;
                next = next + rrStep;
            }
        }

        double AMo  = (100.0 * n) / N;
        double square = 2 * Mo * (next - first);
        double BayevskiyIndex = AMo/square;

        onProcessed.action(BayevskiyIndex);
    }

    public void clear(){
        ring.clear();
    }

}
