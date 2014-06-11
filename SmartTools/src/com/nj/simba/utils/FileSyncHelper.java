package com.nj.simba.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.SyncService;
import com.android.ddmlib.TimeoutException;
import com.android.ddmlib.FileListingService.FileEntry;
import com.android.ddmlib.SyncService.ISyncProgressMonitor;
import com.nj.simba.ctrls.MyProgessBar;
import com.nj.simba.utils.SyncProgressHelper.SyncRunnable;

public class FileSyncHelper implements SyncRunnable {
    IDevice mCurDevice;
    FileEntry[] mRemoteFiles;
    SyncService mSyncSvr;
    String mRemoteFullPath;
    String mLocalName;
    int mFileSize;

    public FileSyncHelper(IDevice device, FileEntry remoteFile) {
        mCurDevice = device;
        mRemoteFiles = new FileEntry[] { remoteFile };
    }

    public FileSyncHelper(IDevice device, ArrayList<FileEntry> remoteFiles) {
        mCurDevice = device;
        mRemoteFiles = remoteFiles.toArray(new FileEntry[remoteFiles.size()]);
    }

    public FileSyncHelper(IDevice device, String remoteFullPath, String localName, int size) {
        mCurDevice = device;
        mRemoteFullPath = remoteFullPath;
        mLocalName = localName;
        mFileSize = size;
    }
    
    public long getCurFileSize() {
        return mFileSize;
    }

    public void syncOut() {
        try {
            SyncProgressHelper.run(this, "Pulling file(s) from the device");
        } catch (SyncException e) { // maybe canceled
            e.printStackTrace();
            System.out.println("SyncException");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception");
        }
    }

    @Override
    public void run(ISyncProgressMonitor monitor) throws SyncException,
            TimeoutException, IOException {

        
        try {
            mSyncSvr = mCurDevice.getSyncService();

            if ( mRemoteFullPath != null ) {
                System.out.println("-->run:" + mRemoteFullPath);
                System.out.println("-->run:" + mLocalName);
                System.out.println("-->run:" + mFileSize);
                
                SyncProgressMonitor syncMonitor = (SyncProgressMonitor) monitor;
                MyProgessBar bar = (MyProgessBar) syncMonitor.getProgressMonitor();
                bar.setMaximum(mFileSize);
                mSyncSvr.pullFile(mRemoteFullPath, new File(mLocalName).getAbsolutePath(), monitor);
                return;
            }
            
            String filePullName = null;
            FileEntry singleEntry = null;

            if (mRemoteFiles.length == 1) {
                singleEntry = mRemoteFiles[0];
                if (singleEntry.getType() == FileListingService.TYPE_FILE) {
                    filePullName = singleEntry.getName();
                }
            }

            if (filePullName != null) {

                mSyncSvr.pullFile(singleEntry,
                        new File(filePullName).getAbsolutePath(), monitor);

            } else {
                mSyncSvr.pull(mRemoteFiles, new File("./").getAbsolutePath(),
                        monitor);
            }

        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        if (mSyncSvr != null) {
            mSyncSvr.close();
        }
    }
}
