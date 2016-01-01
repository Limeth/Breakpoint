package cz.projectsurvive.me.limeth.breakpoint.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;

import cz.projectsurvive.me.limeth.breakpoint.game.BPMap;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFMap;

public class CurrentMapRenderer extends BPMapRenderer
{
	private byte[][] image;

	public CurrentMapRenderer(CTFMap map)
	{
		setCurrentMap(map);
	}

	public CurrentMapRenderer()
	{
		this(null);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		if (image != null)
			drawImage(canvas);
	}

	public void setCurrentMap(BPMap map)
	{
		image = toBytes(map != null ? map.getImage() : null);
	}

	public void drawImage(MapCanvas canvas)
	{
		for (int x = 0; x < image.length; x++)
			for (int y = 0; y < image[0].length; y++)
				canvas.setPixel(x, y, image[x][y]);
	}
}
