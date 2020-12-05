package net.okhotnikov.harald.model.processing;

import net.okhotnikov.harald.protocols.StressNotificator;

public class StressAnalyzer {
    public static final double stressEdge = 200;
    public static final double accumulatedEdge = 3000;
    private double sum = 0;
    private final StressNotificator notificator;

    public StressAnalyzer(StressNotificator notificator) {
        this.notificator = notificator;
    }

    public double getSum() {
        return sum;
    }

    private boolean aboveEdge (double stress) {
        return stress > stressEdge;
    }

    public void add(double element){
        if (! aboveEdge(element)){
            if(sum > 0)
                notificator.onStressReleased();;
            sum = 0;
            return;
        }
        sum += element - stressEdge;

        if(sum < accumulatedEdge)
            return;

        notificator.onStressAccumulated();
    }
}
