package com.nj.simba.connect;

import java.util.List;
import javax.swing.SwingWorker;
import com.android.ddmlib.IShellOutputReceiver;
import com.nj.simba.base.IDeviceReqListener;

public class DeviceCmdWorker extends SwingWorker<List, Void> implements
        IShellOutputReceiver, IDeviceReqListener {

    @Override
    public void onRequestBack(Object o) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addOutput(byte[] arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    protected List doInBackground() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
