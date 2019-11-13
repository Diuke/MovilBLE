package com.example.bledemo.ble;

public class ScanModel {

    private String name;
    private String mac;
    private int signal;

    public ScanModel(String name, String mac, int signal){
        this.name = name;
        this.mac = mac;
        this.signal = signal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    @Override
    public String toString() {
        return name + "/" + mac + "/" + signal;
    }

}
