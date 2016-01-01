package cz.projectsurvive.me.limeth.breakpoint.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.map.MinecraftFont;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SBManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class PlayerConnectionListener implements Listener
{
	Breakpoint plugin;
	public static final int spaceWidth = 5;

	public PlayerConnectionListener(Breakpoint p)
	{
		plugin = p;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		
		System.out.println("Logged in: " + player);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		event.setJoinMessage(null);
		
//		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable()
//		{
//			@Override
//			public void run()
//			{
				Player player = event.getPlayer();
				String playerName = player.getName();
				BPPlayer bpPlayer;
				
				try
				{
					bpPlayer = BPPlayer.get(playerName, true);
				}
				catch(Exception e)
				{
					player.kickPlayer(ChatColor.RED + "Breakpoint Error: " + e.getMessage());
					return;
				}
				
				bpPlayer.clearAfkSecondsToKick();
				bpPlayer.reset();
				//plugin.fim.loadPlayerData(player);
				
				if (player.isDead())
					return;
				
				SBManager sbm = bpPlayer.getScoreboardManager();
				
				player.setGameMode(GameMode.ADVENTURE);
				bpPlayer.spawn();
				sbm.updateLobbyObjective();
				bpPlayer.setPlayerListName();
				player.setHealthScaled(true);
//			}
//		});
	}

/*	public void onPlayerDisconnect(Player player)
	{
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if (!bpPlayer.isInGame())
			InventoryMenuManager.saveLobbyMenu(player, bpPlayer);
		else
			bpPlayer.updateArmorMinutesLeft();
		
		bpPlayer.saveData();
		//plugin.fim.savePlayerData(player);
		bpPlayer.reset(plugin);
		BPPlayer.removePlayer(bpPlayer);
		bpPlayer.tryFinalize();
		plugin.gm.updateTeamMapViews();
	}*/

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(null);
		
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(bpPlayer == null)
			return;
		
		if (!bpPlayer.isInGame())
			InventoryMenuManager.saveLobbyMenu(bpPlayer);
		else
		{
			Game game = bpPlayer.getGame();
			
			bpPlayer.updateArmorMinutesLeft();
			game.onPlayerLeaveGame(bpPlayer);
		}
		
		bpPlayer.trySave();
		bpPlayer.reset();
		BPPlayer.removePlayer(bpPlayer);
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		bpPlayer.getScoreboardManager().unregister();
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		event.setLeaveMessage(null);
	}

/*	@EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		// \u00BB »
		// \u00AB «
		String motd = event.getMotd();
		String curMap = plugin.gm.getCurrentMap().getName();
		int[] teams = plugin.flm.getScore();
		String time = SBManager.formatTime(plugin.gm.mapSecondsLeft);
		motd = motd.replace("%TIME%", time);
		motd = motd.replace("%RED%", Integer.toString(teams[0]));
		motd = motd.replace("%BLUE%", Integer.toString(teams[1]));
		motd = motd.replace("%MAP%", curMap);
		event.setMotd(motd);
	}*/

	public int getWidth(String string)
	{
		String noColors = ChatColor.stripColor(string);
		int width = 0;
		String noSpaces = noColors.replace(" ", "");
		int spaces = 0;
		for (int i = 0; i < noColors.length(); i++)
			if (string.charAt(i) == ' ')
				spaces += spaceWidth;
		width += MinecraftFont.Font.getWidth(noSpaces);
		width += spaces;
		return width;
	}
}
