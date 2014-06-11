package com.nj.simba.utils;

import java.io.IOException;

import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.android.ddmlib.TimeoutException;
import com.nj.simba.app.MainFrame;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.ctrls.MyProgessBar;

public class SyncProgressHelper {
    /**
     * a runnable class run with an {@link ISyncProgressMonitor}.
     */
    public interface SyncRunnable {
        /** Runs the sync action */
        void run(ISyncProgressMonitor monitor) throws SyncException, IOException, TimeoutException;
        /** close the {@link SyncService} */
        void close();
    }
    
    /**
     * Runs a {@link SyncRunnable} in a {@link ProgressMonitorDialog}.
     * @param runnable The {@link SyncRunnable} to run.
     * @param progressMessage the message to display in the progress dialog
     * @param parentShell the parent shell for the progress dialog.
     *
     * @throws InvocationTargetException
     * @throws InterruptedException
     * @throws SyncException if an error happens during the push of the package on the device.
     * @throws IOException
     * @throws TimeoutException
     */
    public static void run(final SyncRunnable runnable, final String progressMessage )
            throws InterruptedException, SyncException, IOException,
            TimeoutException {
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                final Exception[] result = new Exception[1];
                MainFrame frm = SmartToolsApp.getApp().getMainFrm();
                MyProgessBar bar = frm.getProgressBar();
                
                try {
                    System.out.println("SyncProgressHelper: run");
                    runnable.run(new SyncProgressMonitor(bar, progressMessage));
                } catch (Exception e) {
                    result[0] = e;
                } finally {
                    runnable.close();
                }
            }
        }).start();
        
    }
}
