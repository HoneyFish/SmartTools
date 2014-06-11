package com.nj.simba.ctrls;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import com.nj.simba.utils.IProgressMonitor;

@SuppressWarnings("serial")
public class MyProgessBar extends JProgressBar implements IProgressMonitor {

    @Override
    public void beginTask(String mName, int totalWork) {
        System.out.println("beginTask1: " + totalWork);
        
        if ( totalWork != 0 ) {
            setMaximum(totalWork);
        }
        
        System.out.println("beginTask2: " + getMaximum());
    }

    @Override
    public void done() {
        System.out.println("MyProgessBar: done");
        setValue(100);
        setVisible(false);
    }

    @Override
    public void worked(final int work) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setValue(getValue() + work);
            }
        });
    }

    @Override
    public boolean isCanceled() {
        //setValue(0);
        return false;
    }

    @Override
    public void subTask(String name) {
    }

   
}
