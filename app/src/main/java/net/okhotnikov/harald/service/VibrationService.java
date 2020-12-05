package net.okhotnikov.harald.service;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.protocols.StressNotificator;

public class VibrationService  {

    private static final int DEFAULT_DURATION = 500;
    private final Vibrator v;
    private final MainActivity activity;

    public VibrationService(MainActivity activity) {
        this.activity= activity;
        this.v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    // Vibrate for 500 milliseconds
    public void vibrate(int duration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(duration);
        }
    }

    public void cancel(){
        v.cancel();
    }

    public void vibrate(){
        vibrate(DEFAULT_DURATION);
    }

}
