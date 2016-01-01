package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.awt.image.BufferedImage;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class ImageRenderer extends BPMapRenderer
{
	byte[][] image;

	public ImageRenderer(String path)
	{
		BufferedImage rawImage = getImage(path);
		rawImage = MapPalette.resizeImage(rawImage);
		image = toBytes(rawImage);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		drawBytes(canvas, image);
	}
}
