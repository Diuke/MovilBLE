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
import com.example.bledemo.ble.ScanModel;

import java.util.List;



public class BluetoothDeviceListAdapter extends ArrayAdapter<ScanModel> {
    private final Context context;
    private MainActivity mainActivity;
    private List<ScanModel> scanResultList;

    public BluetoothDeviceListAdapter(@NonNull Context context, List<ScanModel> scanResultList, MainActivity mainActivity) {
        super(context, R.layout.device_list_item, scanResultList);
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

        String macAddress=scanResultList.get(position).getMac();
        String deviceName=scanResultList.get(position).getName();
        String signal = ""+scanResultList.get(position).getSignal();

        txtMac.setText(macAddress);
        txtName.setText(deviceName);
        txtSignal.setText(signal);

        /**rowView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String address=((TextView) view.findViewById(R.id.ble_mac)).getText()+"";
                Toast.makeText(context,"Connecting to: "+address,Toast.LENGTH_LONG).show();
                mainActivity.bleManager.connectToGATTServer(mainActivity.bleManager.getByAddress(address));
                return false;
            }
        });**/

        /*rowView.setOnLongClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String address=((TextView) view.findViewById(R.id.ble_mac)).getText()+"";
                Toast.makeText(mainActivity,"Connecting to: "+address,Toast.LENGTH_LONG).show();
                //Toast.makeText(context,"Keep a long touch to connect...",Toast.LENGTH_LONG).show();
                mainActivity.bleManager.connectToGATTServer(mainActivity.bleManager.getByAddress(address));
            }
        });*/


        return rowView;
    }
}