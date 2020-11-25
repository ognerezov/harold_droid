package net.okhotnikov.harald.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.model.BluetoothState;
import net.okhotnikov.harald.protocols.BluetoothStateListener;

public class BluetoothService {
    private final MainActivity activity;
    private final BluetoothStateListener stateListener;
    private BluetoothState state;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;

    public BluetoothService(MainActivity activity) {
        this.activity = activity;
        this.stateListener = activity;

        if (!activity
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            setState(BluetoothState.disabled);
            return;
        }

        bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            activity.enableBluetooth();
            return;
        }

        System.out.println("!!!!");
        stateListener.onStateChanged(BluetoothState.scanning);
    }

    public BluetoothState getState() {
        return state;
    }

    public void setState(BluetoothState state) {
        stateListener.onStateChanged(state);
        this.state = state;
    }

    public void resetAdapter() {
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            activity.enableBluetooth();
            return;
        }

        System.out.println("****");
        stateListener.onStateChanged(BluetoothState.scanning);
    }
}
