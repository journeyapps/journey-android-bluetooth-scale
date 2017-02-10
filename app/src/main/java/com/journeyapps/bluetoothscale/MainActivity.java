package com.journeyapps.bluetoothscale;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "JOURNEYAPPSSCALE";
    private BluetoothAdapter adapter;
    private BluetoothService bluetoothService;
    private final Handler handler;

    public TextView scaleValueText;
    public TextView isZero;
    public TextView isStable;
    public TextView connectionStatus;

    public MainActivity() {
        this.adapter = null;
        this.bluetoothService = null;
        this.handler = new BluetoothHandler(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        this.scaleValueText = (TextView) findViewById(R.id.measured_value);
        this.isZero = (TextView) findViewById(R.id.zero);
        this.isStable = (TextView) findViewById(R.id.stable);
        this.connectionStatus = (TextView) findViewById(R.id.connected);

        this.bluetoothService = new BluetoothService(this, this.handler);
    }

    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
        setupBluetooth();
    }

    public void onDestroy() {
        super.onDestroy();
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void disconnectScale(View view) {
        if(this.bluetoothService != null) {
            this.bluetoothService.stop();
        }
    }

    public void writeZero(View view) {
        this.bluetoothService.writeZero();
    }

    public void showConnectionStatus(String status) {
        this.connectionStatus.setText(status);
    }
}
