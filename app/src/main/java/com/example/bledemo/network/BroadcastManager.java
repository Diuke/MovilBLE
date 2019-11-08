package com.example.bledemo.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

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
        String payload = intent.getExtras().getString("payload");
        String type=intent.getExtras().getString("type");
        caller.messageReceivedThroughBroadcastManager(this.channel, type, payload);
    }

    public void sendBroadcast(String type, String message){
        try{
            Intent intentToBeSent=new Intent();
            intentToBeSent.setAction(channel);
            intentToBeSent.putExtra("payload",message);
            intentToBeSent.putExtra("type",type);
            context.sendBroadcast(intentToBeSent);
        }catch (Exception error){
            caller.errorAtBroadcastManager(error);
        }
    }

}
