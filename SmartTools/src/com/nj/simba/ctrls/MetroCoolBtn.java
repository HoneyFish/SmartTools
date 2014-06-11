package com.nj.simba.ctrls;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.nj.simba.utils.Utils;

@SuppressWarnings("serial")
public class MetroCoolBtn extends JButton {
	
    public MetroCoolBtn(String text, String bgImg, String icon, int x, int y, int w, int h) {
        ImageIcon bgImgIcon = Utils.getResImage(bgImg);
        
        if ( w == 0 ) {
            w = bgImgIcon.getIconWidth();
        }
        
        if ( h == 0 ) {
            h = bgImgIcon.getIconHeight();
        }
                
        setBounds(x, y, w, h);
        setIcon(bgImgIcon);
        
        setOpaque(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        
        setLayout(new BorderLayout());
        
        if ( text != null ) {
            //setText(text);
            JLabel textLabel = new JLabel(text);
            textLabel.setBounds(0, 0, getWidth(), getHeight());
            textLabel.setHorizontalAlignment(SwingConstants.LEFT);
            textLabel.setForeground(Color.WHITE);
            textLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            add(BorderLayout.SOUTH, textLabel);
        }
        
        ImageIcon iconImage = Utils.getResImage(icon);
        JLabel iconLabel = new JLabel(iconImage);
        iconLabel.setBounds(0, 0, iconImage.getIconWidth(), iconImage.getIconHeight());
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(BorderLayout.CENTER, iconLabel);
    }
    
    public MetroCoolBtn(String text, String icon, int x, int y, int w, int h) {
        setBounds(x, y, w, h);
        
        setOpaque(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
        
        setLayout(new BorderLayout());
        
        if ( text != null ) {
            //setText(text);
            JLabel textLabel = new JLabel(text);
            textLabel.setBounds(0, 0, getWidth(), getHeight());
            textLabel.setHorizontalAlignment(SwingConstants.LEFT);
            textLabel.setForeground(Color.WHITE);
            textLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
            add(BorderLayout.SOUTH, textLabel);
        }
        
        ImageIcon iconImage = Utils.getResImage(icon);
        JLabel iconLabel = new JLabel(iconImage);
        iconLabel.setBounds(0, 0, iconImage.getIconWidth(), iconImage.getIconHeight());
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(BorderLayout.CENTER, iconLabel);
    }
    
}
