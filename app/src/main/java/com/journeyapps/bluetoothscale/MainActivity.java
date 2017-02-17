package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ScaleUpdateCallback {
    private static final String TAG = "JOURNEYAPPSSCALE";
    private BluetoothAdapter adapter = null;
    private BluetoothService bluetoothService = null;

    private TextView scaleValueText;
    private TextView isZero;
    private TextView isStable;
    private TextView connectionStatus;

    private ScaleBroadcastReceiver scaleBroadcastReceiver;

    private Intent scaleBroadcastServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.scaleValueText = (TextView) findViewById(R.id.measured_value);
        this.isZero = (TextView) findViewById(R.id.zero);
        this.isStable = (TextView) findViewById(R.id.stable);
        this.connectionStatus = (TextView) findViewById(R.id.connected);

        scaleBroadcastServiceIntent = new Intent(this, ScaleBroadcastService.class);
        startService(scaleBroadcastServiceIntent);
    }

    public void connectToScale(View view) {
        Log.i(TAG, "connectToScale");
        this.adapter = BluetoothAdapter.getDefaultAdapter();
        if (this.adapter == null) {
            finish();
        }
        // get list of devices
        Set<BluetoothDevice> bondedDevices = this.adapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            Log.i(TAG, Integer.toString(bondedDevices.size()));

            ArrayList<String> deviceList = new ArrayList<>(bondedDevices.size());
            for (BluetoothDevice bluetoothDevice : bondedDevices) {
                BluetoothSensorDevice sensorDevice = new BluetoothSensorDevice(bluetoothDevice);
                deviceList.add(sensorDevice.getLabel());
            }
            final String[] deviceArray = deviceList.toArray(new String[0]);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Scale:");
            builder.setItems(deviceArray, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, final int item) {
                    BluetoothSensorDevice sensorDevice = BluetoothSensorDevice.fromLabel(deviceArray[item]);
                    connectToDevice(sensorDevice);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void connectToDevice(BluetoothSensorDevice sensorDevice) {
        Log.i(TAG, sensorDevice.getAddress());
        this.bluetoothService.connectToDevice(this.adapter.getRemoteDevice(sensorDevice.getAddress()));
    }

    private void setupBluetooth() {
        BluetoothHandler handler = new BluetoothHandler(this.getApplicationContext());
        this.bluetoothService = new BluetoothService(handler);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        setupBluetooth();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceiver();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopService(scaleBroadcastServiceIntent);
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void disconnectScale(View view) {
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void sendZeroInstruction(View view) {
        this.bluetoothService.sendZeroInstruction();
    }

    private void registerBroadcastReceiver() {
        IntentFilter broadcastFilter = new IntentFilter(ScaleBroadcastReceiver.SCALE_BROADCAST_ACTION);
        scaleBroadcastReceiver = new ScaleBroadcastReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(scaleBroadcastReceiver, broadcastFilter);
    }

    private void unregisterBroadcastReceiver() {
        try {
            if (scaleBroadcastReceiver != null) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(scaleBroadcastReceiver);
                scaleBroadcastReceiver = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error attempting to unregister scale broadcast receiver", e);
        }
    }

    @Override
    public void handleState(BluetoothService.ConnectionState state) {
        String statusString;
        switch(state) {
            case NOT_CONNECTED:
                statusString = "Disconnected";
                break;
            case CONNECTING:
                statusString = "Connecting";
                break;
            case CONNECTED:
                statusString = "Connected";
                break;
            default:
                statusString = "N/A";
                break;
        }
        this.connectionStatus.setText(statusString);
    }

    @Override
    public void handleScaleReading(ScaleReading scaleReading) {
        this.scaleValueText.setText(String.format(Locale.getDefault(), "%.2f %s", scaleReading.getWeight(), scaleReading.getUnit()));
        this.isZero.setText(scaleReading.isZero() ? "ZERO" : "");
        this.isStable.setText(scaleReading.isStable() ? "STABLE" : "");
    }
}
