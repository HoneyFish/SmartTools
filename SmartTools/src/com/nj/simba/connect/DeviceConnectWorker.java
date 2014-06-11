package com.nj.simba.connect;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.InstallException;
import com.nj.simba.IDeviceInfo;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.base.IDeviceReqListener;
import com.nj.simba.utils.Config;

public class DeviceConnectWorker extends SwingWorker<IDeviceInfo, String> implements IShellOutputReceiver, IDeviceReqListener{
    private boolean mIsApkInstalled = false;
    private IDevice mCurDevice = null;
    private int mRcvrMode = 0;
    private IDeviceInfo mDeviceInfo = null;
    private Object mLock = new Object();
    
    public DeviceConnectWorker(IDevice device) {
        mCurDevice = device;

        try {
            mCurDevice.createForward(Config.HOST_PORT, Config.DEVICE_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    @Override
    protected IDeviceInfo doInBackground() throws Exception {
        mIsApkInstalled = false;
        
        try {
            mRcvrMode = 0;
            mCurDevice.executeShellCommand(Config.CMD_CHECK_SERVER, this); // block call
        } catch (Exception e) {
            e.printStackTrace();
        }

        synchronized (mLock) {
            mLock.wait();
        }
        
        System.out.println("DeviceConnectWorker: doInBackground end");
        return mDeviceInfo;
    }
    
    @Override
    protected void done() {
        System.out.println("DeviceConnectWorker: done");
        SmartToolsApp.getApp().connected(mDeviceInfo);
    }
    
    @Override
    protected void process(List<String> chunks) {
        System.out.println("DeviceConnectWorker: process");
        
        for (String string : chunks) {
            System.out.println(string);
        }
    }
    
    @Override
    public void addOutput(byte[] data, int start, int offset) {
        System.out.println("DeviceConnectWorker: addOutput");
        
        if ( mRcvrMode == 0 ) { // check server apk
            mIsApkInstalled = true;
        } else { // try start server
            // ignore output
        }
    }

    @Override
    public void flush() {
        if ( mRcvrMode == 0 && mIsApkInstalled == false ) {
            installApk();
        } else if ( mRcvrMode == 1)  { // try start server
            System.out.println("server started!");
            SmartToolsApp.getApp().sendMessage(new DeviceMessage(0, this));
            return;
        }
        
        mRcvrMode = 1;
        try {
            System.out.println("try start server!");
            mCurDevice.executeShellCommand(Config.CMD_START_SERVER, this); // block call
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    private void installApk() {
        File apk = new File("apk/SmartToolsApk.apk");
        
        try {
            System.out.println(apk.getAbsolutePath());
            mCurDevice.installPackage(apk.getAbsolutePath(), false, new String[]{});
        } catch (InstallException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestBack(Object o) {
        mDeviceInfo = (IDeviceInfo) o;
        System.out.println("DeviceConnectWorker: app onRequestBack!");
        
        synchronized (mLock) {
            mLock.notify();
        }
    }
}