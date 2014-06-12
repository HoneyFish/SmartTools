package com.nj.simba.app;

import java.awt.CardLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.android.ddmlib.IDevice;
import com.nj.simba.ctrls.MyProgessBar;
import com.nj.simba.page.appmgr.PageAppMgr;
import com.nj.simba.page.device.DevicePanel;
import com.nj.simba.page.device.PageMyDevice;
import com.nj.simba.page.filer.PageFiler;
import com.nj.simba.page.logcat.PageLogcat;
import com.nj.simba.utils.Config;
import com.nj.simba.utils.I2CTest;
import com.nj.simba.utils.Utils;

///TODO SwingWork
@SuppressWarnings("serial")
public class MainFrame extends JFrame {

	/**
	 * Tab Panel PANEL_AUTOTEST: CTS, GTS, Verifier, and another tests
	 * PANEL_APPMGR: list packages of device, view information, remove, etc.
	 * PANEL_FILER: file explore of device PANEL_LOGCAT: get log of device
	 */
	public static final int PANEL_MYDEVICE = 0;
	public static final int PANEL_APPMGR = 1;
	public static final int PANEL_FILER = 2;
	public static final int PANEL_LOGCAT = 3;

	/**
	 * The main container of all panel 1) tool bar 2) left device panel 3) main
	 * panel with right panel( detail panel)
	 */
	private JPanel mRootPanel = null;
	private int mPanelWidth = 0;
	private int mPanelHeight = 0;

	/**
	 * main panel as container of tab pages
	 */
	private JPanel mTabPanel;
	private CardLayout mCardLayout;
	
	private PageMyDevice mMyDevicePanel;
	private PageLogcat mLogcatPanel;
	private PageAppMgr mAppMgrPanel;
	private DevicePanel mDevicePanel;
	private PageFiler mFilerPanel;
	
	private int mWhichTab = PANEL_MYDEVICE;
    private IDevice mCurDevice = null;
	private JPanel mLeftPanel;
    private MyProgessBar mProgressBar;
    private I2CTest mI2CTest;

	void initFrame() {
		mRootPanel = (JPanel) getContentPane();

		/** set background transparent, get rid of white panel **/
		mRootPanel.setOpaque(false);
		mRootPanel.setLayout(null);

		/** set background image as theme **/
		ImageIcon themeImg = Utils.getResImage(Config.background);
		JLabel background = new JLabel(themeImg);

		mPanelWidth = Config.WIN_WIDTH;
		mPanelHeight = Config.WIN_HEIGHT;
		background.setBounds(0, 0, mPanelWidth, mPanelHeight);
		getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));

		/** adjust size according to background image **/
		setSize(mPanelWidth, mPanelHeight);
		setResizable(false);
	}

	void addPanel() {
		/** create tool bar **/
		createToolbarPanel();
		
		/** create main panel, as tab panel **/
		createPages();
		
		/** global prorgressbar **/
		newProgressBar();
	}

	void createPages() {
		mTabPanel = new JPanel();

		mCardLayout = new CardLayout();
		mTabPanel.setLayout(mCardLayout);
		mTabPanel.setOpaque(false);
		
		mTabPanel.setBounds(0, Config.OFFSET_TOP, Config.WIN_WIDTH, Config.WIN_PANEL_H);
		
		mRootPanel.add(mTabPanel);

		mMyDevicePanel = new PageMyDevice(mTabPanel);
		mMyDevicePanel.createPage();

		mAppMgrPanel = new PageAppMgr(mTabPanel);
		mAppMgrPanel.createPage();
		
		mLogcatPanel = new PageLogcat(mTabPanel);
		mLogcatPanel.createPage();
		
		mFilerPanel = new PageFiler(mTabPanel);
		mFilerPanel.createPage();
		
		loadPlugins();
	}

	private void loadPlugins() {
        File pluginsDir = new File("./plugins");
        if ( pluginsDir.exists() ) {
            System.out.println("---**** find plugins! ***-----\n");
        } else {
            System.out.println("---**** no plugins directory! ***-----\n");
            return;
        }
        
        File pluginsConfig = new File("./plugins/plugins.xml");
        if ( pluginsConfig.exists() ) {
            System.out.println("---**** find plugins config ! ***-----\n");
        } else {
            System.out.println("---**** no plugins xml! ***-----\n");
            return;
        }
        
        mI2CTest = new I2CTest(mTabPanel);
        mI2CTest.createPage();
        
    }

    void createToolbarPanel() {
		ToolbarPanel toolbar = new ToolbarPanel(mRootPanel);
		toolbar.createPanel();
	}

	public void showPanel(int which) {
		String key = null;
		
		mWhichTab = which;
		
		switch (which) {
		case PANEL_MYDEVICE:
			key = "my-device";
			break;
		case PANEL_APPMGR:
			key = "app-mgr";
			break;
		case PANEL_FILER:
			key = "filer";
			break;
		case PANEL_LOGCAT:
			key = "logcat";
			break;
		default:
			key = "my-device";
			break;
		}

		mCardLayout.show(mTabPanel, key);
	}

	public void updateDeviceList() {
	    SmartToolsApp app = SmartToolsApp.getApp();
        mMyDevicePanel.deviceListChanged(app);
    }

    public void onStart(SmartToolsApp app) {
        setLocationRelativeTo(null);
        showPanel(MainFrame.PANEL_MYDEVICE);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(app);
    }

    public void deviceConnected() {
        SmartToolsApp app = SmartToolsApp.getApp();
        mMyDevicePanel.deviceConnected(app);
        mAppMgrPanel.deviceConnected(app);
        mFilerPanel.deviceConnected(app);
        mLogcatPanel.deviceConnected(app);
    }
    
    public void deviceDisconnected() {
        SmartToolsApp app = SmartToolsApp.getApp();
        mMyDevicePanel.deviceDisconnected(app);
        mAppMgrPanel.deviceDisconnected(app);
        mFilerPanel.deviceDisconnected(app);
        mLogcatPanel.deviceDisconnected(app);
    }
    
    public void deviceChanged(int changeMask) {
        // TODO Auto-generated method stub        
    }

    public void onAppExit() {
        mMyDevicePanel.onAppExit();
        mAppMgrPanel.onAppExit();
        mFilerPanel.onAppExit();
        mLogcatPanel.onAppExit();
    }

    public MyProgessBar newProgressBar() {
        mProgressBar = new MyProgessBar();
        mProgressBar.setOpaque(false);
        mProgressBar.setForeground(Color.GREEN);
        mProgressBar.setBounds(6, 90, Config.WIN_WIDTH-16, 8);
        mProgressBar.setValue(80);
        mProgressBar.setVisible(false);
        mRootPanel.add(mProgressBar);
        return null;
    }
    
    public MyProgessBar getProgressBar() {
        mProgressBar.setValue(0);
        mProgressBar.setVisible(true);
        return mProgressBar;
    }
}
