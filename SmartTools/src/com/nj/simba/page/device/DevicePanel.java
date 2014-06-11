package com.nj.simba.page.device;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.Timer;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import com.android.ddmlib.IDevice;
import com.nj.simba.app.SmartToolsApp;
import com.nj.simba.IDeviceInfo;
import com.nj.simba.IDeviceInfo.DeviceFeature;
import com.nj.simba.ctrls.ImageBtn;
import com.nj.simba.ctrls.LeftPanel;
import com.nj.simba.utils.Config;
import com.nj.simba.utils.Utils;

public class DevicePanel extends LeftPanel {
	private JLabel mDeviceInfo;
	private JLabel mDeviceScreenshot;
	private IDevice mCurDevice;
	private boolean mIsPlaying;
	private Timer mPlayTimer;

	public DevicePanel(JPanel parent, int x, int y, int w, int h) {
		super(parent, x, y, w, h);
	}

	public DevicePanel(JPanel parent) {
		super(parent, 0, 0,
				Config.PANEL_LEFT_WIDTH, Config.WIN_PANEL_H);
	}

	@Override
    public void createPanel() {
		super.createPanel();
		
		ImageIcon deviceOffImg = Utils.getResImage("res/device-off.png");
		JLabel device = new JLabel(deviceOffImg);
		device.setBounds(6, 16, 230,320);
		device.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		mParentPanel.add(device);
		mDeviceScreenshot = device;

		JButton btn1 = new ImageBtn("res/capture.png", 6, 340, 56, 30);
		btn1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveScreenshot();
			}
		});

		JButton btn2 = new ImageBtn("res/refresh.png", 64, 340, 56, 30);
		btn2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateScreenshot(mDeviceScreenshot);
			}
		});

		JButton btn3 = new ImageBtn("res/fullscr.png", 122, 340, 56, 30);
		btn3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fullScreenScreenshot();
			}
		});

		JButton btn4 = new ImageBtn("res/menu.png", 180, 340, 56, 30);
		btn4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JPopupMenu popup = new JPopupMenu("Select Device");
				popup.add("N986");
				popup.add("U5S");
				popup.show((Component) e.getSource(), 0, 30);
			}
		});

		mParentPanel.add(btn1);
		mParentPanel.add(btn2);
		mParentPanel.add(btn3);
		mParentPanel.add(btn4);

		mDeviceInfo = new JLabel();
		mDeviceInfo.setBounds(6, 380, 230, 200);
		mDeviceInfo.setForeground(Config.default_font_color);
		mDeviceInfo.setVerticalAlignment(JLabel.TOP);
		mDeviceInfo.setOpaque(false);
		mParentPanel.add(mDeviceInfo);
	}

	void updateScreenshot(final JLabel screenLabel) {
	    System.out.println("updateScreenshot");
	    
		new Thread(new Runnable() {
			@Override
			public void run() {
				final ImageIcon screenShot = getScreenshot(mCurDevice,
						screenLabel.getWidth(), screenLabel.getHeight()-8);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						screenLabel.setIcon(screenShot);
					}
				});
			}
		}).start();
	}

	@SuppressWarnings("serial")
	void fullScreenScreenshot() {
		if (new File(Config.sreenshot).exists() == false) {
			return;
		}

		final JFrame fullScreen = new JFrame();
		fullScreen.setUndecorated(true);
		fullScreen.setBackground(Color.BLACK);

		JPanel contentPanel = (JPanel) fullScreen.getContentPane();
		contentPanel.setLayout(null);
		contentPanel.setOpaque(false);

		/**
		 * 1. full screen panel
		 */
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Rectangle bounds = new Rectangle(screenSize);
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(
				fullScreen.getGraphicsConfiguration());

		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		fullScreen.setBounds(bounds);

		/**
		 * 2. create device screenshot image label
		 */
		final JLabel screenLabel = new JLabel();

		ImageIcon deviceScreenImg = new ImageIcon(Config.sreenshot);
		int iconW = deviceScreenImg.getIconWidth();
		int iconH = deviceScreenImg.getIconHeight();
		int[] props = Utils.getProperSize(iconW, iconH, bounds.width,
				bounds.height);
		int imageX = (bounds.width - props[0]) >> 1;

		screenLabel.setBounds(imageX, 0, props[0], props[1]);
		screenLabel.setIcon(Utils.getProperRatioImage(deviceScreenImg,
				props[0], props[1]));
		contentPanel.add(screenLabel);

		/**
		 * 3. create exit button
		 */
		JButton btnExit = new ImageBtn("res/close.png", bounds.width - 120, 16,
				100, 32);
		btnExit.setOpaque(false);
		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fullScreen.dispose();
				mPlayTimer.stop();
			}
		});
		contentPanel.add(btnExit);

		/**
		 * 4. create play/pause button
		 */
		ImageBtn btnPlay = new ImageBtn("res/play.png", bounds.width - 120,
				120, 100, 32);
		btnPlay.setOpaque(false);
		btnPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (mPlayTimer == null) {
					return;
				}

				ImageBtn btnPlay = (ImageBtn) e.getSource();
				if (mIsPlaying) {
					btnPlay.setIcon("res/pause.png");
					mPlayTimer.stop();
				} else {
					btnPlay.setIcon("res/play.png");
					mPlayTimer.start();
				}

				mIsPlaying = !mIsPlaying;
			}
		});
		contentPanel.add(btnPlay);

		/**
		 * 4. create rotate button
		 */
		JButton btnRotateLeft = new ImageBtn("res/rotate1.png",
				bounds.width - 120, 168, 100, 32);
		btnRotateLeft.setOpaque(false);
		btnRotateLeft.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateScreenshot(screenLabel);
			}
		});
		contentPanel.add(btnRotateLeft);

		JButton btnRotateRight = new ImageBtn("res/rotate2.png",
				bounds.width - 120, 216, 100, 32);
		btnRotateRight.setOpaque(false);
		btnRotateRight.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateScreenshot(screenLabel);
			}
		});
		contentPanel.add(btnRotateRight);

		/**
		 * 5. show panel
		 */
		// fullScreen.pack();
		fullScreen.setVisible(true);
		fullScreen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		/**
		 * 6. timer to update screenshot
		 */
		if (mPlayTimer != null && mPlayTimer.isRunning()) {
			mPlayTimer.stop();
		} else {
			mPlayTimer = new Timer(2000, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateScreenshot(screenLabel);
				}
			});
		}

		mPlayTimer.start();
		mIsPlaying = true;
	}

	void saveScreenshot() {
		File source = new File(Config.sreenshot);
		if (source.exists() == false) {
			return;
		}

		JFileChooser chooser = new JFileChooser("./");
		chooser.addChoosableFileFilter(new ImageFileFilter());

		// cancel or error, just return
		if (chooser.showSaveDialog(mParentPanel) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File file = chooser.getSelectedFile();

		try {
			if (file.exists() == false) {
				file.createNewFile();
			}

			byte[] b = new byte[(int) source.length()];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(Config.sreenshot));
			in.read(b);
			in.close();

			BufferedOutputStream out = new BufferedOutputStream(
					new FileOutputStream(file));
			out.write(b);
			out.flush();
			out.close();

		} catch (IOException ex) {
			// TODO: handle exception
		}
	}

	public static class ImageFileFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			return f.getName().toLowerCase().endsWith("png");
		}

		@Override
		public String getDescription() {
			return "*.png(image file)";
		}

	}

	public ImageIcon getScreenshot(IDevice device, int dstW, int dstH) {
		if (device == null) {
			return Utils.getResImage("res/device-off.png");
		}

		return Utils.getProperRatioImage(Utils.getScreenshot(device), dstW,
				dstH);
	}

	/**
	 * "<html>Device: %1$s<br/>
	 * Version: %2$s<br/>
	 * Date: %3$s<br/>
	 * Finger: %4$s<br/>
	 * ";
	 */
	public void updateDeviceInfo(SmartToolsApp app) {
	    IDeviceInfo deviceInfo = app.getDeviceInfo();
	    if ( deviceInfo == null ) {
	        return;
	    }
	    
	    DeviceFeature feature = deviceInfo.mFeatures;
	    if ( feature == null ) {
	        return;
	    }
	    
	    mCurDevice = app.getCurDevice();
	    
	    int w = feature.lcdWidth;
	    int h = feature.lcdHeight;
	    float dpi = feature.lcdDensity;
	    double size = Math.sqrt(w*w+h*h)/(160*dpi);
	    
	    String androidName = Utils.getAndroidName(feature.androidVersion);
	    String deviceName = feature.deviceName;
	    String imageVersion = feature.imageVersion;
	    
		final String display = String.format(Config.device_info_format3, w,
				h, dpi, size, androidName, deviceName, imageVersion);
		
		System.out.println(display);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mDeviceInfo.setText(display);
			}
		});

		updateScreenshot(mDeviceScreenshot);
	}
}
