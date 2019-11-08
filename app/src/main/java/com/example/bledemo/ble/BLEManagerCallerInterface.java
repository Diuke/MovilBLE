package com.example.bledemo.ble;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStopped();
    void scanFailed(int error);
    void newDeviceDetected();

}
