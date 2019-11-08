package com.example.bledemo.adapters;

import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.bledemo.MainActivity;
import com.example.bledemo.R;

import java.util.List;



public class BluetoothDeviceListAdapter extends ArrayAdapter<ScanResult> {
    private final Context context;
    private MainActivity mainActivity;
    private List<ScanResult> scanResultList;

    public BluetoothDeviceListAdapter(@NonNull Context context, List<ScanResult> scanResultList, MainActivity mainActivity) {
        super(context, R.layout.device_list_item,scanResultList);
        this.context = context;
        this.mainActivity=mainActivity;
        this.scanResultList = scanResultList;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.device_list_item, null, true);
        final ListView listView = (ListView) mainActivity.findViewById(R.id.devices_list_id);

        TextView txtMac = (TextView) rowView.findViewById(R.id.ble_mac);
        TextView txtName = (TextView) rowView.findViewById(R.id.ble_name);
        TextView txtSignal = (TextView) rowView.findViewById(R.id.signal_power);

        String macAddress=scanResultList.get(position).getDevice().getAddress();
        String deviceName=scanResultList.get(position).getDevice().getName();
        String signal = ""+scanResultList.get(position).getRssi();

        txtMac.setText(macAddress);
        txtName.setText(deviceName);
        txtSignal.setText(signal);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                listView.getItemAtPosition(position);
                String address=((TextView) view.findViewById(R.id.ble_mac)).getText()+"";
                Toast.makeText(context,"Connecting to: "+address,Toast.LENGTH_LONG).show();
                mainActivity.bleManager.connectToGATTServer(mainActivity.bleManager.getByAddress(address));
                return false;
            }
        });

        listView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address=((TextView) view.findViewById(R.id.ble_mac)).getText()+"";
                Toast.makeText(context,"Keep a long touch to connect...",Toast.LENGTH_LONG).show();
                //mainActivity.bleManager.connectToGATTServer(mainActivity.bleManager.getByAddress(address));
            }
        });


        return rowView;
    }
}