package com.nj.simba.page.device;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.android.ddmlib.IDevice;
import com.nj.simba.app.MainFrame;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.IDeviceInfo;
import com.nj.simba.ctrls.MetroCoolBtn;
import com.nj.simba.ctrls.TabPanel;
import com.nj.simba.ctrls.ThemeLabel;
import com.nj.simba.utils.Config;

public class PageMyDevice extends TabPanel implements ActionListener {

    private DevicePanel mDevicePanel;
    private DefaultListModel mDeviceListModel;
    private JList mDeviceList;
    private JLabel mConnectState;
    private JProgressBar mBatteryBar;
    private JProgressBar mRombar;
    private ThemeLabel mRomDetail;
    private ThemeLabel mBatteryDetail;
    private ThemeLabel mSdCardDetail;
    private JProgressBar mSdCardBar;
    private ThemeLabel mDeviceInfo;

    public PageMyDevice(JPanel tabPanel, int x, int y,
            int w, int h) {
        super(tabPanel, x, y, w, h);
    }
    
    public PageMyDevice(JPanel tabPanel) {
        super(tabPanel);
    }

    @Override
    protected void addBody() {
        super.addBody();
        createMainMenu();
        createBatteryProgress();
        createRomProgress();
        createSDProgress();
    }

    private void createMainMenu() {
        JLabel title = new ThemeLabel(16, 0, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">我的手机</h2></html>");
        mBodyPanel.add(title);

        JButton btn1 = new MetroCoolBtn("应用管理", "res/apps.png", 16, 50, 126,75);
        btn1.setActionCommand(BTN_APPMGR);
        btn1.addActionListener(this);
        mBodyPanel.add(btn1);
        
        JButton btn2 = new MetroCoolBtn("我的照片", "res/picture.png", 16 + 132, 50, 126, 75);
        btn2.setActionCommand(BTN_PICTURE);
        btn2.addActionListener(this);
        mBodyPanel.add(btn2);
        
        JButton btn3 = new MetroCoolBtn("我的音乐", "res/music.png", 16 + 132 * 2, 50, 126, 75);
        btn3.setActionCommand(BTN_MUSIC);
        btn3.addActionListener(this);
        mBodyPanel.add(btn3);
        
        JButton btn4 = new MetroCoolBtn("我的铃声", "res/ringtone.png", 16, 50 + 79, 126, 75);
        btn4.setActionCommand(BTN_RINGTONE);
        btn4.addActionListener(this);
        mBodyPanel.add(btn4);
        
        JButton btn5 = new MetroCoolBtn("文件管理", "res/filer.png", 16 + 132, 50 + 79, 126, 75);
        btn5.setActionCommand(BTN_RINGTONE);
        btn5.addActionListener(this);
        mBodyPanel.add(btn5);
        
        JButton btn6 = new MetroCoolBtn("手机信息", "res/messages.png", 16 + 132 * 2, 50 + 79, 126, 75);
        btn6.setActionCommand(BTN_RINGTONE);
        btn6.addActionListener(this);
        mBodyPanel.add(btn6);

        title = new ThemeLabel(16, 200, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">手机工具</h2></html>");
        mBodyPanel.add(title);

        btn1 = new MetroCoolBtn("I2C测试", "res/icon1.png", "res/android.png",
        		16, 250, 0, 0);
        btn2 = new MetroCoolBtn("APK反编译", "res/icon2.png", "res/java.png",
        		16 + 132, 250, 0, 0);
        btn3 = new MetroCoolBtn("漏洞扫描", "res/icon3.png", "res/scan.png",
        		16 + 132 * 2, 250, 0, 0);
        btn4 = new MetroCoolBtn("内存清理", "res/icon6.png", "res/clear.png", 16,
                250 + 79, 0, 0);
        btn5 = new MetroCoolBtn("备份还原", "res/icon4.png", "res/store.png",
        		16 + 132, 250 + 79, 0, 0);
        btn6 = new MetroCoolBtn("搜索文件", "res/icon5.png", "res/search.png",
        		16 + 132 * 2, 250 + 79, 0, 0);
        mBodyPanel.add(btn1);
        mBodyPanel.add(btn2);
        mBodyPanel.add(btn3);
        mBodyPanel.add(btn4);
        mBodyPanel.add(btn5);
        mBodyPanel.add(btn6);
    }

