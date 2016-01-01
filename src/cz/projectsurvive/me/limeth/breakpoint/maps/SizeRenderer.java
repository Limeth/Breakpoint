package cz.projectsurvive.me.limeth.breakpoint.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public class SizeRenderer extends BPMapRenderer
{
	private static final int fontHeight = MAP_SIZE / MinecraftFont.Font.getHeight();
	byte foregroundColor, backgroundColor;
	int size;

	public SizeRenderer(byte foregroundColor, byte backgroundColor, int size)
	{
		this.foregroundColor = foregroundColor;
		this.backgroundColor = backgroundColor;
		this.size = size;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		drawRectangle(canvas, 0, 0, MAP_SIZE, MAP_SIZE, BPMapPalette.getColor(BPMapPalette.TRANSPARENT, 0));
		drawSize(canvas);
	}

	public void drawSize(MapCanvas canvas)
	{
		String sSize = Integer.toString(size);
		int totalWidth = getWidth(sSize);
		int fontWidth = MAP_SIZE / totalWidth;
		
		int startX = ((MAP_SIZE - (totalWidth * fontWidth)) / 2);
		int startY = ((MAP_SIZE - (MinecraftFont.Font.getHeight() * fontHeight)) / 2);
		
		drawRectangle(canvas, 0, 0, MAP_SIZE, MAP_SIZE, backgroundColor);
		drawText(canvas, startX, startY, fontWidth, fontHeight, MinecraftFont.Font, sSize, foregroundColor);
	}

	public void setSize(int size)
	{
		this.size = size;
	}
}
