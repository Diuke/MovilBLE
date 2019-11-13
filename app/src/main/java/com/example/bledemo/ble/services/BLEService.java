package com.example.bledemo.ble.services;

import android.app.Activity;
import android.app.IntentService;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.bledemo.R;
import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.BLEManagerCallerInterface;
import com.example.bledemo.ble.ScanModel;
import com.example.bledemo.network.BroadcastManager;
import com.example.bledemo.network.BroadcastManagerCallerInterface;

import java.util.ArrayList;

public class BLEService extends IntentService implements BLEManagerCallerInterface, BroadcastManagerCallerInterface {

    private final static String TAG = BLEService.class.getSimpleName();

    public static boolean isRunning = false;

    public static final String ACTION_START_SCAN =
            "com.example.bledemo.ble.services.ACTION_START_SCAN";
    public static final String ACTION_STOP_SCAN =
            "com.example.bledemo.ble.services.ACTION_STOP_SCAN";
    public static final String ACTION_GATT_CONNECT =
            "com.example.bledemo.ble.services.ACTION_GATT_CONNECT";
    public static final String ACTION_GATT_DISCONNECT =
            "com.example.bledemo.ble.services.ACTION_GATT_DISCONNECT";
    public static final String ACTION_READ_CHARACTERISTIC =
            "com.example.bledemo.ble.services.ACTION_READ_CHARACTERISTIC";
    public static final String ACTION_WRITE_CHARACTERISTIC =
            "com.example.bledemo.ble.services.ACTION_WRITE_CHARACTERISTIC";
    public static final String ACTION_CHARACTERISTIC_CHANGE =
            "com.example.bledemo.ble.services.ACTION_CHARACTERISTIC_CHANGE";
    public static final String ACTION_GET_CHARACTERISTICS =
            "com.example.bledemo.ble.services.ACTION_GET_CHARACTERISTICS";
    public static final String ACTION_GET_SERVICES =
            "com.example.bledemo.ble.services.ACTION_GET_SERVICES";
    public static final String ACTION_DEVICE_DETECTED =
            "com.example.bledemo.ble.services.ACTION_DEVICE_DETECTED";

    public static final int NONE_STATUS = -30;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public static int mConnectionState = STATE_DISCONNECTED;

    public static BLEManager bleManager;
    private BroadcastManager broadcastManager;
    private boolean isEnable = true;

    private ArrayList<String> requestQueue;
    private ArrayList<String> requestExtras;

    private final Object mutex = new Object();

    public BLEService() {
        super("BLEService");
        initializeBleManager();
        initializeBroadcastManager();
        requestQueue = new ArrayList<>();
        requestExtras= new ArrayList<>();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(bleManager.getBluetoothManager() != null) {
            processQueue();
        }
    }

    private void initializeBleManager() {
        bleManager = new BLEManager(this, getApplicationContext());
    }

    private void initializeBroadcastManager(){
        broadcastManager = new BroadcastManager(this,
                BroadcastManager.BROADCAST_CHANNEL,this);
    }

