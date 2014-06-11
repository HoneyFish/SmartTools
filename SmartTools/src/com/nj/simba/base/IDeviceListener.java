package com.nj.simba.base;

import com.nj.simba.app.SmartToolsApp;


public interface IDeviceListener {
    void deviceChanged(SmartToolsApp app, int changeMask);
    void deviceListChanged(SmartToolsApp app);
    void deviceConnected(SmartToolsApp app);
    void deviceDisconnected(SmartToolsApp app);
}

