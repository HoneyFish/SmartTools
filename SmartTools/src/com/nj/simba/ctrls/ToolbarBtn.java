package com.nj.simba.ctrls;

import java.awt.Color;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class ToolbarBtn extends JButton {
    public ToolbarBtn(String text, int x, int y, int w, int h) {
        setText(text);
        setBounds(x, y, w, h);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
    }
}
