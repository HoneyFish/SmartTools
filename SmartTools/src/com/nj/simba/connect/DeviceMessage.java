package com.nj.simba.connect;

import com.nj.simba.base.IDeviceReqListener;

public class DeviceMessage {
    int what;
    IDeviceReqListener who;
    
    public DeviceMessage(int what, IDeviceReqListener who) {
        this.what = what;
        this.who = who;
    }
}