package net.okhotnikov.harald.protocols;

import net.okhotnikov.harald.model.BluetoothState;

public interface BluetoothStateListener {
    void onStateChanged(BluetoothState state);
    void enableBluetooth();
    void onBpm(int bpm);
}
