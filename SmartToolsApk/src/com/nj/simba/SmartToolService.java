package com.nj.simba;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.GZIPOutputStream;
import android.os.IBinder;
import android.util.Log;
import android.app.Service;
import android.content.Intent;


public class SmartToolService extends Service {
    private static final String TAG = "SmartTools";
    private ServerSocket mServer;
    private boolean mServerOn = true;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate!");

        super.onCreate();      

        try {
            mServer = new ServerSocket(13470);
            mServerThread.start();
        } catch (IOException e) {
            Log.d(TAG, e.toString());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy!");
        super.onDestroy();

        mServerOn = false;
    }

    /**
     * Server thread accept PC client request CMD: only support list
     * applications now TODO: XXXXX
     */
    private Thread mServerThread = new Thread(new Runnable() {

        @Override
        public void run() {
            Socket client = null;

            while (mServerOn) {
                try {
                    Log.d(TAG, "wait accept...!");
                    client = mServer.accept();
                    Log.d(TAG, "accept one pc client!");

                    talk(client.getInputStream(), client.getOutputStream());
                } catch (IOException e) {
                    Log.d(TAG, e.toString());
                }
            }
        }

        /**
         * talk with PC, and reply data to it
         * 
         * @param in
         * @param out
         */
        void talk(InputStream in, OutputStream out) {
            Log.d(TAG, "startSession!");

            DataInputStream dis = new DataInputStream(in);
            DataOutputStream dos = new DataOutputStream(out);

            /******** 1、read command *******/

            String cmd;
            try {
                cmd = dis.readUTF();
                Log.d(TAG, "CMD: " + cmd);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            /******** 2、reply client *******/
            try {
                replyClient(dos);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            /******** 3、wait ack *******/
            try {
                String ack = dis.readUTF();
                Log.d(TAG, "ACK: " + ack);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 1)图标 2)名称 3)是否系统包 4)是否违反CTS 5)包名 6)包路径 7)是否运行中 8)UID 1)名称 2)包名 3)包路径
         * 4)是否系统包 5)是否违反CTS 6)是否运行中 7)UID 8)图标
         * 
         * @param dos
         * @throws IOException
         */
        void replyClient(DataOutputStream dos) throws IOException {
            Log.d(TAG, "replyClient!");

            DviceInfoCollector deviceInfo = new DviceInfoCollector(SmartToolService.this);
            byte[] data = deviceInfo.toBytes();
            
            Log.d(TAG, "data.len!" + data.length);

            GZIPOutputStream gos = new GZIPOutputStream(dos);
            gos.write(data);
            dos.flush();
            gos.finish();
        }
    });

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

}
