package com.nj.simba.app;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.nj.simba.ctrls.SubPanel;
import com.nj.simba.ctrls.ToolbarBtn;

public class ToolbarPanel extends SubPanel {
    private JButton mToolbarBtnTest = null;
    private JButton mToolbarBtnApp = null;
    private JButton mToolbarBtnFiler = null;
    private JButton mToolbarBtnLog = null;
    
    public ToolbarPanel(JPanel parent) {
        super(parent);
    }
    
    @Override
    protected void createPanel() {
        /* not need call super createPanel*/
        //super.createPanel();

        mToolbarBtnTest = new ToolbarBtn("我的手机", 6, 35, 100, 50);
        mToolbarBtnApp = new ToolbarBtn("应用管理", 110, 35, 100, 50);
        mToolbarBtnFiler = new ToolbarBtn("文件管理", 214, 35, 100, 50);
        mToolbarBtnLog = new ToolbarBtn("手机日志", 318, 35, 100, 50);

        add(mToolbarBtnTest);
        add(mToolbarBtnApp);
        add(mToolbarBtnFiler);
        add(mToolbarBtnLog);
        
        final MainFrame frm = SmartToolsApp.getApp().getMainFrm();

        mToolbarBtnTest.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frm.showPanel(MainFrame.PANEL_MYDEVICE);
            }
        });

        mToolbarBtnApp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frm.showPanel(MainFrame.PANEL_APPMGR);
            }
        });
        
        mToolbarBtnLog.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frm.showPanel(MainFrame.PANEL_LOGCAT);
            }
        });
        
        mToolbarBtnFiler.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frm.showPanel(MainFrame.PANEL_FILER);
            }
        });
        
    }

}
