package com.nj.simba.utils;

public interface IProgressMonitor {

    void beginTask(String mName, int totalWork);

    void done();

    void worked(int work);

    boolean isCanceled();

    void subTask(String name);

}
