package com.example.bledemo.ble;

import java.util.ArrayList;

public class CharModel {

    private String UUID;
    private boolean[] Properties;
    private ArrayList<String> Descriptors;

    public CharModel(String UUID, boolean[] Properties, ArrayList<String> Descriptors){
        this.UUID = UUID;
        this.Properties = Properties;
        this.Descriptors = Descriptors;
    }

    public String getUUID() {
        return UUID;
    }

    public boolean[] getProperties() {
        return Properties;
    }

    public ArrayList<String> getDescriptors() {
        return Descriptors;
    }
}
