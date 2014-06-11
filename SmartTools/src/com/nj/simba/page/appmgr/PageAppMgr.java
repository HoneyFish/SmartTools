package com.nj.simba.page.appmgr;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.IDeviceInfo.AppInfo;
import com.nj.simba.ctrls.TabPanel;
import com.nj.simba.ctrls.ThemeLabel;
import com.nj.simba.utils.Config;
import com.nj.simba.utils.FileSyncHelper;
import com.nj.simba.utils.Utils;


public class PageAppMgr extends TabPanel {
    private DefaultTableModel mDeviceAppsModel;
    private JTable mDeviceApps;
    private JLabel mRightTitle;
    private JLabel mRightIconLabel;
    
    private JButton mBtnSysApp;
    private JButton mBtnUserApp;
    private JButton mBtnMusic;
    private JButton mBtnPicture;
    private JButton mBtnRingTone;
    private JButton mBtnUninstall;
    private JButton mBtnGetApk;
    private JButton mBtnGetMore;
    
    private ThemeLabel mRightAppName;
    private ThemeLabel mRightAppVersion;
    private ThemeLabel mRightAppInstallDate;
    
    private int mCurSel = 0;
	private boolean mIsLastOK;
	private boolean mIsSysApps;
   
    private HashMap<String, AppInfo> mAppInfos;
    private JPanel mRightCardPanel;
    private CardLayout mRightCardLayout;
    private ThemeLabel mTipLabel;

    
    public PageAppMgr(JPanel parent, JPanel tabPanel, int x, int y, int w,
            int h) {
        super(tabPanel, x, y, w, h);
    }
    
    public PageAppMgr(JPanel tabPanel) {
        super(tabPanel);
    }

    @Override
    protected void addBody() {
        super.addBody();
        
        mDeviceAppsModel= new AppListModel();
        mDeviceApps = getAppList(mDeviceAppsModel);
        
        JScrollPane scroll = new JScrollPane(mDeviceApps);
        scroll.setBounds(8, 4, Config.PANEL_BODY_WIDTH - 8*2, Config.PANEL_LEFT_HEIGHT - 4*2);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        mBodyPanel.add(scroll);
    }

