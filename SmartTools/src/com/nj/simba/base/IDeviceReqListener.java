package com.nj.simba.base;

import java.util.EventListener;

public interface IDeviceReqListener extends EventListener {
    void onRequestBack(Object o);
}