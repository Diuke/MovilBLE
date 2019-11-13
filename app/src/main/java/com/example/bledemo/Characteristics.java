package com.example.bledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bledemo.ble.services.BLEService;
import com.example.bledemo.network.BroadcastManager;
import com.example.bledemo.network.BroadcastManagerCallerInterface;

import java.util.ArrayList;

public class Characteristics extends AppCompatActivity implements BroadcastManagerCallerInterface {

    ArrayList<String> listOfChar = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private BroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listOfChar);
        ((ListView) findViewById(R.id.characteristic_list)).setAdapter(adapter);

        initializeBroadcastManager();
        sendRequest(BLEService.ACTION_GET_CHARACTERISTICS,
                this.getIntent().getExtras().getString("Service"));
    }

    protected void actualizarCaracteristica(final ArrayList<String> caracteristicas){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listOfChar.add("Caracteristica");

                adapter.notifyDataSetChanged();
            }
        });
    }


    //BroadcastManager


    //BroadcastReceiver (Mensaje Plano (String))
    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, String message) {
        try {
            if(channel.equals(BroadcastManager.BROADCAST_CHANNEL)) {
                if(type.equals(BroadcastManager.SERVICE_TO_GUI_MESSAGE)) {
                    switch(action) {
                        //Example
                        case BLEService.ACTION_GATT_DISCONNECT: {
                            //TODO
                            break;
                        }
                        //TODO
                    }
                }
            }
        }catch(Exception ex) {
            errorAtBroadcastManager(ex);
        }
    }

    //BroadcastReceiver (ArrayList<String>)
    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, ArrayList<String> data) {
        try {
            if(channel.equals(BroadcastManager.BROADCAST_CHANNEL)) {
                if(type.equals(BroadcastManager.SERVICE_TO_GUI_MESSAGE)) {
                    switch(action) {
                        //Example
                        case BLEService.ACTION_GET_CHARACTERISTICS: {
                            actualizarCaracteristica(data);
                            break;
                        }
                        //TODO
                    }
                }
            }
        }catch(Exception ex) {
            errorAtBroadcastManager(ex);
        }
    }

    @Override
    public void errorAtBroadcastManager(Exception ex) {

    }


    private void initializeBroadcastManager() {
        broadcastManager = new BroadcastManager(this,
                BroadcastManager.BROADCAST_CHANNEL,this);
    }

    public void sendRequest(String action, String extras) {
        broadcastManager.sendBroadcast(action, BroadcastManager.GUI_TO_SERVICE_MESSAGE, extras);
    }
}
