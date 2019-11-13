package com.example.bledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Characteristics extends AppCompatActivity {

    ArrayList<String> listOfChar = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_expandable_list_item_1, listOfChar);
        ((ListView) findViewById(R.id.characteristic_list)).setAdapter(adapter);
    }

    protected void actualizarCaracteristica(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listOfChar.add("Caracteristica");
                adapter.notifyDataSetChanged();
            }
        });
    }
}
