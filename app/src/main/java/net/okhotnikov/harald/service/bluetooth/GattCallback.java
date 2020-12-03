package net.okhotnikov.harald.service.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import net.okhotnikov.harald.model.BluetoothState;
import net.okhotnikov.harald.model.processing.HartData;

import java.util.ArrayList;
import java.util.List;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static net.okhotnikov.harald.service.bluetooth.BluetoothService.heartRateCharacteristicId;
import static net.okhotnikov.harald.service.bluetooth.BluetoothService.heartRateServiceId;
import static net.okhotnikov.harald.service.bluetooth.BluetoothService.settingsDescriptorId;

public class GattCallback extends BluetoothGattCallback {

    final BluetoothService owner;

    public GattCallback(BluetoothService owner) {
        this.owner = owner;
    }

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
            owner.scan();
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
        owner.getStateListener().onStateChanged(BluetoothState.connected);

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
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
          //  Log.d("bluetooth data","Heart rate format UINT16.");
            offset = 3;
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
          //  Log.d("bluetooth data","Heart rate format UINT8.");
            offset = 2;
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        owner.getStateListener().onBpm(heartRate);
//        Log.d("bluetooth data","Received heart rate: " + heartRate);
//                if ((flag & 0x08) != 0) {
//                    // calories present
//                    energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
//                    offset += 2;
//                    Log.d("bluetooth data","Received energy: " + energy);
//                }
        if ((flag & 0x10) != 0){
            rr_count = ((characteristic.getValue()).length - offset) / 2;

            List<HartData> list = new ArrayList<>();
            for (int i = 0; i < rr_count; i++){
                double rr = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset) / 1000.0;
                offset += 2;
                list.add(new HartData(rr,heartRate));
            }
            owner.getStateListener().onRR(list);
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
}
