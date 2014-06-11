package com.nj.simba.ctrls;

import java.awt.Color;
import java.beans.ConstructorProperties;

import javax.swing.Icon;
import javax.swing.JButton;

public class ThemeButton extends JButton {
    public ThemeButton() {
        super(null, null);
    }
    
    /**
     * Creates a button with an icon.
     *
     * @param icon  the Icon image to display on the button
     */
    public ThemeButton(Icon icon) {
        super(null, icon);
    }

    /**
     * Creates a button with text.
     *
     * @param text  the text of the button
     */
    @ConstructorProperties({"text"})
    public ThemeButton(String text) {
        super(text, null);
        setOpaque(false);
        setContentAreaFilled(false);
        setForeground(Color.WHITE);
    }
}
