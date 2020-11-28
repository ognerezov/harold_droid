package net.okhotnikov.harald.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
    private UUID heartRateCharacteristicId = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
    private UUID settingsDescriptorId =      UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private UUID heartRateServiceId =        UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");


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

    private void scan() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        stateListener.onStateChanged(BluetoothState.scanning);
        sensor = bluetoothAdapter.getRemoteDevice(address);
        System.out.println(sensor);
        gatt = sensor.connectGatt(activity, true, new BluetoothGattCallback() {
            @Override
            public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            }

            @Override
            public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyRead(gatt, txPhy, rxPhy, status);
            }

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);

                if (newState == STATE_CONNECTED){
                    System.out.println("CONNECTED!!!");
                    gatt.discoverServices();
                } else {
                    System.out.println("NOT CONNECTED, reason: " +status);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                System.out.println("SERVICE DISCOVERED!!!");
                BluetoothGattService service = gatt.getService(heartRateServiceId);
//                for(BluetoothGattService service: gatt.getServices()){
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(heartRateCharacteristicId);
//                    System.out.println(characteristic);
                    if (characteristic == null)
                        return;

                    System.out.println("found characteristic");

                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(settingsDescriptorId);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);

                    gatt.setCharacteristicNotification(characteristic, true);

                    //Enabled remote notifications
//                    for(BluetoothGattDescriptor descriptor: characteristic.getDescriptors()){
//                        System.out.println(descriptor.getUuid());
//                    }
//                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
//                System.out.println(characteristic.getUuid());
//                System.out.println(characteristic.getValue().length);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                System.out.println(characteristic.getUuid());
                System.out.println(characteristic.getValue().length);
                parseData(characteristic);
            }

            private void parseData(BluetoothGattCharacteristic characteristic) {
                int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                int format = -1;
                int energy = -1;
                int offset = 1;
                int rr_count = 0;
                List<Integer> rrValues = new ArrayList<>();
                if ((flag & 0x01) != 0) {
                    format = BluetoothGattCharacteristic.FORMAT_UINT16;
                    Log.d("bluetooth data","Heart rate format UINT16.");
                    offset = 3;
                } else {
                    format = BluetoothGattCharacteristic.FORMAT_UINT8;
                    Log.d("bluetooth data","Heart rate format UINT8.");
                    offset = 2;
                }
                final int heartRate = characteristic.getIntValue(format, 1);
                Log.d("bluetooth data","Received heart rate: " + heartRate);
//                if ((flag & 0x08) != 0) {
//                    // calories present
//                    energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
//                    offset += 2;
//                    Log.d("bluetooth data","Received energy: " + energy);
//                }
                if ((flag & 0x10) != 0){
                    // RR stuff.
                    rr_count = ((characteristic.getValue()).length - offset) / 2;
                    for (int i = 0; i < rr_count; i++){
                        rrValues.add(characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset));
                        offset += 2;
                        Log.d("bluetooth data","Received RR: " + rrValues.get(i));
                    }
                }
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }

            @Override
            public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                super.onMtuChanged(gatt, mtu, status);
            }
        });
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
