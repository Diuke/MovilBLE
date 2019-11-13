package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.ArrayList;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStopped();
    void scanFailed(int error);
    void newDeviceDetected(ScanModel scanModel);
    void characteristicOperation(String action, BluetoothGatt gatt,
                                 BluetoothGattCharacteristic characteristic, int status);
    void arrayOperation(String action, ArrayList<String> data);
    void gattConnected();
    void gattDisconnected();

}
