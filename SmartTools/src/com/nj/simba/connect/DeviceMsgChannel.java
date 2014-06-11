package com.nj.simba.connect;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.zip.GZIPInputStream;
import com.nj.simba.IDeviceInfo;
import com.nj.simba.base.IDeviceReqListener;
import com.nj.simba.utils.Config;


public class DeviceMsgChannel extends Thread {
    private BlockingQueue<DeviceMessage> mQueue = null;

	public DeviceMsgChannel() {
	    mQueue = new ArrayBlockingQueue<DeviceMessage>(10);
	}
	
	@Override
    public void run() {
	    while(true) {
	        DeviceMessage msg = null;
            try {
                msg = mQueue.take();
                
                if ( msg.what == MSG_QUIT ) {
                    mQueue.clear();
                    break;
                }
                
                process(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
	    }
	    
	    System.out.println("msg looper quit!");
    }
	
	public void sendReqMsg(DeviceMessage msg) {
	    try {
            mQueue.put(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}

	private void process(DeviceMessage msg) {
	    final IDeviceReqListener listener = msg.who;
	    final int what = msg.what;
	    
	    System.out.println("process:" + msg.what);
	    
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                listener.onRequestBack(doProcess(what));
            }
        }).start();
    }

    protected Object doProcess(int what) {
        Object obj = null;
        
        switch(what) {
        case MSG_GET_APPS:
            obj = requestApps();
            break;
        case MSG_GET_PROPS:
            break;
        default:
            break;
        }
        
        return obj;
    }
	
	/**
	 * talk with device get the list of packages on device
	 * 
	 * @return
	 */
	public IDeviceInfo requestApps() {
	    IDeviceInfo deviceInfo = null;

		try {
			Socket sokcet = new Socket("localhost", Config.HOST_PORT);
			boolean isConnected = sokcet.isConnected();
			System.out.println("isConnected:" + isConnected);

			OutputStream out = sokcet.getOutputStream();
			InputStream in = sokcet.getInputStream();
			DataOutputStream dos = new DataOutputStream(out);
			DataInputStream dis = new DataInputStream(in);

			/** 1、send CMD **/
			dos.writeUTF("CMD");

			/** 2、recv DAT **/
			GZIPInputStream gis = new GZIPInputStream(in);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] data = new byte[1024];    
			int len = 0;
			
			while((len = gis.read(data) )!= -1) {
			    baos.write(data, 0, len);
			}
			
			deviceInfo = IDeviceInfo.creator(baos.toByteArray());

			/** 3、send ACK **/
			dos.writeUTF("ACK");
			sokcet.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return deviceInfo;
	}

	public static final int MSG_GET_APPS  = 0;
	public static final int MSG_GET_PROPS = 1;
	public static final int MSG_QUIT      = -1;
}
