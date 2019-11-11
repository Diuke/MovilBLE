package com.example.bledemo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;

import com.example.bledemo.ble.ScanModel;

import java.util.ArrayList;

public class BroadcastManager extends BroadcastReceiver {

    public static String BROADCAST_CHANNEL = "com.example.bledemo.network.BROADCAST_CHANNEL";
    public static String SERVICE_TO_GUI_MESSAGE = "SERVICE_TO_GUI_MESSAGE";
    public static String GUI_TO_SERVICE_MESSAGE = "GUI_TO_SERVICE_MESSAGE";

    private Context context;
    private String channel;
    private BroadcastManagerCallerInterface caller;

    public BroadcastManager(Context context, String channel, BroadcastManagerCallerInterface caller) {
        this.context = context;
        this.channel = channel;
        this.caller = caller;
        initializeBroadcast();
    }

    public void initializeBroadcast(){
        try{
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(channel);
            context.registerReceiver(this, intentFilter);
        }catch (Exception error){
            caller.errorAtBroadcastManager(error);
        }
    }

    public void unRegister(){
        try{
            context.unregisterReceiver(this);
        }catch (Exception error){
            caller.errorAtBroadcastManager(error);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getExtras().getString("serviceAction");
        String type = intent.getExtras().getString("type");
        if(intent.hasExtra("message")) {
            String payload = intent.getExtras().getString("message");
            caller.messageReceivedThroughBroadcastManager(this.channel, action, type, payload);
        }else if(intent.hasExtra("arrayData")) {
            ArrayList<String> data = intent.getExtras().getStringArrayList("arrayData");
            caller.messageReceivedThroughBroadcastManager(this.channel, action, type, data);
        }

    }

    public void sendBroadcast(String action, String type, String message){
        try{
            Intent intentToBeSent = new Intent();
            intentToBeSent.setAction(channel);
            intentToBeSent.putExtra("serviceAction", action);
            intentToBeSent.putExtra("message", message);
            intentToBeSent.putExtra("type", type);
            context.sendBroadcast(intentToBeSent);
        }catch (Exception error){
            caller.errorAtBroadcastManager(error);
        }
    }

    public void sendArrayBroadcast(String action, String type, ArrayList<String> data){
        try{
            Intent intentToBeSent = new Intent();
            intentToBeSent.setAction(channel);
            intentToBeSent.putExtra("serviceAction", action);
            intentToBeSent.putStringArrayListExtra("arrayData", data);
            intentToBeSent.putExtra("type", type);
            context.sendBroadcast(intentToBeSent);
        }catch (Exception error){
            caller.errorAtBroadcastManager(error);
        }
    }



}
