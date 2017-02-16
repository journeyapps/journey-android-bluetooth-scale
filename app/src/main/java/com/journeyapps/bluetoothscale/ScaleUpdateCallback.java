package com.journeyapps.bluetoothscale;

public interface ScaleUpdateCallback {
    void handleState(BluetoothService.ConnectionState state);

    void handleScaleReading(ScaleReading scaleReading);
}