    private void createBatteryProgress() {
        JLabel title;
        title = new ThemeLabel(16, 400, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">手机电量</h2></html>");
        mBodyPanel.add(title);
        
        mBatteryDetail = new ThemeLabel(0, 410, 420, 64, JLabel.TRAILING);
        mBatteryDetail.setText("123/456");
        mBodyPanel.add(mBatteryDetail);
        
        mBatteryBar = new JProgressBar(0, 100);
        mBatteryBar.setValue(75);
        mBatteryBar.setOpaque(false);
        mBatteryBar.setForeground(Color.WHITE);
        mBatteryBar.setBounds(16, 450, 420, 12);
        mBodyPanel.add(mBatteryBar);
    }

    private void createRomProgress() {
        JLabel title;
        title = new ThemeLabel(16, 460, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">手机ROM</h2></html>");
        mBodyPanel.add(title);
        
        mRomDetail = new ThemeLabel(0, 470, 420, 64, JLabel.TRAILING);
        mRomDetail.setText("123/456");
        mBodyPanel.add(mRomDetail);
        
        mRombar = new JProgressBar(0, 100);
        mRombar.setValue(15);
        mRombar.setOpaque(false);
        mRombar.setForeground(Color.WHITE);
        mRombar.setBounds(16, 510, 420, 12);
        mBodyPanel.add(mRombar);
    }
    
    private void createSDProgress() {
        JLabel title;
        title = new ThemeLabel(16, 510, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">SD卡</h2></html>");
        mBodyPanel.add(title);
        
        mSdCardDetail = new ThemeLabel(0, 520, 420, 64, JLabel.TRAILING);
        mSdCardDetail.setText("123/456");
        mBodyPanel.add(mSdCardDetail);
        
        mSdCardBar = new JProgressBar(0, 100);
        mSdCardBar.setValue(15);
        mSdCardBar.setOpaque(false);
        mSdCardBar.setForeground(Color.WHITE);
        mSdCardBar.setBounds(16, 560, 420, 12);
        mBodyPanel.add(mSdCardBar);
    }

    @Override
    protected void addRight() {
        super.addRight();
        
        JLabel title = new ThemeLabel(6, 0, 240, 64);
        title.setText("<html><h2 style=\"color:99cc00\">设备列表</h2></html>");
        mRightPanel.add(title);
        
        mDeviceListModel = new DefaultListModel();
        mDeviceList = new JList(mDeviceListModel);
        mDeviceList.setBounds(6, 50, Config.PANEL_RIGHT_WIDTH-6*2, 120);
        mDeviceList.setOpaque(false);
        mDeviceList.setCellRenderer(new DeviceListRender());
        mDeviceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mRightPanel.add(mDeviceList);
        
        mConnectState = new ThemeLabel(6, 170, Config.PANEL_RIGHT_WIDTH-6*2, 64);
        mConnectState.setText("连接设备中。。。");
        mRightPanel.add(mConnectState);
        
        JLabel help = new ThemeLabel(6, 230, Config.PANEL_RIGHT_WIDTH-6*2, 64);
        help.setText("<html>注意：请在设置里面，打开手机开发者选项。");
        mRightPanel.add(help);
        
        mDeviceInfo = new ThemeLabel(6, 360, Config.PANEL_RIGHT_WIDTH-6*2, 200);
        mRightPanel.add(mDeviceInfo);
        
    }
    
    @Override
    protected void addLeft() {
        super.addLeft();
        
        mDevicePanel = new DevicePanel(mContentLeft);
        mDevicePanel.createPanel();
    }

