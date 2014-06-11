package com.nj.simba.ctrls;

import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import com.nj.simba.utils.Utils;

@SuppressWarnings("serial")
public class ImageBtn extends JButton {
	public ImageBtn(String icon, int x, int y, int w, int h) {
		setHorizontalTextPosition(JButton.CENTER);

		ImageIcon icon1Img = Utils.getResImage(icon);

		if ( w == 0) {
			w = icon1Img.getIconWidth();
		}
		if ( h == 0) {
			h = icon1Img.getIconHeight();
		}

		setFocusable(false);
		//setBorder(new EmptyBorder(0, 0, 0, 0));
		setText("");
		setBounds(x, y, w, h);
		setIcon(icon1Img);

		setOpaque(false);
		setContentAreaFilled(false);
		setForeground(Color.WHITE);
	}

	public void setIcon(String iconUrl) {
	    java.net.URL icon1Url = getClass().getResource(iconUrl);
        ImageIcon icon1Img = new ImageIcon(icon1Url);
        setIcon(icon1Img);
	}

}
