package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapFont.CharacterSprite;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;

public abstract class BPMapRenderer extends MapRenderer
{
	public static final int MAP_SIZE = 128;

	public void set(MapView mw)
	{
		for (MapRenderer mr : mw.getRenderers())
			mw.removeRenderer(mr);
		mw.addRenderer(this);
	}

	public static void setBackgroundColor(MapCanvas canvas, byte color)
	{
		for (int y = 0; y < MAP_SIZE; y++)
			for (int x = 0; x < MAP_SIZE; x++)
				canvas.setPixel(x, y, color);
	}

	public static void drawRectangle(MapCanvas canvas, int startX, int startY, int width, int height, byte color)
	{
		for (int x = startX; x < startX + width; x++)
			for (int y = startY; y < startY + height; y++)
				canvas.setPixel(x, y, color);
	}

	public static void drawRectangleFade(MapCanvas canvas, int startX, int startY, int width, int height, byte[] color, int fadeId)
	{
		int colorLength = color.length + color.length - 1;
		double barHeight = height / colorLength;
		for (double bar = 0; bar < colorLength; bar++)
		{
			int barStartY = (int) (barHeight * bar);
			int reminder = (int) bar % 2;
			boolean fade = reminder != 0;
			if (fade)
				for (int x = startX; x < startX + width; x++)
					for (int y = startY; y < startY + height; y++)
					{
						int color1 = (int) (bar - reminder) / 2;
						int color2 = (int) (bar - reminder) / 2 + 1;
						int curColor = (x + y + barStartY) % 2 == fadeId ? color1 : color2;
						canvas.setPixel(x, y + barStartY, color[curColor]);
					}
			else
				for (int x = startX; x < startX + width; x++)
					for (int y = startY; y < startY + height; y++)
						canvas.setPixel(x, y + barStartY, color[(int) (bar / 2)]);
		}
	}

	public void drawAvailableColors(MapCanvas canvas)
	{
		for (int y = 0; y < MAP_SIZE / 4; y++)
			for (int x = 0; x < MAP_SIZE / 4; x++)
			{
				int id = y * (MAP_SIZE / 4) + x;
				try
				{
					drawRectangle(canvas, x * 4, y * 4, 4, 4, BPMapPalette.matchColor(BPMapPalette.colors[id]));
				}
				catch (Exception e)
				{
					return;
				}
			}
	}
	
	public static final char COLOR_CHAR = '\247';
	
    public void drawText(MapCanvas canvas, int x, int y, int width, int height, MapFont font, String text, byte color)
    {
        int xStart = x;
        if(!font.isValid(text))
            throw new IllegalArgumentException("text contains invalid characters");
        for(int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            if(ch == '\n')
            {
                x = xStart;
                y += font.getHeight() + 1;
                continue;
            }
            if(ch == COLOR_CHAR)
            {
                int j = text.indexOf(';', i);
                if(j >= 0)
                    try
                    {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    }
                    catch(NumberFormatException ex) { }
            }
            org.bukkit.map.MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
			for(int r = 0; r < font.getHeight(); ++r)
				for(int c = 0; c < sprite.getWidth(); ++c)
					for(int xx = 0; xx < width; xx++)
						for(int yy = 0; yy < height; yy++)
							if(sprite.get(r, c))
								canvas.setPixel(x + xx + c * width, y + yy + r * height, color);

            x += (sprite.getWidth() + 1) * width;
        }

    }
	
    public void drawText(Byte[][] canvas, int x, int y, int width, int height, MapFont font, String text, byte color)
    {
        int xStart = x;
        if(!font.isValid(text))
            throw new IllegalArgumentException("text contains invalid characters");
        for(int i = 0; i < text.length(); i++)
        {
            char ch = text.charAt(i);
            if(ch == '\n')
            {
                x = xStart;
                y += font.getHeight() + 1;
                continue;
            }
            if(ch == COLOR_CHAR)
            {
                int j = text.indexOf(';', i);
                if(j >= 0)
                    try
                    {
                        color = Byte.parseByte(text.substring(i + 1, j));
                        i = j;
                        continue;
                    }
                    catch(NumberFormatException ex) { }
            }
            org.bukkit.map.MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
			for(int r = 0; r < font.getHeight(); ++r)
				for(int c = 0; c < sprite.getWidth(); ++c)
					for(int xx = 0; xx < width; xx++)
						for(int yy = 0; yy < height; yy++)
							if(sprite.get(r, c))
							{
								int absX = x + xx + c * width;
								int absY = y + yy + r * height;
								
								if(canvas.length > absX && canvas[absX].length > absY)
									canvas[absX][absY] = color;
							}

            x += (sprite.getWidth() + 1) * width;
        }

    }

