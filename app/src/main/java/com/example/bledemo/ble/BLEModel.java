package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

public class BLEModel {

    ArrayList<BluetoothGattService> services;
    ArrayList<BluetoothGattCharacteristic> suscriptions;
    BluetoothGatt gatt;

    public BLEModel(BluetoothGatt gatt, ArrayList services){
        this.gatt = gatt;
        this.services = services;
        this.suscriptions = new ArrayList<>();
    }

    public BLEModel() {
        this.services = new ArrayList<>();
        this.suscriptions = new ArrayList<>();
    }

    public void addSuscription(BluetoothGattCharacteristic characteristic){
        suscriptions.add(characteristic);
    }

    public BluetoothGattCharacteristic getCharacteristicByUUID(String uuid){
        for(BluetoothGattService service : services){
            for(BluetoothGattCharacteristic characteristic: service.getCharacteristics()){
                if(characteristic.getUuid().equals(uuid)){
                    return characteristic;
                }
            }
        }
        return null;
    }
}
