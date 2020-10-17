package utilities;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class ColorUtilities {
	
	public static final Color darkestGray=new Color(0.05f, 0.05f, 0.05f);

	public static Color getBackgroundCardinalColor(Image img, int depth) {
		BufferedImage image = (BufferedImage) img;
		ArrayList<Integer> Rs = new ArrayList<>();
		ArrayList<Integer> Gs = new ArrayList<>();
		ArrayList<Integer> Bs = new ArrayList<>();
		ArrayList<Float> Hs = new ArrayList<>();
		ArrayList<Float> Vs = new ArrayList<>();
		ArrayList<Float> Ss = new ArrayList<>();
		for (int xx = 0; xx < image.getWidth(); xx++) {
			for (int yy = 0; yy < image.getHeight(); yy++) {
				if (xx > depth && xx < image.getWidth() - depth && yy > depth && yy < image.getHeight() - depth) {
					continue;
				}
				int clr = image.getRGB(xx, yy);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				Rs.add(red);
				Gs.add(green);
				Bs.add(blue);
				float[] c = Color.RGBtoHSB((int) (red * 255), (int) (green * 255), (int) (blue * 255), null);
				Hs.add(c[0]);
				Ss.add(c[1]);
				Vs.add(c[2]);
			}
		}
		return getCardinalColorForPoints(Hs, Ss, Vs);

	}

	public static Color getOpposite(Color c) {
		return new Color(Color.white.getRGB() - c.getRGB());
	}

	public static Color getHalfApproximatedColorForPoints(ArrayList<Integer> Rs, ArrayList<Integer> Gs,
			ArrayList<Integer> Bs) {
		long R = 0;
		long G = 0;
		long B = 0;
		for (int ii = 0; ii < Rs.size(); ii++) {
			R += (int) (Math.floor(Rs.get(ii) * 1f / 256 * 3) / 2 * 255);
			G += (int) (Math.floor(Gs.get(ii) * 1f / 256 * 3) / 2 * 255);
			B += (int) (Math.floor(Bs.get(ii) * 1f / 256 * 3) / 2 * 255);
		}
		R /= Rs.size();
		G /= Gs.size();
		B /= Bs.size();
		R = R < 256 ? R : 255;
		G = G < 256 ? G : 255;
		B = B < 256 ? B : 255;
		return new Color((int) R, (int) G, (int) B);
	}

	public static Color getCardinalColorForPoints(ArrayList<Float> Hs, ArrayList<Float> Ss, ArrayList<Float> Vs) {
		double[] hues = new double[8];
		for (int ii = 0; ii < Hs.size(); ii++) {
			int hue = Math.round(MathUtilities.discretizeFraction(Hs.get(ii), 7) * 6);
			if (Ss.get(ii) < 0.1) {
				if (Vs.get(ii) < 0.5) {
					hues[6] += 1;
				}
				if (Vs.get(ii) > 0.5) {
					hues[7] += 1;
				}
			} else {
				if (hue == 6) {
					hue = 0;
				}
				hues[hue] += 1;
			}
		}
		double max = 0;
		int maxIndex = 0;
		for (int ii = 0; ii < hues.length; ii++) {
			if (hues[ii] > max) {
				max = hues[ii];
				maxIndex = ii;
			}
		}
		switch (maxIndex) {
		case 0:
			return Color.RED;
		case 1:
			return Color.YELLOW;
		case 2:
			return Color.GREEN;
		case 3:
			return Color.CYAN;
		case 4:
			return Color.BLUE;
		case 5:
			return Color.MAGENTA;
		case 6:
			return darkestGray;
		case 7:
			return Color.WHITE;
		}
		return Color.gray;
	}

	public static Color getUnsaturatedColor(Color c) {
		float[] components = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
		Color c1 = Color.getHSBColor(components[0], components[1] * 0.5f, MathUtilities.clamp(components[2] * 2f));
		return c1;
	}
}
