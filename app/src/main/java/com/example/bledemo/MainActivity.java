package com.example.bledemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.example.bledemo.adapters.BluetoothDeviceListAdapter;
import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.BLEManagerCallerInterface;
import com.example.bledemo.ble.UtilsBLE;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements BLEManagerCallerInterface {

    public BLEManager bleManager;
    private MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean isSupported = UtilsBLE.CheckIfBLEIsSupportedOrNot(getApplicationContext());
        TextView support = (TextView)findViewById(R.id.support_textview);
        if(isSupported){
            support.setText("Support BLE: TRUE");
        } else {
            support.setText("Support BLE: FALSE");
        }

        FloatingActionButton fab = findViewById(R.id.fab);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bleManager!=null){
                    bleManager.scanResults.clear();
                    notifyListViewChange();

                    if(!bleManager.isBluetoothOn()) {
                        if(UtilsBLE.RequestBluetoothDeviceEnable(mainActivity)){
                            bleManager.scanDevices();
                        }
                    } else {
                        bleManager.scanDevices();
                    }
                    changeBluetoothStatusTextView();
                }
            }
        });
        mainActivity=this;
        bleManager=new BLEManager(this,this);
        bleManager.scanResults.clear();
        ListView listView=(ListView)findViewById(R.id.devices_list_id);
        BluetoothDeviceListAdapter adapter=new BluetoothDeviceListAdapter(getApplicationContext(),bleManager.scanResults,mainActivity);
        listView.setAdapter(adapter);

        if(!bleManager.isBluetoothOn()){
            UtilsBLE.RequestBluetoothDeviceEnable(this);
        }else{
            bleManager.requestLocationPermissions(this,1002);
        }
        changeBluetoothStatusTextView();

    }

    public void changeBluetoothStatusTextView(){
        TextView status = (TextView)findViewById(R.id.adapter_status_textview);
        if(bleManager.isBluetoothOn()) {
            status.setText("Bluetooth Adapter: ON");
        } else {
            status.setText("Bluetooth Adapter: OFF");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void notifyListViewChange(){
        ListView listView=(ListView)findViewById(R.id.devices_list_id);
        BluetoothDeviceListAdapter adapter = (BluetoothDeviceListAdapter)listView.getAdapter();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
                        .setMessage("Camera and Location permissions must be granted in order to execute the app")
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
                    bleManager.requestLocationPermissions(this,1002);

                }
            }


        }catch (Exception error){

        }
    }


    @Override
    public void scanStartedSuccessfully() {

    }

    @Override
    public void scanStoped() {

    }

    @Override
    public void scanFailed(int error) {

    }

    @Override
    public void newDeviceDetected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    notifyListViewChange();

                }catch (Exception error){

                }

            }
        });


    }
}
