package net.okhotnikov.harald.model;

import net.okhotnikov.harald.R;

public enum BluetoothState {
    off(R.string.bluetooth_off, R.color.red, R.drawable.bluetooth_disabled),
    disabled(R.string.bluetooth_disabled, R.color.gray, R.drawable.bluetooth_disabled),
    pending(R.string.bluetooth_pending, R.color.gray, R.drawable.bluetooth_searching),
    scanning(R.string.bluetooth_scanning, R.color.teal_700, R.drawable.bluetooth_searching),
    connected(R.string.bluetooth_connected, R.color.blue, R.drawable.bluetooth_connected);

    private final int string;
    private final int color;
    private final int image;

    BluetoothState(int string, int color, int image) {
        this.string = string;
        this.color = color;
        this.image = image;
    }

    public int getString() {
        return string;
    }

    public int getColor() {
        return color;
    }

    public int getImage() {
        return image;
    }
}
