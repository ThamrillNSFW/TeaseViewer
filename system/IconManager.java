package system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class IconManager {
	TreeMap<String, ImageIcon> icons;

	public void initialize() {
		icons = new TreeMap<>();
		icons.put("default", createDefaultIcon());
		TreeMap<String, String> strings = new TreeMap<>();
		strings.put("teaseviewer", "resources/icons/teaseviewer.png");
		strings.put("browser_small", "resources/icons/browser_small.png");
		strings.put("file.downloadtease", "resources/icons/downloadtease.png");
		strings.put("file.opentease", "resources/icons/open.png");
		for (String key : strings.keySet()) {
			try {
				icons.put(key, new ImageIcon(ImageIO.read(ClassLoader.getSystemResource(strings.get(key)))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ImageIcon getIcon(String id) {
		if (icons == null) {
			initialize();
		}
		
		if (icons.containsKey(id) && icons.get(id) != null) {
			return icons.get(id);
		}else {
			Logger.staticLog("Missing icon:"+id, Logger.WARNING);
		}
		return icons.get("default");
	}

	public ImageIcon createDefaultIcon() {
		int l = 16;
		BufferedImage img = new BufferedImage(l, l, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) img.getGraphics();

		g2d.setColor(Color.pink);
		g2d.fillRect(0, 0, l, l);
		g2d.setColor(Color.WHITE);
		g2d.drawRect(1, 1, l - 3, l - 3);
		g2d.setColor(Color.RED);
		g2d.drawLine(3, 3, l - 4, l - 4);
		g2d.drawLine(3, l - 4, l - 4, 3);
		g2d.dispose();

		return new ImageIcon(img);
	}

}
