package com.example.bledemo.ble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.ArrayList;

public class BLEModel {

    ArrayList<BluetoothGattService> services;
    ArrayList<BluetoothGattCharacteristic> suscriptions;

    public BLEModel(ArrayList services){
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
}
