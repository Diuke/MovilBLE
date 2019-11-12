package com.example.bledemo.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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

    public BluetoothGattCharacteristic getCharacteristicByUUID(String characteristicUuid){
        for(BluetoothGattService service : services){
            for(BluetoothGattCharacteristic characteristic: service.getCharacteristics()){
                if(characteristic.getUuid().toString().equals(characteristicUuid)){
                    return characteristic;
                }
            }
        }
        return null;
    }

    public BluetoothGattService getServiceByUUID(String serviceUuid) {
        for(BluetoothGattService service : services){
            if(service.getUuid().toString().equals(serviceUuid)) {
                return service;
            }
        }
        return null;
    }

    public ArrayList<String> getServicesUuid() {
        ArrayList<String> result = new ArrayList<>();
        for(BluetoothGattService service : services) {
            result.add(service.getUuid().toString());
        }
        return result;
    }

    public ArrayList<String> getCharacteristicProperties(String serviceUuid) {
        ArrayList<String> result = new ArrayList<>();
        for(BluetoothGattService service : services) {
            if(service.getUuid().toString().equals(serviceUuid)) {
                for(BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    String charProps = characteristic.getUuid().toString() + "/" +
                            UtilsBLE.isCharacteristicReadable(characteristic) + "/" +
                            UtilsBLE.isCharacteristicWritable(characteristic) + "/" +
                            UtilsBLE.isCharacteristicNotifiable(characteristic);
                    for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                        charProps += "/" + descriptor.getUuid().toString();
                    }
                    result.add(charProps);
                }
            }
        }
        return result;
    }

}
