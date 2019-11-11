package com.example.bledemo.network;

import java.util.ArrayList;

public interface BroadcastManagerCallerInterface {

    void messageReceivedThroughBroadcastManager(String channel, String action, String type, String message);
    void messageReceivedThroughBroadcastManager(String channel, String action, String type, ArrayList<String> data);
    void errorAtBroadcastManager(Exception ex);

}
