package cz.projectsurvive.me.limeth.breakpoint.maps;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import cz.projectsurvive.me.limeth.breakpoint.game.BPMap;

public class VoteRenderer extends BPMapRenderer
{
	String name;
	byte[][] image;

	public VoteRenderer(BPMap bpMap)
	{
		name = removeSpecial(bpMap.getName());
		image = toBytes(bpMap.getImage());
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		byte textFrontColor = BPMapPalette.getColor(BPMapPalette.WHITE, 2);
		byte textBackColor = BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 0);
		int textWidth = 2;//MAP_SIZE / getWidth(name);
		int textHeight = 2;
		drawBytes(canvas, image);
		drawText(canvas, 3, 3, textWidth, textHeight, MinecraftFont.Font, name, textBackColor);
		drawText(canvas, 2, 2, textWidth, textHeight, MinecraftFont.Font, name, textFrontColor);
	}

	public static String removeSpecial(String string)
	{
		string = string.replaceAll("á", "a");
		string = string.replaceAll("í", "i");
		string = string.replaceAll("é", "e");
		string = string.replaceAll("ú", "u");
		string = string.replaceAll("ó", "o");
		return string;
	}
}