    @Override
    public void deviceConnected(SmartToolsApp app) {
        super.deviceConnected(app);
        updateDeviceInfo();
        mDevicePanel.updateDeviceInfo(app);
        mConnectState.setText("连接成功，欢迎使用！");
        
        try {
            int level = mCurDevice.getBatteryLevel();
            mBatteryBar.setValue(level);
            mBatteryDetail.setText(String.format("剩余电量: %d%%", level));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        IDeviceInfo deviceInfo = app.getDeviceInfo();
        long romTotal = deviceInfo.mMemInfo.romTotal;
        long romAvail = deviceInfo.mMemInfo.romAvail;
        
        System.out.println(String.format("%d/%d", romTotal-romAvail, romTotal));
        mRombar.setMaximum((int) romTotal);
        mRombar.setValue((int) (romTotal - romAvail));
        
        if ( romTotal / romAvail > 20 ) {
            mRombar.setForeground(Color.RED);
        } else {
            mRombar.setForeground(Color.GREEN);
        }
        mRomDetail.setText(String.format("已使用: %dM/%dM", (romTotal-romAvail)>>10, romTotal>>10));
        
        long sdTotal = deviceInfo.mMemInfo.sd0Total;
        long sdAvail = deviceInfo.mMemInfo.sd0Avail;
        if ( sdTotal / sdAvail > 20 ) {
            mSdCardBar.setForeground(Color.RED);
        } else {
            mSdCardBar.setForeground(Color.GREEN);
        }
        mSdCardDetail.setText(String.format("已使用: %dM/%dM", (sdTotal-sdAvail)>>10, sdTotal>>10));
        
        //deviceListChanged(app);
    }
    
    @Override
    public void deviceDisconnected(SmartToolsApp app) {
        super.deviceDisconnected(app);
        mConnectState.setText("手机连接断开！");
        updateDeviceInfo();
        System.out.println("PageMyDevice: deviceDisconnected");
    }
    
    @Override
    public void deviceListChanged(SmartToolsApp app) {
        super.deviceListChanged(app);
        
        mDeviceListModel.clear();
        
        IDevice[] devices = app.getDevices();
        for (IDevice device : devices) {
            String name = device.getName();
            String state = device.getState().name();
            mDeviceListModel.addElement(name + " : " + state);
        }
    }

    @Override
    protected void setTabPageName() {
        mTabPageName = "my-device";
    }
    
    public void updateDeviceInfo() {
        Map<String, String> props = null;
        String name = "N/A";
        String version = "N/A";
        String date = "N/A";
        String finger = "N/A";

        if (mCurDevice != null && mCurDevice.arePropertiesSet()) {
            props = mCurDevice.getProperties();
            name = props.get("ro.product.name");
            version = props.get("ro.build.display.id");
            date = props.get("ro.build.date");
            finger = props.get("ro.build.fingerprint");
        }

        final String display = String.format(Config.device_info_format2, name,
                version, date, finger);
        System.out.println(display);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mDeviceInfo.setText(display);
            }
        });
    }

    private static final String BTN_APPMGR    = "appmgr";
    private static final String BTN_PICTURE   = "picture";
    private static final String BTN_MUSIC     = "music";
    private static final String BTN_RINGTONE  = "ringtone";
    private static final String BTN_FILER     = "filer";
    private static final String BTN_MESSAGE   = "message";
    private static final String BTN_AUTOTEST  = "autotest";
    private static final String BTN_APKDECODE = "apkdecode";
    private static final String BTN_BACKDOOR  = "backdoor";
    private static final String BTN_MEMORYCLR = "memoryclr";
    private static final String BTN_BACKUP    = "backup";
    private static final String BTN_SEARCH   = "search";

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        int which = 0;
        
        if (action.equals(BTN_APPMGR)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_PICTURE)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_MUSIC)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_RINGTONE)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_FILER)) {
            which = MainFrame.PANEL_FILER;
        } else if (action.equals(BTN_MESSAGE)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_AUTOTEST)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_APKDECODE)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_BACKDOOR)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_MEMORYCLR)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_BACKUP)) {
            which = MainFrame.PANEL_APPMGR;
        } else if (action.equals(BTN_SEARCH)) {
            which = MainFrame.PANEL_FILER;
        } 
        
        SmartToolsApp.getApp().getMainFrm().showPanel(which);
    }
}
