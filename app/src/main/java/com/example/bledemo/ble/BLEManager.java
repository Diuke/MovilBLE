package com.example.bledemo.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.bledemo.ble.services.BLEService;

import java.util.ArrayList;
import java.util.List;

public class BLEManager extends ScanCallback {
    BLEManagerCallerInterface caller;
    Context context;

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    public List<ScanModel> scanResults = new ArrayList<>();
    public List<ScanResult> deviceList = new ArrayList<>();
    public BluetoothGatt lastBluetoothGatt;
    public BLEModel bleModel;

    public BLEManager(BLEManagerCallerInterface caller, Context context) {
        this.caller = caller;
        this.context = context;
        initializeBluetoothManager();
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public void initializeBluetoothManager(){
        try{
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            this.bluetoothAdapter = bluetoothManager.getAdapter();
        }catch (Exception error){

        }
    }

    public boolean isBluetoothOn() {
        try{
            return bluetoothManager.getAdapter().isEnabled();
        }catch (Exception error){

        }
        return false;
    }

    public void requestLocationPermissions(final Activity activity,int REQUEST_CODE) {
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean gps_enabled = false;
                boolean network_enabled = false;

                LocationManager locationManager=(LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!((gps_enabled)||(network_enabled))){

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("In order to BLE connection be successful please proceed to enable the GPS")
                            .setTitle("Settings");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);

                        }
                    });

                    builder.create().show();
                }
            }
            if (ContextCompat.checkSelfPermission(this.context.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                activity.requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);

            }
        }catch (Exception error){

        }

    }

    public void startScanDevices(){
        try {
            scanResults.clear();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(this);
            caller.scanStartedSuccessfully();
        }catch (Exception error){

        }
    }

    public void stopScanDevices() {
        try{
            scanResults.clear();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(this);
            caller.scanStopped();
        }catch (Exception error){

        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        ScanModel atList = isResultAlreadyAtList(result);
        if(atList == null) {
            ScanModel newDevice = new ScanModel(result.getDevice().getName(), result.getDevice().getAddress(), result.getRssi());
            scanResults.add(newDevice);
            deviceList.add(result);
            caller.newDeviceDetected(newDevice);
        } else {
            atList.setSignal(result.getRssi());
            caller.newDeviceDetected(atList);
        }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {

    }

    @Override
    public void onScanFailed(int errorCode) {
        caller.scanFailed(errorCode);
    }

    public ScanModel isResultAlreadyAtList(ScanResult newResult){
        for (ScanModel current : scanResults){
            if(current.getMac().equals(newResult.getDevice().getAddress())){
                return current;
            }
        }
        return null;
    }

    private void searchAndSetAllNotifyAbleCharacteristics() {
        try {
            if(lastBluetoothGatt != null) {
                for(BluetoothGattService currentService: lastBluetoothGatt.getServices()) {
                    if(currentService != null) {
                        for(BluetoothGattCharacteristic currentCharacteristic:currentService.getCharacteristics()) {
                            if(currentCharacteristic != null) {
                                if(UtilsBLE.isCharacteristicNotifiable(currentCharacteristic)) {
                                    lastBluetoothGatt.setCharacteristicNotification(currentCharacteristic, true);
                                    for(BluetoothGattDescriptor currentDescriptor:currentCharacteristic.getDescriptors()) {
                                        if(currentDescriptor != null) {
                                            try {
                                                currentDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                                lastBluetoothGatt.writeDescriptor(currentDescriptor);
                                            }catch (Exception internalError) {

                                            }
                                        }
                                    }
                                    bleModel.addSuscription(currentCharacteristic);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception error) {

        }

    }

    public BluetoothDevice getByAddress(String targetAddress){
        for(ScanResult current : deviceList){
            if(current!=null){
                if(current.getDevice().getAddress().equals(targetAddress)){
                    return current.getDevice();
                }
            }
        }
        return null;
    }

    public boolean readCharacteristic(BluetoothGattCharacteristic characteristic){
        try{
            if(characteristic==null) return false;
            return lastBluetoothGatt.readCharacteristic(characteristic);
        }catch (Exception error){

        }
        return false;
    }

    public boolean writeCharacteristic(BluetoothGattCharacteristic characteristic,byte[] data){
        try{
            if(characteristic==null) return false;
            characteristic.setValue(data);
            return lastBluetoothGatt.writeCharacteristic(characteristic);
        }catch (Exception error){

        }
        return false;
    }

    public void connectToGATTServer(BluetoothDevice device) {
        try{
            device.connectGatt(this.context, false, new BluetoothGattCallback() {
                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt,
                                                    int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if(newState == BluetoothGatt.STATE_CONNECTED){
                        lastBluetoothGatt = gatt;
                        Toast.makeText(context, "Connected to device " + gatt.getDevice().getAddress(), Toast.LENGTH_SHORT).show();
                        gatt.discoverServices();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    bleModel = new BLEModel(gatt, (ArrayList) gatt.getServices());
                    caller.arrayOperation(BLEService.ACTION_GET_SERVICES, bleModel.getServicesUuid());
                    searchAndSetAllNotifyAbleCharacteristics();
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    caller.characteristicOperation(BLEService.ACTION_READ_CHARACTERISTIC, gatt,
                            characteristic, status);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    caller.characteristicOperation(BLEService.ACTION_WRITE_CHARACTERISTIC, gatt,
                            characteristic, status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    caller.characteristicOperation(BLEService.ACTION_CHARACTERISTIC_CHANGE, gatt,
                            characteristic, BLEService.NONE_STATUS);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }
            }, BluetoothDevice.TRANSPORT_LE);
        }catch (Exception error){

        }
    }

    public void disconnectFromGATTServer() {
        try {
            lastBluetoothGatt.disconnect();
            caller.gattDisconnected();
            lastBluetoothGatt = null;
            bleModel = null;
        }catch(Exception ex) {

        }
    }

}
