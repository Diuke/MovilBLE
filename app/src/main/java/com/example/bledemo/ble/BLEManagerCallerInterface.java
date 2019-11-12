package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStopped();
    void scanFailed(int error);
    void newDeviceDetected();
    void characteristicOperation(String action, BluetoothGatt gatt,
                                 BluetoothGattCharacteristic characteristic, int status);

}
