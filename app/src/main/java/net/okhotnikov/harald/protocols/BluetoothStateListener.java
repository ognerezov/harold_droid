package net.okhotnikov.harald.protocols;

import net.okhotnikov.harald.model.BluetoothState;
import net.okhotnikov.harald.model.processing.HartData;

import java.util.List;

public interface BluetoothStateListener {
    void onStateChanged(BluetoothState state);
    void enableBluetooth();
    void onBpm(int bpm);
    void onRR(List<HartData> list);
    void onRR(HartData data);
    void onStressIndex(double bi);
}
