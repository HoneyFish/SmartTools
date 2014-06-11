package com.nj.simba;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class IDeviceInfo {
    public AppInfo[] mAppsInfo;
    public MemInfo mMemInfo;
    public DeviceFeature mFeatures;
    
    byte[] toBytes() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(ba);

        /**
         * 1. write mAppsInfo
         */
        ds.writeInt(mAppsInfo.length);
        for (AppInfo app : mAppsInfo) {
            ds.writeUTF(app.name);
            ds.writeUTF(app.pkgName);
            ds.writeUTF(app.pkgPath);
            ds.writeUTF(app.versionName);
            ds.writeUTF(app.installDate);

            ds.writeBoolean(app.isSystem);
            ds.writeBoolean(app.isCtsPass);
            ds.writeBoolean(app.isRunning);

            ds.writeInt(app.uid);
            ds.writeInt(app.size);

            ds.writeInt(app.icon.length);
            ds.write(app.icon);
        }
        
        /**
         * 2. write mMemInfo
         */
        ds.writeLong(mMemInfo.romTotal);
        ds.writeLong(mMemInfo.romAvail);
        ds.writeLong(mMemInfo.ramTotal);
        ds.writeLong(mMemInfo.ramAvail);
        ds.writeLong(mMemInfo.sd0Total);
        ds.writeLong(mMemInfo.sd0Avail);
        ds.writeUTF(mMemInfo.sd0Path);

        /**
         * 3. write mFeatures
         */
        ds.writeInt(mFeatures.lcdWidth);
        ds.writeInt(mFeatures.lcdHeight);
        ds.writeFloat(mFeatures.lcdDensity);
        ds.writeInt(mFeatures.androidVersion);
        ds.writeUTF(mFeatures.imageVersion);
        ds.writeUTF(mFeatures.deviceName);
        
        return ba.toByteArray();
    }
    
    public static IDeviceInfo creator(byte[] bytes) throws IOException {
        IDeviceInfo deviceInfo = new IDeviceInfo();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        DataInputStream din = new DataInputStream(bis);
        
        
        /**
         * 1. read mAppsInfo
         */
        int size = din.readInt();
        deviceInfo.mAppsInfo = new AppInfo[size];
        
        for (int i = 0; i < size; i++) {
            deviceInfo.mAppsInfo[i] = new AppInfo();

            deviceInfo.mAppsInfo[i].name = din.readUTF();
            deviceInfo.mAppsInfo[i].pkgName = din.readUTF();
            deviceInfo.mAppsInfo[i].pkgPath = din.readUTF();
            deviceInfo.mAppsInfo[i].versionName = din.readUTF();
            deviceInfo.mAppsInfo[i].installDate = din.readUTF();

            deviceInfo.mAppsInfo[i].isSystem = din.readBoolean();
            deviceInfo.mAppsInfo[i].isCtsPass = din.readBoolean();
            deviceInfo.mAppsInfo[i].isRunning = din.readBoolean();

            deviceInfo.mAppsInfo[i].uid = din.readInt();
            deviceInfo.mAppsInfo[i].size = din.readInt();
            System.out.println("uid:" + deviceInfo.mAppsInfo[i].uid);

            deviceInfo.mAppsInfo[i].icon = new byte[din.readInt()];
            System.out.println("icon length:" + deviceInfo.mAppsInfo[i].icon.length);
            din.readFully(deviceInfo.mAppsInfo[i].icon);
        }
        
        /**
         * 2. read mMemInfo
         */
        deviceInfo.mMemInfo = new MemInfo();
        deviceInfo.mMemInfo.romTotal = din.readLong();
        deviceInfo.mMemInfo.romAvail = din.readLong();
        deviceInfo.mMemInfo.ramTotal = din.readLong();
        deviceInfo.mMemInfo.ramAvail = din.readLong();
        deviceInfo.mMemInfo.sd0Total = din.readLong();
        deviceInfo.mMemInfo.sd0Avail = din.readLong();
        deviceInfo.mMemInfo.sd0Path = din.readUTF();
        
        /**
         * 3. read mFeatures
         */
        deviceInfo.mFeatures = new DeviceFeature();
        deviceInfo.mFeatures.lcdWidth = din.readInt();
        deviceInfo.mFeatures.lcdHeight = din.readInt();
        deviceInfo.mFeatures.lcdDensity = din.readFloat();
        deviceInfo.mFeatures.androidVersion = din.readInt();
        deviceInfo.mFeatures.imageVersion = din.readUTF();
        deviceInfo.mFeatures.deviceName = din.readUTF();
        
        return deviceInfo;
    }
    
    public static class AppInfo {
        public String name;
        public String pkgName;
        public String pkgPath;
        public String versionName;
        public String installDate;

        public boolean isSystem;
        public boolean isCtsPass;
        public boolean isRunning;

        public int uid;
        public int size;
        public byte[] icon;
    }

    public static class MemInfo {
        public long romTotal;
        public long romAvail;
        public long ramTotal;
        public long ramAvail;
        public long sd0Total;
        public long sd0Avail;
        public String sd0Path;
        
        /***************
        long sd2Total;
        long sd2Avail;
        String sd2Path;
        ***************/
    }

    public static class DeviceFeature {
        public float lcdDensity;
        public int lcdWidth;
        public int lcdHeight;
        
        public int androidVersion;
        public String imageVersion;
        public String deviceName;
        
        /********************
        String linuxVersion;
        String glVersion;
        String cpuVersion;
        String cpuFreq;
        String androidVersion;
        ***********************/
    }
}
