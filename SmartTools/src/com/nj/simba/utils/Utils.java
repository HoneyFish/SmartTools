package com.nj.simba.utils;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.nj.simba.app.SmartToolsApp;

public class Utils {
    public static Map<String, ImageIcon> mCache = new HashMap<String, ImageIcon>();
    
	public static String findAdb() {
		String adbPath = null;

		String envPath = System.getenv("PATH");
		String separator = System.getProperty("path.separator");

		String os = System.getProperty("os.name");
		System.out.println("os: " + os);

		String[] pathList = envPath.split(separator);

		for (String path : pathList) {
			String tryPath = path + "/adb";

			System.out.println(tryPath);

			File f = new File(tryPath);
			if (f.exists()) {
				adbPath = tryPath;
				break;
			}

			tryPath += ".exe";
			f = new File(tryPath);
			if (f.exists()) {
				adbPath = tryPath;
				break;
			}
		}

		System.out.println("Found adb's path: " + adbPath);

		return adbPath;
	}

	public static ImageIcon getScreenshot(IDevice device) {
		if (device == null) {
			return null;
		}

		RawImage rawImage = null;
		try {
			rawImage = device.getScreenshot();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// convert raw data to an Image
		BufferedImage image = new BufferedImage(rawImage.width,
				rawImage.height, BufferedImage.TYPE_INT_ARGB);

		int index = 0;
		int IndexInc = rawImage.bpp >> 3;
		for (int y = 0; y < rawImage.height; y++) {
			for (int x = 0; x < rawImage.width; x++) {
				int value = rawImage.getARGB(index);
				index += IndexInc;
				image.setRGB(x, y, value);
			}
		}

		try {
			if (!ImageIO.write(image, "png", new File(Config.sreenshot))) {
				throw new IOException("Failed to find png writer");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return new ImageIcon(Config.sreenshot);
	}
	
	public static int[] getProperSize(int iconW, int iconH, int dstW, int dstH) {
		int reImgWidth;
		int reImgHeight;
		
		if ( iconH > dstH ) {
			reImgHeight = dstH;
			reImgWidth = iconW * reImgHeight / iconH;
		} else {
			reImgHeight = dstH;
			reImgWidth = dstW;
		}

		return new int[] { reImgWidth, reImgHeight };
	}

	public static void getScaleImage(ImageIcon icon, int w, int h) {
        icon.setImage(icon.getImage().getScaledInstance(w, h, Image.SCALE_DEFAULT));
    }
	
	public static ImageIcon getProperRatioImage(ImageIcon icon, int w, int h) {
	    int[] proper = getProperSize(icon.getIconWidth(), icon.getIconHeight(), w, h);
	    icon.setImage(icon.getImage().getScaledInstance(proper[0], proper[1], Image.SCALE_DEFAULT));
	    return icon;
	}
	
	public static java.net.URL getImageUrl(String icon) {
		return SmartToolsApp.class.getResource(icon);
	}
	
	public static ImageIcon getResImage(String res) {
	    ImageIcon icon = mCache.get(res);
	    
	    if ( icon == null ) {
	        icon = new ImageIcon(new File(res).getAbsolutePath());
	    }
	    return icon;
	}
	
	public static void loadCache() {
	    ImageIcon icon = new ImageIcon(new File("res/folder-mid.png").getAbsolutePath());
	    mCache.put("res/folder.png", icon);
	    
	    icon = new ImageIcon(new File("res/file-mid.png").getAbsolutePath());
        mCache.put("res/file-mid.png", icon);
        
        icon = new ImageIcon(new File("res/device-off.png").getAbsolutePath());
        mCache.put("res/device-off.png", icon);
        
        icon = new ImageIcon(new File("res/refresh.png").getAbsolutePath());
        mCache.put("res/refresh.png", icon);
        
        icon = new ImageIcon(new File("res/capture.png").getAbsolutePath());
        mCache.put("res/capture.png", icon);
        
        icon = new ImageIcon(new File("res/save.png").getAbsolutePath());
        mCache.put("res/save.png", icon);
        
        icon = new ImageIcon(new File("res/to-up.png").getAbsolutePath());
        mCache.put("res/to-up.png", icon);
	}
	
	public static void addToCache(String res, ImageIcon icon) {
	    mCache.put(res, icon);
	}
	
	public static String getAndroidName(int version) {
        switch (version) {
        case  3: return "Android1.5, CupCake";
        case  4: return "Android1.6, Donut";
        case  5: return "Android2.0, Eclair";
        case  6: return "Android2.0.1, Eclair_0_1";
        case  7: return "Android2.1, Eclair_MR1";
        case  8: return "Android2.2, Froyo";
        case  9: return "Android2.3, GingerBread";
        case 10: return "Android2.3.3, GingerBread_MR1";
        case 11: return "Android3.0, HoneyComb";
        case 12: return "Android3.1, HoneyComb_MR1";
        case 13: return "Android3.2, HoneyComb_MR2";
        case 14: return "Android4.0, Ice Cream Sandwich";
        case 15: return "Android4.0.3, Ice Cream Sandwich_MR1";
        case 16: return "Android4.1.2, Jelly Bean";
        case 17: return "Android4.2.2, Jelly Bean_MR1";
        case 18: return "Android4.3, Jelly Bean_MR2";
        case 19: return "Android4.4, KitKat";
        case 20: return "Android5.0, LaLa";
        default: return "Android ? Ah~~!";
        }
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
