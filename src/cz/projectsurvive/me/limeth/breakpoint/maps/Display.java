package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.awt.image.BufferedImage;

public class Display
{
	private static final int mapSize = 128;
	private final int tileWidth, tileHeight;
	private final short topLeftMapId;
	private final byte[][] surface;
	
	public Display(int tileWidth, int tileHeight)
	{
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		surface = new byte[mapSize * tileWidth][mapSize * tileHeight];
		topLeftMapId = MapManager.getNextFreeId(getTileAmount());
	}
	
	public byte[][] getSurface()
	{
		return surface;
	}
	
	public int getTileAmount()
	{
		return tileWidth * tileHeight;
	}
	
	public byte getColor(int x, int y)
	{
		return surface[x][y];
	}
	
	public void setColor(int x, int y, byte color)
	{
		surface[x][y] = color;
	}
	
	public int getTileWidth()
	{
		return tileWidth;
	}
	
	public int getTileHeight()
	{
		return tileHeight;
	}

	public short getTopLeftMapId()
	{
		return topLeftMapId;
	}
	
	public static void initializeWithImage(String path)
	{
		BufferedImage img = BPMapRenderer.getImage(path);
		int surfaceWidth = img.getWidth();
		int surfaceHeight = img.getHeight();
		byte[][] bytes = BPMapRenderer.toBytes(img);
		int tileWidth = surfaceWidth / mapSize + (surfaceWidth % mapSize > 0 ? 1 : 0);
		int tileHeight = surfaceHeight / mapSize + (surfaceHeight % mapSize > 0 ? 1 : 0);
		Display display = new Display(tileWidth, tileHeight);
		int startX = (tileWidth * mapSize - surfaceWidth) / 2;
		int startY = (tileHeight * mapSize - surfaceWidth) / 2;
		
		for(int x = 0; x < surfaceWidth; x++)
			for(int y = 0; y < surfaceHeight; y++)
				display.setColor(startX + x, startY + y, bytes[x][y]);
	}
}
