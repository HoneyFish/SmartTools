package com.nj.simba.page.filer;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import com.android.ddmlib.FileListingService;
import com.android.ddmlib.FileListingService.FileEntry;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.ctrls.FilerListRender;
import com.nj.simba.ctrls.TabPanel;
import com.nj.simba.utils.Config;
import com.nj.simba.utils.FileSyncHelper;
import com.nj.simba.utils.Utils;


public class PageFiler extends TabPanel {
    private DefaultListModel mListModel;
    private DefaultTableModel mCurDirFilesModel;
    private JList mFileList;
    private FileListingService mFileSvr;
    private JTable mCurDirFiles;
    private FileEntry mCurEntry;
    private ImageIcon mIconFolder;
    private ImageIcon mIconFile;
    private JButton mBtnGetOut;
    private int mCurSel = -1;

    public PageFiler(JPanel tabPanel, int x, int y, int w, int h) {
        super(tabPanel, x, y, w, h);
    }

    public PageFiler(JPanel tabPanel) {
        super(tabPanel);
    }

    @Override
    protected void setTabPageName() {
        mTabPageName = "filer";
    }

    @Override
    protected void addBody() {
        super.addBody();

        mIconFolder = Utils.getResImage("res/folder-mid.png");
        mIconFile = Utils.getResImage("res/file-mid.png");

        mCurDirFilesModel = new FileListModel();
        mCurDirFiles = getFileList(mCurDirFilesModel);

        JScrollPane scroll = new JScrollPane(mCurDirFiles);
        scroll.setBounds(8, 4, Config.PANEL_BODY_WIDTH - 8 * 2,
                Config.PANEL_LEFT_HEIGHT - 4 * 2);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JButton btnUp = new JButton("..");
        scroll.add(btnUp);

        mBodyPanel.add(scroll);
    }

    @Override
    protected void addLeft() {
        super.addLeft();

        mListModel = new DefaultListModel();
        mFileList = new JList(mListModel);
        mFileList.setBounds(6, 16, Config.PANEL_LEFT_WIDTH - 6 * 2,
                Config.PANEL_LEFT_HEIGHT);
        mFileList.setOpaque(false);
        mFileList.setCellRenderer(new FilerListRender());
        mFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mFileList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = mFileList.getSelectedIndex();

                if (index < 0) {
                    return;
                }

                if (e.getClickCount() >= 2) {
                    mCurEntry = (FileEntry) mListModel.get(index);

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (mFileSvr != null) {
                                updateFiler();
                            }
                        }
                    });
                }
            }
        });

        JScrollPane scroll = new JScrollPane(mFileList);
        scroll.setBounds(6, 4, Config.PANEL_LEFT_WIDTH - 6 * 2,
                Config.PANEL_LEFT_HEIGHT - 4 * 2);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        mLeftPanel.add(scroll);
    }

    @Override
    protected void addRight() {
        super.addRight();

        JLabel rightTitle = new JLabel("我的收藏");
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setBounds(4, 0, Config.PANEL_RIGHT_WIDTH, 32);
        rightTitle.setOpaque(false);
        mRightPanel.add(rightTitle);

        rightTitle = new JLabel("外置存储卡");
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setBounds(4, 30, Config.PANEL_RIGHT_WIDTH, 32);
        rightTitle.setOpaque(false);
        mRightPanel.add(rightTitle);

        rightTitle = new JLabel("内置存储卡");
        rightTitle.setForeground(Color.WHITE);
        rightTitle.setBounds(4, 60, Config.PANEL_RIGHT_WIDTH, 32);
        rightTitle.setOpaque(false);
        mRightPanel.add(rightTitle);

        mBtnGetOut = getButton("取出文件");
        mBtnGetOut.setBounds(4, 100, 100, 24);
        mRightPanel.add(mBtnGetOut);
        mBtnGetOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mCurSel < 0) {
                    return;
                }

                String child = (String) mCurDirFiles.getValueAt(mCurSel, 1);
                FileEntry curSel = mCurEntry.findChild(child);
                FileSyncHelper syncHelper = new FileSyncHelper(mCurDevice,
                        curSel);
                syncHelper.syncOut();
            }
        });
    }

    @Override
    public void deviceConnected(SmartToolsApp app) {
        super.deviceConnected(app);

        if (mCurDevice == null) {
            return;
        }

        mFileSvr = mCurDevice.getFileListingService();
        mCurEntry = mFileSvr.getRoot();
        updateFiler();
    }

    @Override
    public void deviceDisconnected(SmartToolsApp app) {
        super.deviceDisconnected(app);
        System.out.println("PageFiler: deviceDisconnected");
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

    private JTable getFileList(DefaultTableModel model) {
        String columns[] = { "Icon", "Name", "Size", "Date", "Time",
                "Permissions", "info" };
        model.setColumnIdentifiers(columns);

        JTable listFiles = new JTable(model);
        listFiles.setBounds(0, 0, Config.PANEL_BODY_WIDTH - 6 * 2,
                Config.PANEL_LEFT_HEIGHT - 4 * 2);
        listFiles.setDefaultRenderer(Object.class, new FileTableRender());
        listFiles.setOpaque(false);
        listFiles.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        listFiles.setRowHeight(36);

        TableColumnModel columnMode = listFiles.getColumnModel();
        columnMode.getColumn(0).setPreferredWidth(48);
        columnMode.getColumn(1).setPreferredWidth(120);
        columnMode.getColumn(2).setPreferredWidth(64);
        columnMode.getColumn(3).setPreferredWidth(100);
        columnMode.getColumn(4).setPreferredWidth(80);
        columnMode.getColumn(5).setPreferredWidth(120);

        listFiles.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    mCurSel = mCurDirFiles.getSelectedRow();
                }
            }
        });

        return listFiles;
    }

    private void updateFiler() {
        FileEntry[] entries = mFileSvr.getChildren(mCurEntry, true, null);

        mListModel.clear();
        ((DefaultTableModel) mCurDirFilesModel).getDataVector().clear();

        mListModel.addElement(mCurEntry.getParent());

        for (FileEntry fileEntry : entries) {
            Object[] details = new Object[7];

            if (fileEntry.isDirectory()) {
                mListModel.addElement(fileEntry);
                details[0] = mIconFolder;
            } else {
                details[0] = mIconFile;
            }

            details[1] = fileEntry.getName();
            details[2] = fileEntry.getSize();
            details[3] = fileEntry.getDate();
            details[4] = fileEntry.getTime();
            details[5] = fileEntry.getPermissions();
            details[6] = fileEntry.getInfo();
            mCurDirFilesModel.addRow(details);

            // System.out.println(details[1]);
        }
    }

    @SuppressWarnings("serial")
    public static class FileListModel extends DefaultTableModel {

        @Override
        public boolean isCellEditable(int row, int column) {
            if (column == 0) {
                return false;
            }
            return super.isCellEditable(row, column);
        }
    }

}
