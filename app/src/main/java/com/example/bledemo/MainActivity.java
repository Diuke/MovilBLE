package com.example.bledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bledemo.ble.UtilsBLE;
import com.example.bledemo.ble.services.BLEService;
import com.example.bledemo.network.BroadcastManager;
import com.example.bledemo.network.BroadcastManagerCallerInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BroadcastManagerCallerInterface {

    private MainActivity mainActivity;
    private Switch btStatus;
    private Snackbar snack;
    private BroadcastManager broadcastManager;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listOfDevices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeBroadcastManager();

        boolean isSupported = UtilsBLE.CheckIfBLEIsSupportedOrNot(getApplicationContext());
        TextView support = (TextView)findViewById(R.id.support_textview);
        if(isSupported){
            support.setText("Support BLE: TRUE");
        } else {
            support.setText("Support BLE: FALSE");
        }

        ListView listView=(ListView)findViewById(R.id.devices_list_id);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfDevices);
        listView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BLEService.isRunning){
                    sendRequest(BLEService.ACTION_STOP_SCAN,"");
                    updateList(null);

                    if(!BLEService.isBluetoothOn()) {
                        if(UtilsBLE.RequestBluetoothDeviceEnable(mainActivity)){
                            sendRequest(BLEService.ACTION_START_SCAN,"");
                        }
                    } else {
                        sendRequest(BLEService.ACTION_START_SCAN,"");
                    }
                    changeBluetoothStatusSwitch();
                }
            }
        });
        mainActivity=this;
        sendRequest(BLEService.ACTION_STOP_SCAN,"");

        if(!BLEService.isBluetoothOn()){
            UtilsBLE.RequestBluetoothDeviceEnable(this);
        }else{
            BLEService.requestLocationPermissions(this,1002);
        }
        changeBluetoothStatusSwitch();
        btStatus = (Switch)findViewById(R.id.adapter_status_switch);
        btStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b && !BLEService.isBluetoothOn()){
                    UtilsBLE.RequestBluetoothDeviceEnable(getParent());
                }else{
                    UtilsBLE.RequestBluetoothDeviceEnable(getParent());
                }
            }
        });

        ListView devices = (ListView) findViewById(R.id.devices_list_id);
        devices.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {
                //Si ya está conectado pasa a ServicesInformation, sino intenta conectar
                if (BLEService.mConnectionState == BLEService.STATE_CONNECTED){
                    if (BLEService.getConnectedDeviceUUID().equals(adapterView.getItemAtPosition(i))) {
                        Intent intentServicesView = new Intent(getApplicationContext(), ServicesInformation.class);
                        startActivity(intentServicesView);
                        return true;
                    }
                }
                //Conexion con el dispositivo
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        snack = Snackbar.make(getCurrentFocus(), "Connecting", Snackbar.LENGTH_LONG);
                        snack.show();
                        sendRequest(BLEService.ACTION_GATT_CONNECT, adapterView.getItemAtPosition(i).toString().split("/")[1]);
                    }
                });
                return false;
            }
        });
    }

    private void updateList(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!s.equals(null)) {
                    listOfDevices.add(s);
                }else {
                    listOfDevices.clear();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void changeBluetoothStatusSwitch(){
        if(BLEService.isBluetoothOn()) {
            btStatus.setChecked(true);
        } else {
            btStatus.setChecked(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Log) {
            Intent intent = new Intent(getApplicationContext(), Log.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean allPermissionsGranted=true;
        if (requestCode == 1002) {
            for (int currentResult:grantResults
            ) {
                if(currentResult!= PackageManager.PERMISSION_GRANTED){
                    allPermissionsGranted=false;
                    break;
                }
            }
            if(!allPermissionsGranted){
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setTitle("Permissions")
                        .setMessage("Location permissions must be granted in order to execute the app")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                builder.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{

            if(requestCode==1001){
                if(resultCode!=Activity.RESULT_OK){

                }else{
                    BLEService.requestLocationPermissions(this,1002);

                }
            }


        }catch (Exception error){

        }
    }


    //BroadcastManager


    //BroadcastReceiver (Mensaje Plano (String))
    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, String message) {
        try {
            if(channel.equals(BroadcastManager.BROADCAST_CHANNEL)) {
                if(type.equals(BroadcastManager.SERVICE_TO_GUI_MESSAGE)) {
                    if (action.equals(BLEService.ACTION_DEVICE_DETECTED)){
                        listOfDevices.add(message);
                    }
                }
            }
        }catch(Exception ex) {
            errorAtBroadcastManager(ex);
        }
    }

    //BroadcastReceiver (ArrayList<String>)
    @Override
    public void messageReceivedThroughBroadcastManager(String channel, String action, String type, ArrayList<String> data) {}

    @Override
    public void errorAtBroadcastManager(Exception ex) {
        snack = Snackbar.make(getCurrentFocus(), "Error", Snackbar.LENGTH_LONG);
        snack.show();
    }


    private void initializeBroadcastManager() {
        broadcastManager = new BroadcastManager(this,
                BroadcastManager.BROADCAST_CHANNEL,this);
    }

    public void sendRequest(String action, String extras) {
        broadcastManager.sendBroadcast(action, BroadcastManager.GUI_TO_SERVICE_MESSAGE, extras);
    }
}
