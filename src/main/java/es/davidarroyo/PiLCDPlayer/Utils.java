package es.davidarroyo.PiLCDPlayer;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

import javax.swing.ImageIcon;

public class Utils {
	
	/**
	 * Reads an image from a file inside the current classpath.
	 * 
	 * @return the image; can be displayed and used in ImageIcons.
	 */
	public static Image loadImage(String path) {
		URL imgUrl = Utils.class.getClassLoader().getResource(path);
		return Toolkit.getDefaultToolkit().createImage(imgUrl);
	}
	
	public static ImageIcon createImage(String path, int width, int height) {
		URL imgUrl = Utils.class.getClassLoader().getResource(path);
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(imgUrl).getScaledInstance(width, height, Image.SCALE_SMOOTH));
	}
	
	public static ImageIcon createImage(String path) {
		URL imgUrl = Utils.class.getClassLoader().getResource(path);
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(imgUrl));
	}

}
