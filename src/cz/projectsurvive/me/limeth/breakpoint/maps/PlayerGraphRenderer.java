package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class PlayerGraphRenderer extends BPMapRenderer
{
	public static byte[] bgColor = new byte[] { BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 0), BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 1), BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 2) };
	public static byte[][] graphColor = new byte[][] { new byte[] { BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 2), BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 1), BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 0) }, new byte[] { BPMapPalette.getColor(BPMapPalette.YELLOW, 2), BPMapPalette.getColor(BPMapPalette.YELLOW, 1), BPMapPalette.getColor(BPMapPalette.YELLOW, 0) } };
	public static byte headColor = BPMapPalette.getColor(BPMapPalette.WHITE, 1);
	public static byte headTextColor = MapPalette.DARK_GRAY;
	private List<Integer> onlinePlayers;

	public PlayerGraphRenderer(ArrayList<Integer> onlinePlayers)
	{
		this.onlinePlayers = onlinePlayers;
	}

	public void setOnlinePlayers(List<Integer> onlinePlayers)
	{
		this.onlinePlayers = onlinePlayers;
	}

	public List<Integer> getOnlinePlayers()
	{
		return onlinePlayers;
	}

	public void addStat(int amount)
	{
		moveList();
		onlinePlayers.set(0, amount);
	}

	public void moveList()
	{
		int length = MAP_SIZE - 2;
		if (onlinePlayers.size() < length)
		{
			onlinePlayers.add(0);
			length = onlinePlayers.size();
		}
		for (int i = 1; i <= length - 1; i++)
			onlinePlayers.set(length - i, onlinePlayers.get(length - i - 1));
		onlinePlayers.set(0, 0);
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		int headerHeight = MinecraftFont.Font.getHeight() + 4;
		BPPlayer bpPlayer = BPPlayer.get(player);
		drawRectangle(canvas, 0, 0, MAP_SIZE, headerHeight, headColor);
		drawText(canvas, 2, 2, 1, 1, MinecraftFont.Font, "Graf online hracu:", headTextColor);
		drawRectangleFade(canvas, 0, headerHeight, MAP_SIZE, MAP_SIZE - headerHeight, bgColor, 0);
		drawGraph(canvas, headerHeight, bpPlayer);
	}

	public void drawGraph(MapCanvas canvas, int rawStartY, BPPlayer bpPlayer)
	{
		int graphWidth = MAP_SIZE - 2;
		int graphHeight = MAP_SIZE - rawStartY - 2;
		int maxPlayers = Bukkit.getMaxPlayers();
		long joined = bpPlayer.getTimeJoined();
		int joinedMinutes = (int) (joined / (1000.0 * 60.0));
		for (int x = 0; x < graphWidth; x++)
			drawColumn(canvas, MAP_SIZE - x - 2, MAP_SIZE - 2, graphHeight, x, maxPlayers, joinedMinutes);
	}

	public void drawColumn(MapCanvas canvas, int x, int startY, int height, int id, int maxPlayers, int joinedMinutes)
	{
		if (onlinePlayers.size() <= id)
			return;
		int perc = (int) (((double) onlinePlayers.get(id) / (double) maxPlayers) * height);
		int colorId = joinedMinutes < ((System.currentTimeMillis() / 60000.0) - (id * MapManager.playerGraphDelay)) ? 1 : 0;
		drawRectangleFade(canvas, x, startY - perc, 1, perc, graphColor[colorId], 1);
	}
}
