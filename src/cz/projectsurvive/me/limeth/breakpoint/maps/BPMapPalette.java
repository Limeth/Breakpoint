package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.awt.Color;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;

public class BPMapPalette
{
	public static final int TRANSPARENT = 0;
	public static final int LIGHT_GREEN = 1;
	public static final int YELLOW = 2;
	public static final int LIGHT_GRAY_1 = 3;
	public static final int RED = 4;
	public static final int LIGHT_BLUE = 5;
	public static final int LIGHT_GRAY_2 = 6;
	public static final int DARK_GREEN = 7;
	public static final int WHITE = 8;
	public static final int LIGHT_GRAY_BLUE = 9;
	public static final int LIGHT_BROWN = 10;
	public static final int DARK_GRAY = 11;
	public static final int DARK_BLUE = 12;
	public static final int DARK_BROWN = 13;
	public static final int baseColorLength = 14;

	public static byte getColor(int id, int shade)
	{ // TODO use
		int colorId = id * 4 + shade;
		if (colorId < colors.length)
			return matchColor(colors[id * 4 + shade]);
		else
		{
			Breakpoint.warn("Incorrect color! id: " + id + " shade: " + shade + "; make sure to use BPMapPalette instead of MapPalette!");
			return 0;
		}
	}

	public static final Color[] colors = { new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), new Color(0, 0, 0, 0), c(89, 125, 39), c(109, 153, 48), c(27, 178, 56), c(109, 153, 48), c(174, 164, 115), c(213, 201, 140), c(247, 233, 163), c(213, 201, 140), c(117, 117, 117), c(144, 144, 144), c(167, 167, 167), c(144, 144, 144), c(180, 0, 0), c(220, 0, 0), c(255, 0, 0), c(220, 0, 0), c(112, 112, 180), c(138, 138, 220), c(160, 160, 255), c(138, 138, 220), c(117, 117, 117), c(144, 144, 144), c(167, 167, 167), c(144, 144, 144), c(0, 87, 0), c(0, 106, 0), c(0, 124, 0), c(0, 106, 0), c(180, 180, 180), c(220, 220, 220), c(255, 255, 255), c(220, 220, 220), c(115, 118, 129), c(141, 144, 158), c(164, 168, 184), c(141, 144, 158), c(129, 74, 33), c(157, 91, 40), c(183, 106, 47), c(157, 91, 40), c(79, 79, 79), c(96, 96, 96), c(112, 112, 112), c(96, 96, 96), c(45, 45, 180), c(55, 55, 220), c(64, 64, 255), c(55, 55, 220), c(73, 58, 35), c(89, 71, 43), c(104, 83, 50), c(89, 71, 43) };

	private static Color c(int r, int g, int b)
	{
		return new Color(r, g, b);
	}

	public static byte matchColor(Color color)
	{
		if (color.getAlpha() < 128)
			return 0;
		int index = 0;
		double best = -1;
		for (int i = 4; i < colors.length; i++)
		{
			double distance = getDistance(color, colors[i]);
			if (distance < best || best == -1)
			{
				best = distance;
				index = i;
			}
		}
		return (byte) index;
	}

	private static double getDistance(Color c1, Color c2)
	{
		double rmean = (c1.getRed() + c2.getRed()) / 2.0;
		double r = c1.getRed() - c2.getRed();
		double g = c1.getGreen() - c2.getGreen();
		int b = c1.getBlue() - c2.getBlue();
		double weightR = 2 + rmean / 256.0;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256.0;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}
}
