package com.nj.simba.ctrls;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JLabel;

public class ThemeLabel extends JLabel {
    public ThemeLabel() {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
    public ThemeLabel(int x, int y, int w, int h) {
        this();
        setOpaque(false);
        setForeground(Color.WHITE);
        setBounds(x, y, w, h);
    }
    
    public ThemeLabel(int x, int y, int w, int h, int horizontalAlignment) {
        this();
        setHorizontalAlignment(horizontalAlignment);
        setOpaque(false);
        setForeground(Color.WHITE);
        setBounds(x, y, w, h);
    }
    
    public ThemeLabel(String text) {
        super(text);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
    public ThemeLabel(Icon image) {
        super(image);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
    public ThemeLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
    
    public ThemeLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
    public ThemeLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    
}
