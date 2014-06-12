package com.nj.simba.page.logcat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import com.android.ddmlib.IDevice;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.ctrls.TabPanel;
import com.nj.simba.utils.Config;

public class PageLogcat extends TabPanel {
	private RTextScrollPane mScrollPane;
	private RSyntaxTextArea mTextArea;
	private JComboBox mLogType;
	private JComboBox mLogLevel;
	private JTextArea mKeyWords;
	private JComboBox mLogFormat;
	private JTextArea mLogRequest;
	private JCheckBox mSaveCheck;
	private JTable mExceptionTable;
	private DefaultTableModel mExceptionMode;
    private String mLogDir;
    private LogCatReceiver mLogMainReceiver;
    private LogCatReceiver mLogSystemReceiver;
    private LogCatReceiver mLogEventsReceiver;
    private LogCatReceiver mLogRadioReceiver;
	private BufferedWriter mLogMainFile;
	private BufferedWriter mLogSystemFile;
	private BufferedWriter mLogEventsFile;
	private BufferedWriter mLogRadioFile;
	
	public PageLogcat(JPanel parent, JPanel tabPanel, int x, int y, int w,
            int h) {
        super(tabPanel, x, y, w, h);
    }
	
	public PageLogcat(JPanel tabPanel) {
		super(tabPanel);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void setTabPageName() {
		mTabPageName = "logcat";
	}

	@Override
	protected void addBody() {
	    super.addBody();
	    
		mTextArea = createTextArea();
		mTextArea.setSyntaxEditingStyle(RSyntaxTextArea.SYNTAX_STYLE_PERL);
		mTextArea.setBackground(Color.LIGHT_GRAY);
		//textArea.setForeground(Color.WHITE);
		mScrollPane = new RTextScrollPane(mTextArea, true);
		mScrollPane.setBounds(1, 1, mContentBody.getWidth()-1, Config.WIN_PANEL_H-1);
		
		mBodyPanel.add(mScrollPane);
	}

	@Override
	protected void addRight() {
	    super.addRight();
	    
		JLabel title = new JLabel();
        title.setBounds(4, 4, 100, 32);
        title.setText("<html><h2 style=\"color:ffffff;text-decoration:underline\">Settings</h2></html>");
        title.setOpaque(false);
        mRightPanel.add(title);
        
        JButton apply = new JButton("Apply");
        apply.setBounds(140, 12, 80, 24);
        apply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//resetAdbLogcat();
			}
		});
        mRightPanel.add(apply);
        
        ///////////////////////////////////////////
        title = new JLabel("Log Type:");
        title.setBounds(4, 64, 70, 24);
        title.setForeground(Color.WHITE);
        mRightPanel.add(title);
        
        String[] types = { "main", "radio", "events"};
        mLogType = new JComboBox(types); 
        mLogType.setSelectedIndex(0);
        mLogType.setBounds(72, 64, 150, 24);
        //mLogType.addActionListener(this);
        mRightPanel.add(mLogType);
        
        ///////////////////////////////////////////
        title = new JLabel("Log Level:");
        title.setBounds(4, 92, 70, 24);
        title.setForeground(Color.WHITE);
        mRightPanel.add(title);
        
        String[] levels = { "I", "V", "D", "W", "E", "F"};
        mLogLevel = new JComboBox(levels); 
        mLogLevel.setSelectedIndex(0);
        mLogLevel.setBounds(72, 92, 150, 24);
        mRightPanel.add(mLogLevel);
        
        ///////////////////////////////////////////
        title = new JLabel("KeyWords:");
        title.setBounds(4, 120, 70, 24);
        title.setForeground(Color.WHITE);
        mRightPanel.add(title);
        
        mKeyWords = new JTextArea(); 
        mKeyWords.setLineWrap(true);
        mKeyWords.setBounds(72, 120, 150, 48);
        mRightPanel.add(mKeyWords);
        
		///////////////////////////////////////////
		title = new JLabel("Log Format:");
		title.setBounds(4, 172, 70, 24);
		title.setForeground(Color.WHITE);
		mRightPanel.add(title);
		
		String[] formats = { "brief", "prcess", "tag", "thread", "raw", "time", "long", };
		mLogFormat = new JComboBox(formats); 
		mLogFormat.setSelectedIndex(5);
		mLogFormat.setBounds(72, 172, 150, 24);
		mRightPanel.add(mLogFormat);
		
		///////////////////////////////////////////
		title = new JLabel("Command:");
		title.setBounds(4, 200, 70, 24);
		title.setForeground(Color.WHITE);
		mRightPanel.add(title);
		
		mLogRequest = new JTextArea(); 
		mLogRequest.setLineWrap(true);
		mLogRequest.setBounds(72, 200, 150, 48);
		mLogRequest.setText("adb logcat -v time Emode:D *:S");
		mLogRequest.setLineWrap(true);
        mRightPanel.add(mLogRequest);
        
		///////////////////////////////////////////
		title = new JLabel("Save Log?");
		title.setBounds(4, 252, 70, 24);
		title.setForeground(Color.WHITE);
		mRightPanel.add(title);
		
		mSaveCheck = new JCheckBox("Save"); 
		mSaveCheck.setBounds(72, 252, 150, 24);
		mSaveCheck.setForeground(Color.WHITE);
		mSaveCheck.setOpaque(false);
		mRightPanel.add(mSaveCheck);
		
		///////////////////////////////////////////
		title = new JLabel("Exceptions:");
		title.setBounds(4, 280, 70, 24);
		title.setForeground(Color.WHITE);
		mRightPanel.add(title);
		
		String columns[] = { "line", "module", "detail"};
		mExceptionMode = new DefaultTableModel();
		mExceptionMode.setColumnIdentifiers(columns);
		
		mExceptionTable = new JTable(mExceptionMode); 
		mExceptionTable.setBounds(4, 308, 224, 124);
		mExceptionTable.setForeground(Color.WHITE);
		mExceptionTable.setOpaque(false);
		mRightPanel.add(mExceptionTable);
		mExceptionTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
		mExceptionTable.setRowHeight(36);
		TableColumn column0 = mExceptionTable.getColumnModel().getColumn(0);
        column0.setPreferredWidth(32);
        TableColumn column1 = mExceptionTable.getColumnModel().getColumn(2);
        column1.setPreferredWidth(160);
		
		JScrollPane scroll = new JScrollPane(mExceptionTable);
        scroll.setBounds(4, 308, 220, 124);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
		mRightPanel.add(scroll);
	}
	
	@Override
	protected void addLeft() {
	    mContentLeft = null;
	    mLeftPanel = null;
	}
	
	@Override
	public void onAppExit() {
		closeLogFiles();
	}

	/**
	 * Creates the text area for this application.
	 *
	 * @return The text area.
	 */
	private RSyntaxTextArea createTextArea() {
		RSyntaxTextArea textArea = new RSyntaxTextArea(25, 70);
		textArea.setTabSize(2);
		textArea.setCaretPosition(0);
		//textArea.addHyperlinkListener(this);
		textArea.requestFocusInWindow();
		//textArea.setMarkOccurrences(true);
		//textArea.setCodeFoldingEnabled(true);
		//textArea.setClearWhitespaceLinesEnabled(false);
		//textArea.setText("int java = 2");
		return textArea;
	}


	/**
	 * Focuses the text area.
	 */
	void focusTextArea() {
		mTextArea.requestFocusInWindow();
	}
	
	@Override
	public void deviceConnected(SmartToolsApp app) {
	    super.deviceConnected(app);
        
        if ( mCurDevice == null || mIsDeviceChanged == false ) {
            return;
        }
	    
        System.out.println("PageLogcat, device is changed:" + mIsDeviceChanged);
        
	    initLogDir();
        addLogListener(app.getCurDevice());
	}
	
	@Override
    public void deviceDisconnected(SmartToolsApp app) {
        super.deviceDisconnected(app);
        System.out.println("PageLogcat: deviceDisconnected");
    }

	private void initLogDir() {
		closeLogFiles();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.sss");
	    mLogDir = sdf.format(new Date());
        File dir = new File(mLogDir);
        
        if ( dir.exists() == false ) {
            dir.mkdir();
        }
        
        mLogDir = dir.getAbsolutePath();
        File fMain = new File(mLogDir + "/main.log");
        File fSystem = new File(mLogDir + "/system.log");
        File fEvents = new File(mLogDir + "/events.log");
        File fRadio = new File(mLogDir + "/radio.log");
        
        try {
			mLogMainFile = new BufferedWriter(new FileWriter(fMain, true));
			mLogSystemFile = new BufferedWriter(new FileWriter(fSystem, true));
			mLogEventsFile = new BufferedWriter(new FileWriter(fEvents, true));
			mLogRadioFile = new BufferedWriter(new FileWriter(fRadio, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void addLogListener(IDevice device) {
        mLogMainReceiver = LogCatReceiverFactory.INSTANCE.newReceiver(device, LogCatReceiver.LOGCAT_MAIN);
        mLogMainReceiver.addMessageReceivedEventListener(new LogListener(LogCatReceiver.LOGCAT_MAIN));
        
        mLogSystemReceiver = LogCatReceiverFactory.INSTANCE.newReceiver(device, LogCatReceiver.LOGCAT_SYSTEM);
        mLogSystemReceiver.addMessageReceivedEventListener(new LogListener(LogCatReceiver.LOGCAT_SYSTEM));
        
        mLogEventsReceiver = LogCatReceiverFactory.INSTANCE.newReceiver(device, LogCatReceiver.LOGCAT_EVENT);
        mLogEventsReceiver.addMessageReceivedEventListener(new LogListener(LogCatReceiver.LOGCAT_EVENT));
        
        mLogRadioReceiver = LogCatReceiverFactory.INSTANCE.newReceiver(device, LogCatReceiver.LOGCAT_RADIO);
        mLogRadioReceiver.addMessageReceivedEventListener(new LogListener(LogCatReceiver.LOGCAT_RADIO));
    }
	
	public class LogListener implements ILogCatBufferChangeListener {
		private int mLogType = LogCatReceiver.LOGCAT_MAIN;
		
		public LogListener(int log) {
			mLogType = log;
		}
		
        @Override
        public void bufferChanged(List<LogCatMessage> addedMessages,
                List<LogCatMessage> deletedMessages) {
        	switch (mLogType) {
			case LogCatReceiver.LOGCAT_MAIN:
				doMainLog(addedMessages,deletedMessages);
				break;
				
			case LogCatReceiver.LOGCAT_SYSTEM:
				doSystemLog(addedMessages, deletedMessages);
				break;
				
			case LogCatReceiver.LOGCAT_EVENT:
				doEventsLog(addedMessages, deletedMessages);
				break;
				
			case LogCatReceiver.LOGCAT_RADIO:
				doRadioLog(addedMessages, deletedMessages);
				break;
				
			default:
				break;
			}
        }

		private void doMainLog(List<LogCatMessage> addedMessages,
				List<LogCatMessage> deletedMessages) {
			
			final StringBuilder b = new StringBuilder();
			
            for (LogCatMessage logCatMessage : addedMessages) {
                b.append(logCatMessage.toString()+"\n");
            }
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                	if ( mTextArea.getLineCount() > 10000 ) {
                		mTextArea.setText("");
                	}
                    mTextArea.append(b.toString());        
                }
            });

        	writeLogFile(LogCatReceiver.LOGCAT_MAIN, addedMessages);
		}
		
		private void doSystemLog(List<LogCatMessage> addedMessages,
				List<LogCatMessage> deletedMessages) {
			writeLogFile(LogCatReceiver.LOGCAT_SYSTEM, addedMessages);
			
		}
		
		private void doEventsLog(List<LogCatMessage> addedMessages,
				List<LogCatMessage> deletedMessages) {
			writeLogFile(LogCatReceiver.LOGCAT_EVENT, addedMessages);
			
		}
		
		private void doRadioLog(List<LogCatMessage> addedMessages,
				List<LogCatMessage> deletedMessages) {
			writeLogFile(LogCatReceiver.LOGCAT_RADIO, addedMessages);
		}
        
    }
	
	private void writeLogFile(int log, List<LogCatMessage> addedMessages) {
    	BufferedWriter logMainFile = null;
    	
    	if ( addedMessages.size() == 0 ) {
            return;
        }
    	
    	switch(log) {
    	case LogCatReceiver.LOGCAT_MAIN:
    		logMainFile = mLogMainFile;
    		break;
    	case LogCatReceiver.LOGCAT_SYSTEM:
    		logMainFile = mLogSystemFile;
    		break;
    	case LogCatReceiver.LOGCAT_EVENT:
    		logMainFile = mLogEventsFile;
    		break;
    	case LogCatReceiver.LOGCAT_RADIO:
    		logMainFile = mLogRadioFile;
    		break;
    	default:
    		logMainFile = mLogMainFile;
    		break;
    	}
    	
    	if ( logMainFile == null ) {
    	    return;
    	}
    	
		try {
            for (LogCatMessage logCatMessage : addedMessages) {
            	logMainFile.write(logCatMessage.toString() + "\n");
            }
            logMainFile.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void closeLogFiles() {
		try {
			if ( mLogMainFile != null ) {
				mLogMainFile.close();
				mLogMainFile = null;
			}
			if ( mLogSystemFile != null ) {
				mLogSystemFile.close();
				mLogSystemFile = null;
			}
			if ( mLogEventsFile != null ) {
				mLogEventsFile.close();
				mLogEventsFile = null;
			}
			if ( mLogRadioFile != null ) {
				mLogRadioFile.close();
				mLogRadioFile = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
