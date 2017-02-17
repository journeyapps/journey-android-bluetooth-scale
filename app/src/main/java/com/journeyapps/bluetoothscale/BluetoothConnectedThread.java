package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothConnectedThread extends Thread {
    private static final String TAG = "JOURNEYAPPSSCALE";
    private final BluetoothSocket bluetoothSocket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private BluetoothService bluetoothService;
    private final static int payloadSize = 11;
    private final static byte[] ZERO_INSTRUCTION_BYTES = new byte[]{(byte) 0x02, (byte) 0x4B, (byte) 0x5A, (byte) 0x52, (byte) 0x40, (byte) 0xB7, (byte) 0x0D};

    public BluetoothConnectedThread(BluetoothService bluetoothService, BluetoothSocket bluetoothSocket) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        this.bluetoothService = bluetoothService;
        this.bluetoothSocket = bluetoothSocket;

        try {
            InputStream tempInputStream = bluetoothSocket.getInputStream();
            try {
                inputStream = tempInputStream;
                outputStream = bluetoothSocket.getOutputStream();
            } catch (Throwable e) {
                Log.e(TAG, "BluetoothConnectedThread socket not created", e);
            }
        } catch (IOException e) {
            Log.e(TAG, "BluetoothConnectedThread socket not created", e);
        }
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void sendZeroInstruction() {
        Log.i(TAG, "BluetoothConnectedThread sendZeroInstruction");
        try {
            this.outputStream.write(this.ZERO_INSTRUCTION_BYTES);
        } catch (Throwable e) {
            Log.e(TAG, "Could not write", e);
        }
    }

    public final void close() {
        try {
            this.inputStream.close();
            this.outputStream.close();
            this.bluetoothSocket.close();
        } catch (Throwable e) {
            Log.e(TAG, "BluetoothConnectedThread Could not close socket");
        }
    }

    public final void run() {
        Log.i(TAG, "BluetoothConnectedThread Started to run Connected Thread");
        //noinspection InfiniteLoopStatement
        while(true) {
            try {
                byte[] payload = new byte[payloadSize];

                int readingMarker = this.inputStream.read();

                if (readingMarker == BluetoothService.CODE_READING_MESSAGE) {
                    // initial byte of `2` indicates that we have a valid reading
                    payload[0] = (byte)readingMarker;
                } else {
                    continue;
                }

                // we have already read the first byte from the stream
                // need to read the next 10 (11-1)
                int offset = 1;
                // read from stream until we have filled the buffer
                while (offset < payloadSize) {
                    int bytesRead = this.inputStream.read(payload, offset, payloadSize - offset);
                    offset += bytesRead;
                }

                if (payload.length != payloadSize || payload[payloadSize-1] != 13) {
                    // not a valid result (must have correct length and last byte must be `13`
                    continue;
                }

                this.bluetoothService.getHandler().obtainMessage(BluetoothService.CODE_READING_MESSAGE, payload).sendToTarget();

            } catch (IOException e) {
                Log.e(TAG, "Error reading from byte stream", e);
            }
        }
    }
}
