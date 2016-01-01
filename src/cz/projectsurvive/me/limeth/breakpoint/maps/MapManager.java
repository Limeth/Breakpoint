package cz.projectsurvive.me.limeth.breakpoint.maps;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.StatisticsManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public class MapManager
{
	private static short usedIds = 0;
	public static final int playerGraphDelay = 1; // Minuty

	public static PlayerGraphRenderer playerGraphRenderer;
	public static short breakpointMapId, playerGraphMapId, vipMapId, czechFlagMapId, slovakFlagMapId, totalPlayersMapId, totalKillsMapId, totalDeathsMapId, totalMoneyMapId, totalBoughtMapId;
	
	public static short getNextFreeId(int amount)
	{
		short id = usedIds;
		usedIds += amount;
		return id;
	}
	
	public static short getNextFreeId()
	{
		return getNextFreeId(1);
	}
	
	public static void setup()
	{
		setIds();
		setRenderers();
	}
	
	private static void setIds()
	{
		breakpointMapId = getNextFreeId();
		playerGraphMapId = getNextFreeId();
		vipMapId = getNextFreeId();
		czechFlagMapId = getNextFreeId();
		slovakFlagMapId = getNextFreeId();
		totalPlayersMapId = getNextFreeId();
		totalKillsMapId = getNextFreeId();
		totalDeathsMapId = getNextFreeId();
		totalMoneyMapId = getNextFreeId();
		totalBoughtMapId = getNextFreeId();
	}

	private static void setRenderers()
	{
		new ImageRenderer("plugins/Breakpoint/images/logo.png").set(Bukkit.getMap(breakpointMapId));
		new ImageRenderer("plugins/Breakpoint/images/vip.png").set(Bukkit.getMap(vipMapId));
		new ImageRenderer("plugins/Breakpoint/images/czech.png").set(Bukkit.getMap(czechFlagMapId));
		new ImageRenderer("plugins/Breakpoint/images/slovak.png").set(Bukkit.getMap(slovakFlagMapId));
		
		playerGraphRenderer = new PlayerGraphRenderer(new ArrayList<Integer>(BPMapRenderer.MAP_SIZE - 2));
		new PlayerGraphRenderer(new ArrayList<Integer>(BPMapRenderer.MAP_SIZE - 2)).set(Bukkit.getMap(playerGraphMapId));
		
		//STATS
		new StatisticRenderer("Hracu", BPMapPalette.getColor(BPMapPalette.LIGHT_BLUE, 2)) {
			@Override
			public String getValue()
			{
				if(StatisticsManager.isUpdating() || !StatisticsManager.hasTotalStats())
					return "Nacitam...";
				
				return Integer.toString(StatisticsManager.getTotalStats().getPlayerAmount());
			}
		}.set(Bukkit.getMap(totalPlayersMapId));
		
		new StatisticRenderer("Zabiti", BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 2)) {
			@Override
			public String getValue()
			{
				if(StatisticsManager.isUpdating() || !StatisticsManager.hasTotalStats())
					return "Nacitam...";
				
				return Integer.toString(StatisticsManager.getTotalStats().getKills());
			}
		}.set(Bukkit.getMap(totalKillsMapId));
		
		new StatisticRenderer("Umrti", BPMapPalette.getColor(BPMapPalette.RED, 2)) {
			@Override
			public String getValue()
			{
				if(StatisticsManager.isUpdating() || !StatisticsManager.hasTotalStats())
					return "Nacitam...";
				
				return Integer.toString(StatisticsManager.getTotalStats().getDeaths());
			}
		}.set(Bukkit.getMap(totalDeathsMapId));
		
		new StatisticRenderer("Emeraldu", BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 2)) {
			@Override
			public String getValue()
			{
				if(StatisticsManager.isUpdating() || !StatisticsManager.hasTotalStats())
					return "Nacitam...";
				
				return Integer.toString(StatisticsManager.getTotalStats().getMoney());
			}
		}.set(Bukkit.getMap(totalMoneyMapId));
		
		new StatisticRenderer("Nakoupeno veci", BPMapPalette.getColor(BPMapPalette.YELLOW, 2)) {
			@Override
			public String getValue()
			{
				if(StatisticsManager.isUpdating() || !StatisticsManager.hasTotalStats())
					return "Nacitam...";
				
				return Integer.toString(StatisticsManager.getTotalStats().getBought());
			}
		}.set(Bukkit.getMap(totalBoughtMapId));
	}

	public static void updateLobbyMapsForPlayer(BPPlayer bpPlayer)
	{
		for(Game game : GameManager.getGames())
			game.updateLobbyMaps(bpPlayer);
	}
	
	public static void updateLobbyMapsNotPlayingPlayers()
	{
		for(BPPlayer bpPlayer : BPPlayer.onlinePlayers)
			if(!bpPlayer.isPlaying())
				updateLobbyMapsForPlayer(bpPlayer);
	}

	public static void updatePlayerGraphForPlayer(Player player)
	{
		player.sendMap(Bukkit.getMap(playerGraphMapId));
	}

	public static void updatePlayerGraphForPlayers()//CraftMapCanvas
	{
		for (BPPlayer bpPlayer : BPPlayer.onlinePlayers)
			if(bpPlayer.isInLobby())
			{
				Player player = bpPlayer.getPlayer();
				
				updatePlayerGraphForPlayer(player);
			}
	}
	
	public static void updateMapForNotPlayingPlayers(short mapId)
	{
		for(BPPlayer bpPlayer : BPPlayer.onlinePlayers)
			if(!bpPlayer.isPlaying())
				bpPlayer.getPlayer().sendMap(Bukkit.getMap(mapId));
	}

	public static void startLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				playerGraphRenderer.addStat(Bukkit.getOnlinePlayers().length);
				updatePlayerGraphForPlayers();
			}
		}, 0, 20L * 60 * playerGraphDelay);
	}

	public static ItemStack getMap(Player player, short id)
	{
		ItemStack is = new ItemStack(Material.MAP, 1, id);
		MapView mw = Bukkit.getMap(id);
		player.sendMap(mw);
		return is;
	}

	public static ItemStack getPlayerGraphMap(Player player)
	{
		ItemStack is = getMap(player, playerGraphMapId);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GREEN + "Graf online hrácu");
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getBreakpointMap(Player player)
	{
		ItemStack is = getMap(player, breakpointMapId);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Vítejte na serveru BREAKPOINT.");
		is.setItemMeta(im);
		return is;
	}
}
