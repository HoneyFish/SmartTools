package com.nj.simba.page.device;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.nj.simba.utils.Utils;

public class DeviceListRender extends JLabel implements ListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList list,
            Object value, int index, boolean isSelected, boolean cellHasFocus) {
        
        setOpaque(isSelected);
        setIcon(Utils.getResImage("res/device.png"));
        
        if(isSelected) {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.WHITE);
        }
        
        setText(value==null?"":value.toString());
        return this;
    }

}
