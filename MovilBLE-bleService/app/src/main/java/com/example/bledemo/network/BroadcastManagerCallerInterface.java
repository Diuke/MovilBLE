package com.example.bledemo.network;

public interface BroadcastManagerCallerInterface {

    void messageReceivedThroughBroadcastManager(String channel, String type, String message);
    void errorAtBroadcastManager(Exception ex);

}
