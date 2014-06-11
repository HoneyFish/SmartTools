package com.nj.simba.page.filer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class FileTableRender extends DefaultTableCellRenderer {

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
        
        if (column == 0) {
            setText(null);
            setIcon((Icon) value);
        } else {
            setText(value==null?"":value.toString());
            setIcon(null);
        }

        return this;
    }
}
