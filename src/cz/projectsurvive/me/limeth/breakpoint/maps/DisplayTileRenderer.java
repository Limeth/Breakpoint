package cz.projectsurvive.me.limeth.breakpoint.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

public class DisplayTileRenderer extends BPMapRenderer
{
	private final Display display;
	private final int tileX, tileY;
	
	public DisplayTileRenderer(Display display, int tileX, int tileY)
	{
		this.display = display;
		this.tileX = tileX;
		this.tileY = tileY;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		int surfaceX = getSurfaceX();
		int surfaceY = getSurfaceY();
		
		for(int x = 0; x < MAP_SIZE; x++)
			for(int y = 0; y < MAP_SIZE; y++)
			{
				byte color = display.getColor(surfaceX + x, surfaceY + y);
				
				canvas.setPixel(x, y, color);
			}
	}

	public Display getDisplay()
	{
		return display;
	}
	
	public int getSurfaceX()
	{
		return tileX * MAP_SIZE;
	}
	
	public int getSurfaceY()
	{
		return tileY * MAP_SIZE;
	}

	public int getTileX()
	{
		return tileX;
	}

	public int getTileY()
	{
		return tileY;
	}
}
