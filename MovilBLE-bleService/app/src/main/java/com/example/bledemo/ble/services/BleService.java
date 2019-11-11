package com.example.bledemo.ble.services;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.BLEManagerCallerInterface;
import com.example.bledemo.network.BroadcastManager;
import com.example.bledemo.network.BroadcastManagerCallerInterface;

import java.util.ArrayList;

public class BleService extends IntentService implements BLEManagerCallerInterface, BroadcastManagerCallerInterface {

    private final static String TAG = BleService.class.getSimpleName();

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

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    private int mConnectionState = STATE_DISCONNECTED;

    public BLEManager bleManager;
    private BroadcastManager broadcastManager;
    private boolean isEnable = true;

    private ArrayList<String> requestQueue;
    private ArrayList<String> requestExtras;

    private final Object mutex = new Object();

    public BleService() {
        super("BleService");
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
                processRequest(requestQueue.get(0));
            }
            waitForAWhile();
        }
    }

    private void processRequest(String action) {
        switch (action) {
            case ACTION_START_SCAN: {
                bleManager.scanDevices();
                break;
                log.w(TAG,"comenzo a escanear ");
            }
            case ACTION_STOP_SCAN: {
                bleManager.stopScanDevices();
                log.w(TAG,"se detuvo el escaneo");
                break;
            }
            case ACTION_GATT_CONNECT: {
                String address = requestExtras.get(0);
                bleManager.connectToGATTServer(bleManager.getByAddress(address));
                log.w(TAG,"conectando");
                break;
            }
            case ACTION_GATT_DISCONNECT: {
                //Disconnect GATT Server
                log.w(TAG,"desconectando");
                break;
            }

            case ACTION_READ_CHARACTERISTIC: {
                String uuid = requestExtras.get(0);
                bleManager.readCharacteristic(bleManager.bleModel.getCharacteristicByUUID(uuid));
                log.w(TAG,"leio una caracteristica");
                break;
            }
            case ACTION_WRITE_CHARACTERISTIC: {
                String[] data = requestExtras.get(0).split("/");
                String uuid = data[0];
                String updateData = data[1];
                bleManager.writeCharacteristic(bleManager.bleModel.getCharacteristicByUUID(uuid),
                        updateData.getBytes());
                log.w(TAG,"escribio una caracteristica");
                break;
            }
        }
        requestQueue.remove(0);
        requestExtras.remove(0);
    }

    @Override
    public void scanStartedSuccessfully() {

    }

    @Override
    public void scanStopped() {

    }

    @Override
    public void scanFailed(int error) {

    }

    @Override
    public void newDeviceDetected() {

    }

    @Override
    public void sendBroadcastToGUI(String action, String data) {
        //Send Broadcast to GUI
    }

    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String type, String message) {
        try {
            if(channel.equals(BroadcastManager.BROADCAST_CHANNEL)) {
                if(type.equals(BroadcastManager.GUI_TO_SERVICE_MESSAGE)) {
                    requestQueue.add(message);
                    notifyMutex();
                }
            }
        }catch(Exception ex) {
            errorAtBroadcastManager(ex);
        }
    }

    @Override
    public void errorAtBroadcastManager(Exception ex) {
        ex.printStackTrace();
    }
}