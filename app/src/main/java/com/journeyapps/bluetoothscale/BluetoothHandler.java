package com.journeyapps.bluetoothscale;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class BluetoothHandler extends Handler {
    private static final String TAG = "JOURNEYAPPSSCALE";

    private final Context context;

    private class BroadcastIntent {

        private final Context context;
        private final Intent intent;

        public BroadcastIntent(Context context) {
            this.context = context;
            this.intent = new Intent();
            this.intent.setAction(ScaleBroadcastReceiver.SCALE_BROADCAST_ACTION);
        }

        public BroadcastIntent putState(BluetoothService.ConnectionState state) {
            this.intent.putExtra(ScaleBroadcastReceiver.SCALE_BROADCAST_STATE_KEY, state.getValue());
            return this;
        }

        public BroadcastIntent putScaleReading(ScaleReading scaleReading) {
            ScaleReadingParcel scaleReadingParcel = new ScaleReadingParcel(scaleReading);
            this.intent.putExtra(ScaleBroadcastReceiver.SCALE_BROADCAST_READING_KEY, scaleReadingParcel);
            return this;
        }

        public void sendBroadcast() {
            LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
        }

    }

    BluetoothHandler(Context context) {
        this.context = context;
    }

    public final void handleMessage(Message message) {
        Log.i(TAG, "handleMessage: " + message.toString());
        if(message.what == BluetoothService.CODE_CONNECTION_STATUS_MESSAGE) {
            BluetoothService.ConnectionState state = BluetoothService.ConnectionState.valueOf(message.arg1);

            getBroadcastIntent().putState(state).sendBroadcast();

        }
        if(message.what == BluetoothService.CODE_READING_MESSAGE) {
            byte[] bytes = (byte[]) message.obj;
            try {
                ScaleReading scaleReading = HiweighScaleProcessor.getInstance().constructReading(bytes);
                getBroadcastIntent().putScaleReading(scaleReading).sendBroadcast();
            } catch (HiweighScaleProcessor.HiweighScaleException e) {
                Log.e(TAG, "Error processing reading", e);
            }
        }
    }

    private BroadcastIntent getBroadcastIntent() {
        return new BroadcastIntent(this.context);
    }
}
