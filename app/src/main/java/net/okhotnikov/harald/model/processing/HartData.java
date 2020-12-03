package net.okhotnikov.harald.model.processing;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class HartData {
    public static final HartData NaN = new HartData(Double.NaN,0);
    public Timestamp time;
    public double rr;
    public int bpm;

    public HartData(double rr, int bpm) {
        this.rr = rr;
        this.bpm = bpm;
        Date date = new Date();
        this.time = new Timestamp(date.getTime());
    }

    public boolean isNan(){
        return Double.isNaN(rr);
    }

    public HartData(Timestamp time, double rr, int bpm) {
        this.time = time;
        this.rr = rr;
        this.bpm = bpm;
    }

    public HartData() {
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public double getRr() {
        return rr;
    }

    public void setRr(double rr) {
        this.rr = rr;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    @Override
    public String toString() {
        return "HartData{" +
                "time=" + time +
                ", rr=" + rr +
                ", bpm=" + bpm +
                '}';
    }
}
