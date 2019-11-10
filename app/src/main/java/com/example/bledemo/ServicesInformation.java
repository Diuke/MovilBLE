package com.example.bledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ServicesInformation extends AppCompatActivity {

    ArrayList<String> listOfServices = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_information);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listOfServices);
        ((ListView) findViewById(R.id.service_list)).setAdapter(adapter);
    }

    protected void actualizarServicios(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listOfServices.add("servicio");
                adapter.notifyDataSetChanged();
            }
        });
    }
}
