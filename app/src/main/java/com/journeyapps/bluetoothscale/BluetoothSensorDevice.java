package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothDevice;

public class BluetoothSensorDevice {

    private final String address;
    private final String name;

    protected BluetoothSensorDevice(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public BluetoothSensorDevice(BluetoothDevice bluetoothDevice) {
        this.address = bluetoothDevice.getAddress();
        this.name = bluetoothDevice.getName();
    }

    public static BluetoothSensorDevice fromLabel(String label) {
        final String[] parts = label.split("\n");
        final String name = parts[0];
        final String address = parts[1];
        return new BluetoothSensorDevice(address, name);
    }

    public String getAddress() {
        return this.address;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
       return this.getName() + "\n" + this.getAddress();
   }

}
