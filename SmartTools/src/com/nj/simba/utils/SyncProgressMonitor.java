package com.nj.simba.utils;

import javax.swing.SwingUtilities;

import com.android.ddmlib.SyncService.ISyncProgressMonitor;

/**
 * Implementation of the {@link ISyncProgressMonitor} wrapping an Eclipse {@link IProgressMonitor}.
 */
class SyncProgressMonitor implements ISyncProgressMonitor {

    private IProgressMonitor mMonitor;
    private String mName;

    public SyncProgressMonitor(IProgressMonitor monitor, String name) {
        mMonitor = monitor;
        mName = name;
    }

    @Override
    public void start(int totalWork) {
        System.out.println("start: totalWork=" + totalWork);
        mMonitor.beginTask(mName, totalWork);
    }

    @Override
    public void stop() {
        mMonitor.done();
    }

    @Override
    public void advance(final int work) {
        mMonitor.worked(work);
    }

    @Override
    public boolean isCanceled() {
        return mMonitor.isCanceled();
    }

    @Override
    public void startSubTask(String name) {
        System.out.println("startSubTask: name=" + name);
        mMonitor.subTask(name);
    }
    
    public IProgressMonitor getProgressMonitor() {
        return mMonitor;
    }
}