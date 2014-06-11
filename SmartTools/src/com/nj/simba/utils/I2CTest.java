package com.nj.simba.utils;

import java.awt.Color;
import java.awt.TextArea;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;




import com.nj.simba.ctrls.TabPanel;

public class I2CTest extends TabPanel {
    private TextArea mTextArea;

    public I2CTest(JPanel parent, JPanel tabPanel, int x, int y, int w,
            int h) {
        super(tabPanel, x, y, w, h);
    }
    
    public I2CTest(JPanel tabPanel) {
        super(tabPanel);
    }

    @Override
    protected void setTabPageName() {
        mTabPageName = "I2C Test";
    }
    
    @Override
    protected void addBody() {
        super.addBody();
        
        mTextArea = new TextArea();
        mTextArea.setBackground(Color.LIGHT_GRAY);
        mTextArea.setBounds(1, 1, mContentBody.getWidth()-1, Config.WIN_PANEL_H-1);
        
        JScrollPane scroll = new JScrollPane(mTextArea);
        scroll.setBounds(8, 4, Config.PANEL_BODY_WIDTH - 8 * 2,
                Config.PANEL_LEFT_HEIGHT - 4 * 2);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        
        mBodyPanel.add(scroll);
    }

}
