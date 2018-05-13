package com.example.root.incubator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by eli on 18-5-13.
 *
 */

public class BLEActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter mBluetoothAdapter;
    private final int REQUEST_ENABLE_BT = 1;
    private boolean hasGrantedPermission;
    private String TAG = BLEActivity.class.getSimpleName();

    private BluetoothAdapter.LeScanCallback callback;

    private ScanCallback scanCallback;
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @SuppressLint("NewApi")
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if (result != null && result.getDevice() != null) {
                        Log.d(TAG, "name:" + result.getDevice().getName() +
                                " mac:" + result.getDevice().getAddress());
                    }
                }

            @Override
            public void onScanFailed ( int errorCode){
                super.onScanFailed(errorCode);
                Log.e(TAG,"scan fail errorCode:"+errorCode);
            }
        };
        }
        else{
            callback = new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    if (device != null) {
                        Log.d(TAG, "name:" + device.getName() + " mac:" + device.getAddress());
                    }
                }
            };
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.bluetooth_ble_not_support,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permission1 = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int permission2 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

            if (permission1 == PackageManager.PERMISSION_GRANTED &&
                    permission2 == PackageManager.PERMISSION_GRANTED) {
                hasGrantedPermission = true;
            }
        } else {
            hasGrantedPermission = true;
        }

        if (hasGrantedPermission) {

            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null)
                mBluetoothAdapter = bluetoothManager.getAdapter();

            requestBtEnable();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        int REQUEST_PERMISSION_CODE = 100;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_PERMISSION_CODE);
    }

    private void requestBtEnable() {
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanBLE();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                break;
            case R.id.btn_close:
                break;
            case R.id.btn_plus_volume:
                break;
            case R.id.btn_reduce_volume:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            scanBLE();
        } else {
            requestBtEnable();
        }
    }

    private void scanBLE() {
        Log.d(TAG, "start scan ble");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().startScan(scanCallback);
        } else {
            mBluetoothAdapter.startLeScan(callback);
        }
    }
}
