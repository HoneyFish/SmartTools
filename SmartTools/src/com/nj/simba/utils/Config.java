package com.nj.simba.utils;


import java.awt.Color;

public class Config {
    public static String background = "res/theme1.jpg";
    public static String sreenshot = "./device.png";
    
    public static Color default_font_color = Color.WHITE;
    public static Color default_back_color = Color.BLACK;

    public static String CMD_CHECK_SERVER="pm path com.nj.simba";
    public static String CMD_START_SERVER="am broadcast -a com.nj.simba.action.START_SERVER com.nj.simba/.DaemonController";
    public static String CMD_RELEASE_SERVER="am broadcast -a com.nj.simba.action.RELEASE_SERVER";
    
    public static String device_info_format = 
    		"<html>" +
    		"<h2 style=\"color:99cc00\">Device:</h2><hr/> %1$s" +
    		"<h2 style=\"color:99cc00\">Version:</h2><hr/> %2$s" +
    		"<h2 style=\"color:99cc00\">Date:</h2><hr/> %3$s" +
    		"<h2 style=\"color:99cc00\">Finger:</h2><hr/> %4$s" +
    		"</html>";
    
    public static String device_info_format2 = 
    		"<html>" +
    		"<p style=\"color:99cc00\">Device:</p> %1$s" +
    		"<p style=\"color:99cc00\">Version:</p> %2$s" +
    		"<p style=\"color:99cc00\">Date:</p> %3$s" +
    		"<p style=\"color:99cc00\">Finger:</p> %4$s" +
    		"</html>";
    
    public static String device_info_format3 = 
            "<html>" +
            "<p style=\"color:99cc00;\">1、屏幕尺寸: <hr/></p> %1$d x %2$d, dpi=%3$.1f, 尺寸=%4$.2f" +
            "<p style=\"color:99cc00;font-size:2px\"/>" +
            "<p style=\"color:99cc00\">2、Android版本: <hr/></p> %5$s" +
            "<p style=\"color:99cc00;font-size:2px\"/>" +
            "<p style=\"color:99cc00\">3、手机名称: <hr/></p> %6$s" + 
            "<p style=\"color:99cc00;font-size:2px\"/>" +
            "<p style=\"color:99cc00\">4、软件版本: <hr/></p> %7$s" +
            "</html>";
    
    public static int SCROLLBAR_WIDTH = 8;//ScrollBar
    public static int HOST_PORT = 13470;
    public static int DEVICE_PORT = 13470;
    
    public static int PADDING_RIGHT = 4;
    public static int PADDING_LEFT  = 4;
    public static int PADDING_PANEL = 8;
    
    public static int PANEL_GAP     = 8;
    
    public static int OFFSET_LEFT = PADDING_LEFT;
    public static int OFFSET_TOP  = 100;
    public static int OFFSET_X_MAIN = 264;
    
    public static int OFFSET_RIGHT = 1;
    
    public static int WIN_WIDTH     = 960;
    public static int WIN_HEIGHT    = 722;
    public static int WIN_PANEL_H   = 590;
    
    public static int PANEL_LEFT_WIDTH = 242;
    public static int PANEL_LEFT_HEIGHT = WIN_PANEL_H;
 
    public static int PANEL_BODY_WIDTH = 472;
    
    public static int PANEL_RIGHT_WIDTH = 230;
    public static int PANEL_RIGHT_HEIGHT = WIN_PANEL_H;
}
