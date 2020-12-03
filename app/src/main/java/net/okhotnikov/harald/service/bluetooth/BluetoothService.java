package net.okhotnikov.harald.service.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.ParcelUuid;
import android.util.Log;

import net.okhotnikov.harald.MainActivity;
import net.okhotnikov.harald.model.BluetoothState;
import net.okhotnikov.harald.protocols.BluetoothStateListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_BALANCED;
import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;

public class BluetoothService extends ScanCallback {
    private final MainActivity activity;
    private final BluetoothStateListener stateListener;
    private BluetoothState state;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothLeScanner bluetoothLeScanner;
    private final List<ScanFilter> filters;
    private final ScanSettings scanSettings;
    private HashSet<UUID> uuids = new HashSet<>();
    private String address = "DC:7D:9B:09:53:1E";
    private BluetoothDevice sensor;
    private BluetoothGatt gatt;
    public static final UUID heartRateCharacteristicId = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    public static final UUID settingsDescriptorId =      UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID heartRateServiceId =        UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");


    public BluetoothService(MainActivity activity) {
        this.activity = activity;
        this.stateListener = activity;

        if (!activity
                .getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            setState(BluetoothState.disabled);
            filters = null;
            scanSettings = null;
            return;
        }

        filters = new ArrayList<>();
        UUID uuid = UUID.fromString("F000180D-0451-4000-B000-000000000000");
        ParcelUuid parcelUuid = new ParcelUuid(uuid);
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(parcelUuid);
        ScanFilter filter = builder.build();
        filters.add(filter);

        ScanSettings.Builder sb = new ScanSettings.Builder();
        sb.setScanMode(SCAN_MODE_BALANCED);
        scanSettings = sb.build();

        bluetoothManager =
                (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            activity.enableBluetooth();
            return;
        }

        System.out.println("!!!!");
        scan();
    }

    public BluetoothStateListener getStateListener() {
        return stateListener;
    }

    public void scan() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        stateListener.onStateChanged(BluetoothState.scanning);
        sensor = bluetoothAdapter.getRemoteDevice(address);

        gatt = sensor.connectGatt(activity, true, new GattCallback(this));
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
        scan();
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);
        if (result.getDevice() == null || result.getDevice().getUuids() == null){
            return;
        }
        for(ParcelUuid parcelUuid: result.getDevice().getUuids()){
            if(!uuids.contains(parcelUuid.getUuid())){
                System.out.println(parcelUuid.getUuid());
                uuids.add(parcelUuid.getUuid());
            }
        }
    }

    public void stop() {
        gatt.close();
        gatt = null;

    }
}
/*
  le scanner not in use for now
 */
//        for (BluetoothDevice device: bluetoothAdapter.getBondedDevices()){
//            String name = device.getName();
//
//            if (device == null || device.getUuids() == null)
//                continue;
////            for(ParcelUuid parcelUuid: device.getUuids()) {
////                System.out.println(device.getName() + " " + parcelUuid.getUuid());
////            }
//        }
//        AsyncService.instance.<String,String>perform(
//                (onResult,onError)->{
//                    bluetoothLeScanner.startScan(new ScanCallback() {
//
//
//                        @Override
//                        public void onScanResult(int callbackType, ScanResult result) {
//                            super.onScanResult(callbackType, result);
//                            onResult(result);
//                        }
//
//                        private void onResult(ScanResult result) {
//                            if (result.getDevice() == null || result.getDevice().getUuids() == null){
//                                return;
//                            }
//                            String device = result.getDevice().getName();
//                            device = device == null ? "" : " " + device;
//                            for(ParcelUuid parcelUuid: result.getDevice().getUuids()){
//                                if(!uuids.contains(parcelUuid.getUuid())){
//                                    onResult.perform(parcelUuid.getUuid().toString() +device);
//                                    uuids.add(parcelUuid.getUuid());
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onBatchScanResults(List<ScanResult> results) {
//                            super.onBatchScanResults(results);
//
//                            for(ScanResult result : results){
//                                if (result != null){
//                                    onResult(result);
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onScanFailed(int errorCode) {
//                            super.onScanFailed(errorCode);
//                            onResult.perform("scan failed "+ errorCode);
//                        }
//                    });
//                }, result ->{
//                    Log.d("bluetooth result",result);
//                }, error->{
//                    Log.e("bluetooth error",error);
//                }
//        );

//        bluetoothLeScanner.startScan(this);