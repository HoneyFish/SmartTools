package com.nj.simba.cts;

public class CtsResultReset {
    private String mCtsReportDir = null;
    
    public CtsResultReset(String ctsReportDir) {
        mCtsReportDir = ctsReportDir;
    }
    
    protected String loadCtsReport() {
        return null;
    }
    
    public void resetCtsReport() {
        
    }
    
    public void resetCtsReport(String ctsReportDir) {
        mCtsReportDir = ctsReportDir;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
