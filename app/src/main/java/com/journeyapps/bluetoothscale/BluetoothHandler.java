package com.journeyapps.bluetoothscale;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Locale;

public class BluetoothHandler extends Handler {
    private static final String TAG = "JOURNEYAPPSSCALE";

    private MainActivity mainActivity;

    BluetoothHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public final void handleMessage(Message message) {
        Log.i(TAG, "handleMessage: " + message.toString());
        if(message.what == BluetoothService.CODE_CONNECTION_STATUS_MESSAGE) {
            BluetoothService.ConnectionState state = BluetoothService.ConnectionState.valueOf(message.arg1);
            switch (state) {
                case NOT_CONNECTED:
                    Log.i(TAG, "MESSAGE: NOT CONNECTED");
                    mainActivity.showConnectionStatus("Disconnected");
                    break;
                case CONNECTING:
                    Log.i(TAG, "MESSAGE: CONNECTING");
                    mainActivity.showConnectionStatus("Connecting");
                    break;
                case CONNECTED:
                    Log.i(TAG, "MESSAGE: CONNECTED");
                    mainActivity.showConnectionStatus("Connected");
                    break;
                default:
                    break;
            }
        }
        if(message.what == BluetoothService.CODE_READING_MESSAGE) {
            byte[] bytes = (byte[]) message.obj;
            try {
                ScaleReading scaleReading = HiweighScaleProcessor.getInstance().constructReading(bytes);
                mainActivity.scaleValueText.setText(String.format(Locale.getDefault(), "%.2f %s", scaleReading.getWeight(), scaleReading.getUnit()));
                mainActivity.isZero.setText(scaleReading.isZero() ? "ZERO" : "");
                mainActivity.isStable.setText(scaleReading.isStable() ? "STABLE" : "");
            } catch (HiweighScaleProcessor.HiweighScaleException e) {
                Log.e(TAG, "Error processing reading", e);
            }
        }
    }
}
