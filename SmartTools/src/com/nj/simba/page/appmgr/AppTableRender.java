package com.nj.simba.page.appmgr;

import java.awt.Color;
import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class AppTableRender extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        
        setOpaque(isSelected);
        
        if(isSelected) {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.WHITE);
        }
        
        /** cts is fail! **/
//        if ( column == 3 && !(Boolean) value) {
//            setBackground(Color.RED);
//            setForeground(Color.BLACK);
//            setOpaque(true);
//        }
        
        if (column == 0) {
            setText(null);
            setIcon(new ImageIcon((byte[])value));
        } else {
            setText(value.toString());
            setIcon(null);
        }

        return this;
    }
}