    private JTable getAppList(DefaultTableModel model) {
    	String columns[] = { "icon", "name", "package", "apk", "running", "uid" };
    	model.setColumnIdentifiers(columns);

    	JTable listApps = new JTable(model);
    	listApps.setBounds(0, 0, Config.PANEL_BODY_WIDTH - 8*2, 550);
    	listApps.setDefaultRenderer(Object.class, new AppTableRender());
    	listApps.setOpaque(false);
    	listApps.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
    	listApps.setRowHeight(36);
    	
    	TableColumnModel columnMode = listApps.getColumnModel();
    	columnMode.getColumn(0).setPreferredWidth(48);
    	columnMode.getColumn(1).setPreferredWidth(100);
    	columnMode.getColumn(2).setPreferredWidth(160);
    	columnMode.getColumn(3).setPreferredWidth(160);
    	
        listApps.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ( e.getClickCount() == 1 ) {
                    mCurSel = mDeviceApps.getSelectedRow();
                    updateRight();
                }
            }
        });

    	return listApps;
    }

	@Override
    protected void addRight() {
	    super.addRight();
	    
		mRightTitle = new ThemeLabel(6, 0, Config.PANEL_RIGHT_WIDTH, 32);
		mRightTitle.setText("<html><h2 style=\"color:99cc00\">应用信息</h2></html>");
        mRightPanel.add(mRightTitle);
        
        mRightCardLayout = new CardLayout();
        mRightCardPanel = new JPanel();
        mRightCardPanel.setLayout(mRightCardLayout);
        mRightCardPanel.setOpaque(false);
        mRightCardPanel.setBounds(0, 44, Config.PANEL_RIGHT_WIDTH, Config.PANEL_RIGHT_HEIGHT - 48);
        
        JPanel appInfo = new JPanel();
        appInfo.setLayout(null);
        appInfo.setOpaque(false);
        appInfo.setBounds(0, 0, Config.PANEL_RIGHT_WIDTH, Config.PANEL_RIGHT_HEIGHT - 48);
        
        mRightIconLabel = new JLabel();
        mRightIconLabel.setBounds(6, 4, 32, 32);
        mRightIconLabel.setOpaque(false);
        appInfo.add(mRightIconLabel);
        
        mRightAppName = new ThemeLabel(40, 4, 150, 32);
        appInfo.add(mRightAppName);
        
        mRightAppVersion = new ThemeLabel(6, 36, 150, 24);
        appInfo.add(mRightAppVersion);
        
        mRightAppInstallDate = new ThemeLabel(6, 60, 150, 24);
        appInfo.add(mRightAppInstallDate);
        
        mBtnUninstall = getButton("卸载");
        mBtnUninstall.setBounds(6, 100, 80, 24);
        appInfo.add(mBtnUninstall);
        
        mBtnGetApk = getButton("取出");
        mBtnGetApk.setBounds(90, 100, 80, 24);
        appInfo.add(mBtnGetApk);
        
        mBtnGetMore = getButton("获取更多信息");
        mBtnGetMore.setBounds(6, 132, 164, 24);
        appInfo.add(mBtnGetMore);
        
        mRightPanel.add(mRightCardPanel);
        mRightCardPanel.add("appinfo", appInfo);
        
        JPanel tipPanel = new JPanel();
        tipPanel.setLayout(null);
        tipPanel.setOpaque(false);
        tipPanel.setBounds(0, 0, Config.PANEL_RIGHT_WIDTH, Config.PANEL_RIGHT_HEIGHT - 48);
        
        mTipLabel = new ThemeLabel("请选择条目之后再进行操作！");
        mTipLabel.setBounds(6, 4, 150, 32);
        tipPanel.add(mTipLabel);
        mRightCardPanel.add("tipinfo", tipPanel);
        
        mRightCardLayout.show(mRightCardPanel, "tipinfo");
    }
	
	@Override
    protected void addLeft() {
        super.addLeft();
        
        JLabel appTitle = new JLabel();
        appTitle.setIcon(Utils.getResImage("res/app_title.png"));
        appTitle.setBounds(0, 4, Config.PANEL_LEFT_WIDTH, 24);
        appTitle.setOpaque(true);
        mLeftPanel.add(appTitle);
        
        mBtnSysApp = getButton("系统程序");
        mBtnSysApp.setBounds(0, 30, Config.PANEL_LEFT_WIDTH, 32);
        mLeftPanel.add(mBtnSysApp);
        
        mBtnUserApp = getButton("用户程序");
        mBtnUserApp.setBounds(0, 61, Config.PANEL_LEFT_WIDTH, 32);
        mLeftPanel.add(mBtnUserApp);
        
        ////////////////////////////////////////////////////////////
        JLabel mediaTitle = new JLabel();
        mediaTitle.setIcon(Utils.getResImage("res/media_title.png"));
        mediaTitle.setBounds(0, 126, Config.PANEL_LEFT_WIDTH, 24);
        mediaTitle.setOpaque(true);
        mLeftPanel.add(mediaTitle);
        
        mBtnMusic = getButton("手机音乐");
        mBtnMusic.setBounds(0, 150, Config.PANEL_LEFT_WIDTH, 32);
        mLeftPanel.add(mBtnMusic);
        
        mBtnPicture = getButton("手机图片");
        mBtnPicture.setBounds(0, 181, Config.PANEL_LEFT_WIDTH, 32);
        mLeftPanel.add(mBtnPicture);
        
        mBtnRingTone = getButton("手机铃声");
        mBtnRingTone.setBounds(0, 212, Config.PANEL_LEFT_WIDTH, 32);
        mLeftPanel.add(mBtnRingTone);
        
        
        ////////////////////////////////////////////////////////////
        JLabel contactTitle = new JLabel();
        contactTitle.setIcon(Utils.getResImage("res/contact_title.png"));
        contactTitle.setBounds(0, 260, Config.PANEL_LEFT_WIDTH, 24);
        contactTitle.setOpaque(true);
        mLeftPanel.add(contactTitle);
        
    }

    @Override
    protected void setTabPageName() {
        mTabPageName = "app-mgr";
    }

    private void updateRight() {
    	if ( mDeviceAppsModel.getRowCount() == 0 ) {
			return;
		}
         
    	mRightCardLayout.show(mRightCardPanel, "appinfo");
    	
    	String pkgName = (String) mDeviceAppsModel.getValueAt(mCurSel, 2);
    	AppInfo app = mAppInfos.get(pkgName);
    	
        ImageIcon icon = new ImageIcon(app.icon);
        mRightIconLabel.setIcon(icon);

        mRightAppName.setText((String) mDeviceAppsModel.getValueAt(mCurSel, 1));
        mRightAppVersion.setText(String.format("当前版本: %s", app.versionName));
        mRightAppInstallDate.setText(String.format("安装日期: %s", app.installDate));
    }
    
    @SuppressWarnings("unchecked")
    protected void updateAppTable(AppInfo[] apps) {
        System.out.println("updateAppTable");

        mAppInfos = new HashMap<String, AppInfo>(apps.length);
        mDeviceAppsModel.getDataVector().clear();
        
        if ( apps == null ) {
            System.out.println("apps == null");
            mDeviceApps.invalidate();
            return;
        }
        
        System.out.println("mIsSysApps:" + mIsSysApps);

        for (AppInfo appInfo : apps) {
            Vector rowData = new Vector();

            rowData.add(appInfo.icon);
            rowData.add(appInfo.name);
            
            //rowData.add(appInfo.isSystem);
            //rowData.add(appInfo.isCtsPass);

            rowData.add(appInfo.pkgName);
            rowData.add(appInfo.pkgPath);

            rowData.add(appInfo.isRunning);
            rowData.add(appInfo.uid);

            //System.out.println("pkgPath:" + appInfo.pkgPath);

            if ( appInfo.isSystem && mIsSysApps ) {
                mDeviceAppsModel.addRow(rowData);
            }
            
            if ( !appInfo.isSystem  && !mIsSysApps ) {
                mDeviceAppsModel.addRow(rowData);
            }
            
            mAppInfos.put(appInfo.pkgName, appInfo);
        }

        mIsLastOK = true;
        mDeviceApps.invalidate();
    }
    
    @SuppressWarnings("serial")
	public static class AppListModel extends DefaultTableModel {

		@Override
		public boolean isCellEditable(int row, int column) {
			if ( column == 0 ) {
				return false;
			}
			return super.isCellEditable(row, column);
		}
    }

    @Override
    public void deviceConnected(SmartToolsApp app) {
        if ( mCurDevice == null ) {
            mIsLastOK = false;
        }
        
        super.deviceConnected(app);
        
        if ( !mIsDeviceChanged && mIsLastOK ) {
            return;
        } else {
            mIsLastOK = false;
        }
        
        updateAppTable(app.getApps());
    }
    
    @Override
    public void deviceDisconnected(SmartToolsApp app) {
        super.deviceDisconnected(app);
        System.out.println("PageAppMgr: deviceDisconnected");
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if ( e.getSource().equals(mBtnSysApp)) {
            mIsSysApps = true;
            updateAppTable(SmartToolsApp.getApp().getApps());
        } else if ( e.getSource().equals(mBtnUserApp)) {
            mIsSysApps = false;
            updateAppTable(SmartToolsApp.getApp().getApps());
        } else if ( e.getSource().equals(mBtnGetApk)) {
            pullFile();
        } else if ( e.getSource().equals(mBtnUninstall)) {
            //uninstall();
        }
    }
    
    private void pullFile() {
        String pkgName = (String) mDeviceAppsModel.getValueAt(mCurSel, 2);
        AppInfo app = mAppInfos.get(pkgName);
        String apkName =app.name + ".apk";
        String remote = app.pkgPath;
        
        System.out.println("pullFile: pkgName=" + pkgName + ", size=" + app.size);
        
        FileSyncHelper syncHelper = new FileSyncHelper(mCurDevice,
                remote, apkName, app.size);
        syncHelper.syncOut();
    }

    protected JButton getButton(String text) {
        JButton btn = new JButton(text);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setFocusable(false);
        btn.setForeground(Color.WHITE);
        btn.addMouseListener(this);
        return btn;
    }
    
    protected void showSysApps() {
        mIsSysApps = true;
        updateAppTable(SmartToolsApp.getApp().getApps());
    }
    
    protected void showUserApps() {
        mIsSysApps = false;
        updateAppTable(SmartToolsApp.getApp().getApps());
    }

}
