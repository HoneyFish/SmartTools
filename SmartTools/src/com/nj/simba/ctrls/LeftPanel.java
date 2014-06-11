package com.nj.simba.ctrls;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.nj.simba.utils.Config;

public class LeftPanel extends SubPanel {

	public LeftPanel(JPanel parent, int x, int y, int w, int h) {
		super(parent, x, y, w, h);
	}

	public LeftPanel(JPanel parent) {
		super(parent, Config.OFFSET_LEFT, 0,
				Config.PANEL_LEFT_WIDTH, Config.WIN_PANEL_H);
		addBorder();
	}

	@Override
	protected void createPanel() {
		JLabel frame = new JLabel();
		frame.setForeground(Color.WHITE);
		frame.setOpaque(false);
		frame.setBounds(mPosX, mPosY, mWidth, mHeight);
		frame.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		add(frame);
	}
}