    private void waitForAWhile() {
        try {
            synchronized(mutex) {
                mutex.wait();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void notifyMutex() {
        try {
            synchronized(mutex) {
                mutex.notify();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processQueue() {
        while(isEnable) {
            while(requestQueue.size() > 0) {
                processRequest(requestQueue.get(0), requestExtras.get(0));
                requestQueue.remove(0);
                requestExtras.remove(0);
            }
            waitForAWhile();
        }
    }

    private void processRequest(String action, String extras) {
        switch (action) {
            case ACTION_START_SCAN: {
                bleManager.startScanDevices();
                break;
            }
            case ACTION_STOP_SCAN: {
                bleManager.stopScanDevices();
                break;
            }
            case ACTION_GATT_CONNECT: {
                mConnectionState = STATE_CONNECTING;
                String address = extras;
                bleManager.connectToGATTServer(bleManager.getByAddress(address));
                break;
            }
            case ACTION_GATT_DISCONNECT: {
                //Disconnect GATT Server
                bleManager.disconnectFromGATTServer();
                break;
            }

            case ACTION_READ_CHARACTERISTIC: {
                String uuid = extras;
                bleManager.readCharacteristic(bleManager.bleModel.getCharacteristicByUUID(uuid));
                break;
            }
            case ACTION_WRITE_CHARACTERISTIC: {
                String[] data = extras.split("/");
                String uuid = data[0];
                String updateData = data[1];
                bleManager.writeCharacteristic(bleManager.bleModel.getCharacteristicByUUID(uuid),
                        updateData.getBytes());
                break;
            }
            case ACTION_GET_CHARACTERISTICS: {
                String serviceUuid = extras;
                arrayOperation(ACTION_GET_CHARACTERISTICS,
                        bleManager.bleModel.getCharacteristicProperties(serviceUuid));
                break;
            }

            case ACTION_GET_SERVICES: {
                arrayOperation(ACTION_GET_SERVICES, bleManager.bleModel.getServicesUuid());
                break;
            }
        }
    }

    @Override
    public void scanStartedSuccessfully() {
        //Log Scan Started
    }

    @Override
    public void scanStopped() {
        //Log Scan Stopped
        broadcastManager.sendBroadcast(ACTION_STOP_SCAN, BroadcastManager.SERVICE_TO_GUI_MESSAGE,
                "Stop It");
    }

    @Override
    public void scanFailed(int error) {
        //Log Scan Failed
    }

    @Override
    public void newDeviceDetected(ScanModel scanModel) {
        broadcastManager.sendBroadcast(ACTION_DEVICE_DETECTED,
                BroadcastManager.SERVICE_TO_GUI_MESSAGE, scanModel.toString());
    }

    @Override
    public void characteristicOperation(String action, BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic, int status) {
        //Send Broadcast to GUI
        String msg = "";
        switch (action) {
            case ACTION_CHARACTERISTIC_CHANGE: {
                //Log
                msg +=  characteristic.getUuid().toString()+ "/" +
                        characteristic.getStringValue(0);
                break;
            }
            case ACTION_WRITE_CHARACTERISTIC: {
                msg += characteristic.getUuid().toString() + "/" + status + "/";
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    //Log Success
                    msg += R.string.characteristic_write_success;
                }else {
                    //Log Failure
                    msg += R.string.characteristic_write_failure;
                }
                break;
            }
            case ACTION_READ_CHARACTERISTIC: {
                msg += characteristic.getUuid().toString()+ "/" + status + "/";
                if(status == BluetoothGatt.GATT_SUCCESS) {
                    //Log Success
                    msg += (R.string.characteristic_read_success + "/" +
                            characteristic.getStringValue(0));
                }else {
                    //Log Failure
                    msg += R.string.characteristic_read_failure;
                }

                break;
            }
        }
        broadcastManager.sendBroadcast(action, BroadcastManager.SERVICE_TO_GUI_MESSAGE, msg);
    }

    @Override
    public void arrayOperation(String action, ArrayList<String> data) {
        broadcastManager.sendArrayBroadcast(action, BroadcastManager.SERVICE_TO_GUI_MESSAGE, data);
    }

    @Override
    public void gattConnected() {
        mConnectionState = STATE_CONNECTED;
        broadcastManager.sendBroadcast(ACTION_GATT_CONNECT,
                BroadcastManager.SERVICE_TO_GUI_MESSAGE, "Connected");
    }

    @Override
    public void gattDisconnected() {
        mConnectionState = STATE_DISCONNECTED;
        broadcastManager.sendBroadcast(ACTION_GATT_DISCONNECT,
                BroadcastManager.SERVICE_TO_GUI_MESSAGE, "Disconnected");
    }

    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, String message) {
        try {
            if(channel.equals(BroadcastManager.BROADCAST_CHANNEL)) {
                if(type.equals(BroadcastManager.GUI_TO_SERVICE_MESSAGE)) {
                    requestQueue.add(action);
                    requestExtras.add(message);
                    notifyMutex();
                }
            }
        }catch(Exception ex) {
            errorAtBroadcastManager(ex);
        }
    }

    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, ArrayList<String> data) {

    }

    @Override
    public void errorAtBroadcastManager(Exception ex) {
        ex.printStackTrace();
    }

    public static boolean isBluetoothOn(){
        return bleManager.isBluetoothOn();
    }

    public static void requestLocationPermissions(final Activity activity, int REQUEST_CODE){
        bleManager.requestLocationPermissions(activity, REQUEST_CODE);
    }

    public static String getConnectedDeviceUUID(){
        BluetoothGatt btGatt = bleManager.lastBluetoothGatt;
        if (btGatt != null) {
            return bleManager.lastBluetoothGatt.getDevice().getAddress();
        }
        return null;
    }
}