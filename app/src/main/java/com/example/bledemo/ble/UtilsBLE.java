package com.example.bledemo.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.example.bledemo.R;

public class UtilsBLE {

    private static final int REQUEST_BLUETOOTH_PERMISSION_NEEDED = 1;
    public static boolean CheckIfBLEIsSupportedOrNot(Context context){
        try {
            if (!context.getPackageManager().
                    hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            }
            return true;
        }catch (Exception error){

        }
        return false;
    }

    public static boolean RequestBluetoothDeviceEnable(final Activity activity){
        try{
            final BluetoothManager bluetoothManager=(BluetoothManager) activity.getSystemService(activity.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter=bluetoothManager.getAdapter();
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity)
                        .setTitle("Bluetooth")
                        .setMessage("The bluetooth device must be enabled in order to connect the device")
                        .setIcon(R.mipmap.bt_blue)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                activity.startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH_PERMISSION_NEEDED);
                            }
                        });
                builder.show();

            }else {
                return true;
            }
        }catch (Exception error){

        }
        return false;
    }

    public static boolean RequestBluetoothDeviceDisable(final Activity activity){
        try{
            final BluetoothManager bluetoothManager=(BluetoothManager) activity.getSystemService(activity.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter=bluetoothManager.getAdapter();
            if (bluetoothAdapter == null || bluetoothAdapter.isEnabled()) {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity)
                        .setTitle("Bluetooth")
                        .setMessage("The bluetooth is required in order for the application to work, please turn it on")
                        .setIcon(R.mipmap.bt_blue)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                BluetoothAdapter.getDefaultAdapter().disable();
                            }
                        });
                builder.show();

            }else {
                return true;
            }
        }catch (Exception error){

        }
        return false;
    }

    public static boolean isCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() &
                (BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    public static boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    public static boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0);
    }

    public static byte[] hexStringToByteArray(String s) {
        if((s.length() % 2) == 1){
            s = "0" + s;
        }
        int len = s.length();
        if(len == 1) {
            return new byte[]{Byte.parseByte(s,16)};
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
