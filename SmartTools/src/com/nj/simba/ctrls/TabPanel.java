package com.nj.simba.ctrls;


import java.util.List;

import javax.swing.JPanel;

import com.android.ddmlib.IDevice;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.base.IDeviceListener;
import com.nj.simba.utils.Config;

/**
 * Singleton
 * @author Honey
 *
 */
public abstract class TabPanel extends SubPanel implements IDeviceListener {
    protected JPanel mTabRootPanel;
    protected JPanel mPageRootPanel;
    protected String mTabPageName;
    
    protected SubPanel mBodyPanel;
    protected SubPanel mLeftPanel;
    protected SubPanel mRightPanel;
    
    protected JPanel mContentBody;
	protected JPanel mContentRight;
	protected JPanel mContentLeft;
	
	protected IDevice mCurDevice;
	protected boolean mIsDeviceChanged;
    
    /**
     * @param parent: top1 level panel of application
     * @param tabPanel: top1 level panel of main body 288, 100, 450, 590
     * @param x: default 288
     * @param y: default 100
     * @param w: default 450
     * @param h: default 586
     */
    public TabPanel(JPanel tabPanel, int x, int y, int w, int h) {
        super(tabPanel, x, y, w, h);
        
        mTabRootPanel = tabPanel;
        
        mPageRootPanel = new JPanel();
        mPageRootPanel.setLayout(null);
        mPageRootPanel.setOpaque(false);
        mPageRootPanel.setBounds(0, 0, w, h);
    }
    
    /**
     * @param parent
     * @param tabPanel
     */
    public TabPanel(JPanel tabPanel) {
        this(tabPanel, 0, 0, Config.WIN_WIDTH, Config.WIN_PANEL_H);
    }
    
    public void createPage() {
        setTabPageName();

        addLeft();
        addRight();
        addBody();
        
        if ( mContentLeft != null ) {
            mPageRootPanel.add(mContentLeft);
            mLeftPanel.addBorder();
        }
        
        if ( mContentRight != null ) {
            mPageRootPanel.add(mContentRight);
            mRightPanel.addBorder();
        }
        
        if ( mContentBody != null ) {
            mPageRootPanel.add(mContentBody);
            mBodyPanel.addBorder();
        }
        
        mTabRootPanel.add(mPageRootPanel, mTabPageName);
    }
    
    protected void createPanel() {
        createPage();
    }
    
    public String getTabPageName() {
        return mTabPageName;
    }
    
    protected void addBody() {
        mContentBody = new JPanel();
        mContentBody.setLayout(null);
        mContentBody.setOpaque(false);
        
        int offsetX = 0;
        int width = Config.WIN_WIDTH;
        
        if ( mLeftPanel != null ) {
            offsetX += mLeftPanel.mPosX;
            offsetX += mLeftPanel.mWidth;
            offsetX += Config.PANEL_GAP;
            width -= Config.PADDING_LEFT + mLeftPanel.mWidth;
            width -= Config.PANEL_GAP;
        } else {
            width -= Config.PADDING_LEFT;
        }
        
        if ( mRightPanel != null ) {
            width -= Config.PANEL_GAP;
            width -= mRightPanel.mWidth + Config.PADDING_RIGHT;
        }
        
        mContentBody.setBounds(offsetX, mPosY, width, Config.WIN_PANEL_H);
        mBodyPanel = new SubPanel(mContentBody, 0, 0, width, Config.WIN_PANEL_H);
    }
    
    protected void addLeft(){
        mContentLeft = new JPanel(); 
        mContentLeft.setLayout(null);
        mContentLeft.setOpaque(false);
        mContentLeft.setBounds(0, 0, Config.PANEL_LEFT_WIDTH, Config.WIN_PANEL_H);
        mLeftPanel = new SubPanel(mContentLeft, 0, 0, Config.PANEL_LEFT_WIDTH, Config.WIN_PANEL_H);
    }
    
    protected void addRight(){
        mContentRight = new JPanel();
        mContentRight.setLayout(null);
        mContentRight.setOpaque(false);
        mContentRight.setBounds(720, 0, 230, Config.WIN_PANEL_H);
        mRightPanel = new SubPanel(mContentRight, 0, 0, 230, Config.WIN_PANEL_H);
    }
    
   
    protected abstract void setTabPageName();
    
    @Override
    public void deviceChanged(SmartToolsApp app, int changeMask){
        // TODO Auto-generated method stub  
    }
    
    @Override
    public void deviceListChanged(SmartToolsApp app) {
        // TODO Auto-generated method stub        
    }
    
    public void onAppExit() {
        // TODO Auto-generated method stub  
    }
    
    @Override
    public void deviceConnected(SmartToolsApp app) {
        IDevice device = app.getCurDevice();
        mIsDeviceChanged = false;
        
        if ( mCurDevice != device ) {
            mIsDeviceChanged = true;
        }
        
        mCurDevice =  device;
    }
    
    @Override
    public void deviceDisconnected(SmartToolsApp app) {
        mCurDevice = null;
    }
    
    
}
