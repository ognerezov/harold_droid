package net.okhotnikov.harald.protocols;

public interface StressNotificator {
    void onStressAccumulated();
    void onStressReleased();
}
