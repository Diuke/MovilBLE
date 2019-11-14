package com.example.bledemo;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.example.bledemo.ble.BLEManager;
import com.example.bledemo.ble.services.BLEService;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class Loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loader);
        Intent i = new Intent(getApplicationContext(), BLEManager.class);
        startService(i);

        try {
            wait(1000);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
