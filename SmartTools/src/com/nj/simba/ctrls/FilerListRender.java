package com.nj.simba.ctrls;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.android.ddmlib.FileListingService.FileEntry;
import com.nj.simba.utils.Utils;

@SuppressWarnings("serial")
public class FilerListRender extends JLabel implements ListCellRenderer, MouseListener {
 
    private ImageIcon icon;

    public FilerListRender() {
        setForeground(Color.WHITE);
        setVerticalTextPosition(JLabel.CENTER);  
        setHorizontalTextPosition(JLabel.RIGHT);
        setIcon(icon);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        FileEntry entry = (FileEntry) value;
        
        setOpaque(isSelected);
        
        if(isSelected) {
            setForeground(Color.BLACK);
        } else {
            setForeground(Color.WHITE);
        }

        if ( entry != null ) {
            if ( index == 0 ) {
                setText(null);
                setIcon(Utils.getResImage("res/to-up.png"));
            } else {
                setText(entry.getName());
                setIcon(Utils.getResImage("res/folder-mid.png"));
            }
        } else {
            setText(" / ");
            setIcon(Utils.getResImage("res/red.png"));
        }
        
        return this;
    }
}
