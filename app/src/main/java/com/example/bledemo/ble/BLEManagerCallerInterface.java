package com.example.bledemo.ble;

public interface BLEManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();

}
