package com.nj.simba.app;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.IDevice;
import com.nj.simba.connect.DeviceConnectWorker;
import com.nj.simba.connect.DeviceMessage;
import com.nj.simba.connect.DeviceMsgChannel;
import com.nj.simba.IDeviceInfo;
import com.nj.simba.IDeviceInfo.AppInfo;
import com.nj.simba.utils.Config;
import com.nj.simba.utils.Utils;

public class SmartToolsApp extends WindowAdapter implements IDeviceChangeListener, IDebugBridgeChangeListener{
	private MainFrame mMainFrame = new MainFrame();
	private static SmartToolsApp mApp = new SmartToolsApp();
	private IDevice mCurDevice = null;
	private List<IDevice> mDevices = new ArrayList<IDevice>();
	private AndroidDebugBridge mAdb = null;
	private DeviceConnectWorker mWorker = null;
	private IDeviceInfo mDeviceInfo;
	private DeviceMsgChannel mDeviceChannel = null;

	/** Singleton Application Object **/
	private SmartToolsApp() {
	}
	
	public static SmartToolsApp getApp() {
        return mApp;
    }

	void initApp() {
		UIManager.put("ScrollBar.width", new Integer(Config.SCROLLBAR_WIDTH));

		mMainFrame.initFrame();
		mMainFrame.addPanel();

		String adbPath = Utils.findAdb();
		AndroidDebugBridge.init(false /* debugger support */);
		mAdb = AndroidDebugBridge
				.createBridge(adbPath, false /* forceNewBridge */);
		
		mDeviceChannel = new DeviceMsgChannel();
		mDeviceChannel.start();
	}

	void startApp() {
	    mMainFrame.onStart(this);
	}

	public MainFrame getMainFrm() {
		return mMainFrame;
	}

	static void createAndShowGUI() {
		SmartToolsApp app = getApp();
		
		Utils.loadCache();
		app.initApp();
		app.startApp();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	@Override
	public void deviceChanged(IDevice device, int changeMask) {
		System.out.println("deviceChanged:" + device.getName());
		
		if ( mCurDevice == device ) {
		    mMainFrame.deviceChanged(changeMask);
		}
	}

	@Override
	public void deviceConnected(IDevice device) {
		System.out.println("deviceConnected:" + device.getName());
		updateDeviceList();
	}

	@Override
	public void deviceDisconnected(IDevice device) {
		System.out.println("deviceDisconnected:" + device.getName());
		if ( mCurDevice == device ) {
		    mCurDevice = null;
		}
		
		mMainFrame.deviceDisconnected();
		updateDeviceList();
	}

     @Override
    public void bridgeChanged(AndroidDebugBridge arg0) {
        updateDeviceList();
    }

	@Override
	public void windowClosing(WindowEvent e) {
		AndroidDebugBridge.removeDeviceChangeListener(this);
		mMainFrame.onAppExit();
	}

	@Override
	public void windowOpened(WindowEvent e) {
	    AndroidDebugBridge.addDeviceChangeListener(this);

	    updateDeviceList();
        System.out.println("device number:" + mDevices.size());
	}
	
	private void updateDeviceList() {
	    if (mCurDevice != null ) {
	        System.out.println("updateDeviceList" + mCurDevice.getState());
	    }
	    
        IDevice[] devices = mAdb.getDevices();
        mDevices.clear();
        
        for (IDevice device : devices) {
            mDevices.add(device);
            System.out.println(device.getName());
        }
        
        if (mDevices.size() > 0 && !mDevices.contains(mCurDevice)) {
            mCurDevice = mDevices.get(0);
            
            System.out.println("*********updateDeviceList connect device*********");
            mWorker = new DeviceConnectWorker(mCurDevice);
            mWorker.execute();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mMainFrame.updateDeviceList();
            }
        });
    }
	
	public void reConnectDevice(IDevice device) {
	    System.out.println("*********reConnectDevice*********");
	    
	    mCurDevice = device;
	    mWorker = new DeviceConnectWorker(mCurDevice);
	    mWorker.execute();
	}
	
	public AppInfo[] getApps() {
	    return mDeviceInfo.mAppsInfo;
	}
	
	public IDeviceInfo getDeviceInfo() {
        return mDeviceInfo;
    }
	
	public void sendMessage(DeviceMessage msg) {
        mDeviceChannel.sendReqMsg(msg);
    }
   
	public void setCurDevice(IDevice device) {
        mCurDevice = device;
    }
	
    public IDevice getCurDevice() {
        return mCurDevice;
    }

    public IDevice[] getDevices() {
        IDevice[] devices = new IDevice[mDevices.size()];
        for (int i = 0; i < mDevices.size(); i++) {
            devices[i] = mDevices.get(i);
        }
        return devices;
    }

    public void connected(IDeviceInfo deviceInfo) {
        mDeviceInfo = deviceInfo;
        mMainFrame.deviceConnected();
    }
	
}
