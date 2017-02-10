package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class BluetoothServiceThread extends Thread {
    private static final String TAG = "JOURNEYAPPSSCALE";
    private final BluetoothSocket bluetoothSocket;
    private final BluetoothService bluetoothService;

    public BluetoothServiceThread(BluetoothService bluetoothService, BluetoothDevice bluetoothDevice) {
        this.bluetoothService = bluetoothService;

        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(bluetoothService.getSppUuid());
        } catch (Throwable e) {
            Log.e(TAG, "could not create socket", e);
        }
        this.bluetoothSocket = bluetoothSocket;
    }

    public final void run() {
        Log.i(TAG, "BluetoothServiceThread starting");
        setName("ConnectionThread");
        try {
            this.bluetoothSocket.connect();
            synchronized (this.bluetoothService) {
                this.bluetoothService.setBluetoothServiceThread(null);
            }
            this.bluetoothService.connected(this.bluetoothSocket);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    public final void closeSocket() {
        try {
            this.bluetoothSocket.close();
        } catch (Throwable e) {
            Log.e(TAG, "BluetoothServiceThread Could not close socket", e);
        }
    }
}
