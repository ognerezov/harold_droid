package net.okhotnikov.harald.model.processing;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HartData {
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    public static final HartData NaN = new HartData(Double.NaN,0);
    public Date date;
    public double rr;
    public int bpm;

    public HartData(double rr, int bpm) {
        this.rr = rr;
        this.bpm = bpm;
        this.date = new Date();
    }

    @JsonIgnore
    public boolean isNan(){
        return Double.isNaN(rr);
    }

    public HartData(Date time, double rr, int bpm) {
        this.date = time;
        this.rr = rr;
        this.bpm = bpm;
    }

    public HartData() {
    }

    public String getTime(){
        return SIMPLE_DATE_FORMAT.format(date);
    }

    @JsonIgnore
    public Date getDate() {
        return date;
    }

    @JsonIgnore
    public void setDate(Date date) {
        this.date = date;
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
                "time=" + getTime() +
                ", rr=" + rr +
                ", bpm=" + bpm +
                '}';
    }
}
