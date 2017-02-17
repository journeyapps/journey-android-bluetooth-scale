package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

public class BluetoothService {
    private static final String TAG = "JOURNEYAPPSSCALE";

    public static final int CODE_CONNECTION_STATUS_MESSAGE = 1;
    public static final int CODE_READING_MESSAGE = 2;

    public enum ConnectionState {
        NOT_CONNECTED(1),
        CONNECTING(2),
        CONNECTED(3);

        private final int value;

        ConnectionState(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static ConnectionState valueOf(int value) {
            switch(value) {
                case 1:
                    return NOT_CONNECTED;
                case 2:
                    return CONNECTING;
                case 3:
                    return CONNECTED;
            }
            return null;
        }
    }

    private static final UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final Handler handler;

    private BluetoothServiceThread bluetoothServiceThread;

    private BluetoothConnectedThread bluetoothConnectedThread;

    private ConnectionState state;

    public Handler getHandler() {
        return this.handler;
    }

    public UUID getSppUuid() {
        return sppUuid;
    }

    public void setBluetoothServiceThread(BluetoothServiceThread bluetoothServiceThread) {
        this.bluetoothServiceThread = bluetoothServiceThread;
    }

    public BluetoothService(Handler handler) {
        this.handler = handler;
        this.state = ConnectionState.NOT_CONNECTED;
    }

    private synchronized void setState(ConnectionState state) {
        Log.i(TAG, "State change from " + this.state + " to " + state.getValue());
        this.state = state;
        this.handler.obtainMessage(CODE_CONNECTION_STATUS_MESSAGE, state.getValue(), -1).sendToTarget();
    }

    public final synchronized void connectToDevice(BluetoothDevice bluetoothDevice) {
        Log.i(TAG, "BluetoothService connectToDevice " + bluetoothDevice.toString());
        this.close();
        this.bluetoothServiceThread = new BluetoothServiceThread(this, bluetoothDevice);
        this.bluetoothServiceThread.start();
        setState(ConnectionState.CONNECTING);
    }

    public final synchronized void connected(BluetoothSocket bluetoothSocket) {
        Log.i(TAG, "BluetoothService connected");
        this.close();
        this.bluetoothConnectedThread = new BluetoothConnectedThread(this, bluetoothSocket);
        this.bluetoothConnectedThread.start();
        setState(ConnectionState.CONNECTED);
    }

    public void sendZeroInstruction() {
        Log.i(TAG, "BluetoothService sendZeroInstruction");
        this.bluetoothConnectedThread.sendZeroInstruction();
    }

    private synchronized void close() {
        if(this.bluetoothServiceThread != null) {
            this.bluetoothServiceThread.closeSocket();
            this.bluetoothServiceThread = null;
        }
        if(this.bluetoothConnectedThread != null) {
            this.bluetoothConnectedThread.close();
            this.bluetoothConnectedThread = null;
        }
    }

    public final synchronized void stop() {
        Log.i(TAG, "BluetoothService stop()");
        setState(ConnectionState.NOT_CONNECTED); // TODO: consider adding a DISCONNECTED state?
        this.close();
    }

}
