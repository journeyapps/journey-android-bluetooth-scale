package com.journeyapps.bluetoothscale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Service for public scale broadcasts.
 */
public class ScaleBroadcastService extends Service implements ScaleUpdateCallback {

    /**
     * Action for scale broadcasts.
     */
    private static final String SCALE_BROADCAST_ACTION = "com.journeyapps.bluetoothscale.SCALE_BROADCAST_ACTION";

    /**
     * DTO for scale reading data sent in a scale broadcast.
     */
    private class ScaleReadingWrapper {

        /**
         * Key for scale reading object.
         */
        public static final String SCALE_READING_KEY = "SCALE_READING";

        // scale reading object property keys
        private static final String SCALE_READING_WEIGHT_KEY = "weight";
        private static final String SCALE_READING_UNIT_KEY = "unit";
        private static final String SCALE_READING_FAULT_KEY = "fault";
        private static final String SCALE_READING_OVERWEIGHT_KEY = "overweight";
        private static final String SCALE_READING_STABLE_KEY = "stable";
        private static final String SCALE_READING_ZERO_KEY = "zero";

        private final ScaleReading scaleReading;

        public ScaleReadingWrapper(ScaleReading scaleReading) {
            this.scaleReading = scaleReading;
        }

        public Bundle toBundle() {
            Bundle bundle = new Bundle();
            bundle.putDouble(SCALE_READING_WEIGHT_KEY, scaleReading.getWeight());
            bundle.putString(SCALE_READING_UNIT_KEY, scaleReading.getUnit().toString());
            bundle.putBoolean(SCALE_READING_FAULT_KEY, scaleReading.isFault());
            bundle.putBoolean(SCALE_READING_OVERWEIGHT_KEY, scaleReading.isOverweight());
            bundle.putBoolean(SCALE_READING_STABLE_KEY, scaleReading.isStable());
            bundle.putBoolean(SCALE_READING_ZERO_KEY, scaleReading.isZero());
            return bundle;
        }

    }

    private static final String TAG = ScaleBroadcastService.class.getSimpleName();

    private ScaleBroadcastReceiver scaleBroadcastReceiver;

    private Context getContext() {
        return this;
    }

    private ScaleUpdateCallback getScaleUpdateCallback() {
        return this;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        registerBroadcastReceiver();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        unregisterBroadcastReceiver();
        super.onDestroy();
    }

    private void registerBroadcastReceiver() {
        Log.e(TAG, "Registering scale broadcast receiver");
        IntentFilter broadcastFilter = new IntentFilter(ScaleBroadcastReceiver.SCALE_BROADCAST_ACTION);
        scaleBroadcastReceiver = new ScaleBroadcastReceiver(getScaleUpdateCallback());
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(scaleBroadcastReceiver, broadcastFilter);
    }

    private void unregisterBroadcastReceiver() {
        Log.e(TAG, "Un-registering scale broadcast receiver");
        try {
            if (scaleBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(scaleBroadcastReceiver);
                scaleBroadcastReceiver = null;
            }
        } catch(Exception e) {
            Log.e(TAG, "Error attempting to unregister scale broadcast receiver", e);
        }
    }

    @Override
    public void handleState(BluetoothService.ConnectionState state) {
        // do nothing
    }

    @Override
    public void handleScaleReading(ScaleReading scaleReading) {
        Log.d(TAG, "Handling scale reading");

        // send the scale reading as a global broadcast
        Intent broadcastIntent = new Intent(SCALE_BROADCAST_ACTION);
        ScaleReadingWrapper scaleReadingWrapper = new ScaleReadingWrapper(scaleReading);
        broadcastIntent.putExtra(ScaleReadingWrapper.SCALE_READING_KEY, scaleReadingWrapper.toBundle());
        getContext().sendBroadcast(broadcastIntent);
    }
}
