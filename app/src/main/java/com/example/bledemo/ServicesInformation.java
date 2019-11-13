package com.example.bledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.bledemo.ble.services.BLEService;
import com.example.bledemo.network.BroadcastManager;
import com.example.bledemo.network.BroadcastManagerCallerInterface;

import java.util.ArrayList;

public class ServicesInformation extends AppCompatActivity implements BroadcastManagerCallerInterface {

    ArrayList<String> listOfServices = new ArrayList<>();
    ArrayAdapter<String> adapter;

    private BroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_information);

        initializeBroadcastManager();

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfServices);
        ListView lista = (ListView) findViewById(R.id.service_list);
        lista.setAdapter(adapter);
        lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intentChar = new Intent(getApplicationContext(), Characteristics.class);
                intentChar.putExtra("Service", adapterView.getItemIdAtPosition(i));
                startActivity(intentChar);
                return true;
            }
        });

        sendRequest(BLEService.ACTION_GET_SERVICES,"");
        actualizarServicios(null);
    }

    protected void actualizarServicios(final ArrayList<String> services){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (services != null) {
                    for (String s: services) {
                        listOfServices.add(s);
                    }
                }else{
                    listOfServices.clear();
                }
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
                            this.finishActivity(1010);
                            break;
                        }
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
                        case BLEService.ACTION_GET_SERVICES: {
                            actualizarServicios(data);
                            break;
                        }
                    }
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


    private void initializeBroadcastManager() {
        broadcastManager = new BroadcastManager(this,
                BroadcastManager.BROADCAST_CHANNEL,this);
    }

    public void sendRequest(String action, String extras) {
        broadcastManager.sendBroadcast(action, BroadcastManager.GUI_TO_SERVICE_MESSAGE, extras);
    }
}
