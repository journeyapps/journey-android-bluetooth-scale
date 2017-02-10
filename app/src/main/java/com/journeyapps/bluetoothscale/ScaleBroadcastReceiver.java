package com.journeyapps.bluetoothscale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Receiver for internal scale broadcasts.
 */
public class ScaleBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = ScaleBroadcastReceiver.class.getSimpleName();

    public static final String SCALE_BROADCAST_ACTION = "com.journeyapps.bluetoothscale.internal.SCALE_BROADCAST_ACTION";

    public static final String SCALE_BROADCAST_STATE_KEY = "com.journeyapps.bluetoothscale.internal.SCALE_BROADCAST_STATE_KEY";

    public static final String SCALE_BROADCAST_READING_KEY = "com.journeyapps.bluetoothscale.internal.SCALE_BROADCAST_READING_KEY";

    private final ScaleUpdateCallback callback;

    public ScaleBroadcastReceiver(ScaleUpdateCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(SCALE_BROADCAST_STATE_KEY)) {
            Log.d(TAG, "Received state change broadcast");
            int stateInt = intent.getIntExtra(SCALE_BROADCAST_STATE_KEY, 0);
            if (stateInt > 0) {
                BluetoothService.ConnectionState state = BluetoothService.ConnectionState.valueOf(stateInt);
                callback.handleState(state);
            }
        }

        if (intent.hasExtra(SCALE_BROADCAST_READING_KEY)) {
            Log.d(TAG, "Received scale reading broadcast");
            ScaleReadingParcel scaleReading = intent.getParcelableExtra(SCALE_BROADCAST_READING_KEY);
            if (scaleReading != null) {
                callback.handleScaleReading(scaleReading);
            }
        }
    }
}
