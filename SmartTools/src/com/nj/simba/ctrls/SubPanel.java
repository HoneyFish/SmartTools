package com.nj.simba.ctrls;

import java.awt.Color;
import java.awt.event.MouseAdapter;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.nj.simba.base.IDeviceListener;


public class SubPanel extends MouseAdapter{
    protected JPanel mParentPanel;
    protected JLabel mBorderLabel;
    protected int mPosX;
    protected int mPosY;
    protected int mWidth;
    protected int mHeight;
    
    public SubPanel(JPanel parent, int x, int y, int w, int h) {
        mParentPanel = parent;
        mPosX   = x;
        mPosY   = y;
        mWidth  = w;
        mHeight = h;
    }
    
    public void addBorder() {
        mBorderLabel = new JLabel();
        mBorderLabel.setForeground(Color.WHITE);
        mBorderLabel.setOpaque(false);
        mBorderLabel.setBounds(mPosX, mPosY, mWidth, mHeight);
        mBorderLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        mParentPanel.add(mBorderLabel);
    }
    
    /**
     * for tool bar panel
     * @param parent
     */
    public SubPanel(JPanel parent) {
        mParentPanel = parent;
    }

    protected void setBounds(int x, int y, int w, int h) {
        mPosX   = x;
        mPosY   = y;
        mWidth  = w;
        mHeight = h;
    }
    
    protected void createPanel() {
    }
    
    public void add(JComponent comp) {
        mParentPanel.add(comp);
    }
}