	public static void drawTextOld(MapCanvas canvas, int x, int y, int width, int height, MapFont font, String text)
	{
		int xStart = x;
		byte color = MapPalette.DARK_GRAY;
	/*	String text = "";
		
		for(char c : rawText.toCharArray())
			if(MinecraftFont.Font.getChar(c) != null)
				text += c;*/
		
		if(!MinecraftFont.Font.isValid(text))
			return;
		
		for (int i = 0; i < text.length(); ++i)
		{
			char ch = text.charAt(i);
			if (ch == '\n')
			{
				x = xStart;
				y += font.getHeight() + 1;
				continue;
			}
			else
				if (ch == '§')
				{
					int j = text.indexOf(';', i);
					if (j >= 0)
						try
						{
							color = Byte.parseByte(text.substring(i + 1, j));
							i = j;
							continue;
						}
						catch (NumberFormatException ex)
						{
						}
				}
			CharacterSprite sprite = font.getChar(text.charAt(i));
			for(int r = 0; r < font.getHeight(); ++r)
				for(int c = 0; c < sprite.getWidth(); ++c)
					for(int xx = 0; xx < width; xx++)
						for(int yy = 0; yy < height; yy++)
							if(sprite.get(r, c))
								canvas.setPixel(x + xx + c * width, y + yy + r * height, color);
			x += (sprite.getWidth() + 1) * width;
		}
	}

	public static void drawTextOld(Byte[][] canvas, int x, int y, int width, int height, MapFont font, String text)
	{
		int xStart = x;
		byte color = MapPalette.DARK_GRAY;
	/*	String text = "";
		
		for(char c : rawText.toCharArray())
			if(MinecraftFont.Font.getChar(c) != null)
				text += c;*/
		
		if(!MinecraftFont.Font.isValid(text))
			return;
		
		for (int i = 0; i < text.length(); ++i)
		{
			char ch = text.charAt(i);
			if (ch == '\n')
			{
				x = xStart;
				y += font.getHeight() + 1;
				continue;
			}
			else
				if (ch == '§')
				{
					int j = text.indexOf(';', i);
					if (j >= 0)
						try
						{
							color = Byte.parseByte(text.substring(i + 1, j));
							i = j;
							continue;
						}
						catch (NumberFormatException ex)
						{
						}
				}
			CharacterSprite sprite = font.getChar(text.charAt(i));
			for (int r = 0; r < font.getHeight(); ++r)
				for (int c = 0; c < sprite.getWidth(); ++c)
					for (int xx = 0; xx < width; xx++)
						for (int yy = 0; yy < height; yy++)
							if (sprite.get(r, c))
							{
								int absX = x + xx + c * width;
								int absY = y + yy + r * height;
								
								if(canvas.length > absX && canvas[absX].length > absY)
									canvas[absX][absY] = color;
							}
			
			x += (sprite.getWidth() + 1) * width;
		}
	}

	@SuppressWarnings("deprecation")
	public static byte[][] toBytes(BufferedImage image)
	{
		if (image == null)
		{
			byte[][] map = new byte[MAP_SIZE][MAP_SIZE];
			byte color = BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 0);
			for (int x = 0; x < MAP_SIZE; x++)
				for (int y = 0; y < MAP_SIZE; y++)
					map[x][y] = color;
			return map;
		}
		int width = image.getWidth();
		int height = image.getHeight();
		byte[][] map = new byte[width][height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
			{
				Color color = Color.decode(Integer.toString(image.getRGB(x, y)));
				map[x][y] = MapPalette.matchColor(color);
			}
		return map;
	}
	
	public static int getWidth(String s)
	{
		if(s.length() == 0)
			return MinecraftFont.Font.getWidth(s);
		else
			return MinecraftFont.Font.getWidth(s) + s.length() - 1;
	}

	public static void drawBytes(MapCanvas canvas, byte[][] image)
	{
		for (int x = 0; x < image.length; x++)
			for (int y = 0; y < image[0].length; y++)
				canvas.setPixel(x, y, image[x][y]);
	}

	public static BufferedImage getImage(String path)
	{
		try
		{
			File file = new File(path);
			BufferedImage img = ImageIO.read(file);
			return img;
		}
		catch (Throwable e)
		{
			Breakpoint.warn("Error when loading map image '" + path + "'.");
			e.printStackTrace();
			return null;
		}
	}
}
