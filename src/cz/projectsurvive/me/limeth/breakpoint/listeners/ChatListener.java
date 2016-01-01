package cz.projectsurvive.me.limeth.breakpoint.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;

public class ChatListener implements Listener
{
	@EventHandler(ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		String message = event.getMessage();
		String lastMsg = bpPlayer.getLastMessage();
		
		if(message.equals(lastMsg))
		{
			event.setCancelled(true);
			return;
		}
		else
			bpPlayer.setLastMessage(message);
		
		Game game = bpPlayer.getGame();
		boolean cont = game != null ? game.getListener().onPlayerChat(event, bpPlayer) : true;
		
		if(!cont)
			return;
		
		if(message.charAt(0) == '#')
		{
			if(bpPlayer.isStaff())
			{
				event.setCancelled(true);
				String playerName = player.getName();
				bpPlayer.sendStaffMessage(message);
				Breakpoint.info("Staff chat: " + playerName + ": " + message);
				return;
			}
		}
		else if(message.charAt(0) == '&')
		{
			String playerName = player.getName();
			Clan clan = Clan.getByPlayer(playerName);
			if(clan != null)
			{
				event.setCancelled(true);
				bpPlayer.sendClanMessage(message);
				Breakpoint.info("Clan [" + clan.getName() + "] chat: " + playerName + ": " + message);
				return;
			}
		}
		String chatPrefix = bpPlayer.getChatPrefix();
		event.setFormat(chatPrefix + "%1$s" + ChatColor.GRAY + ": " + "%2$s");
	}

/*	@Deprecated
	public static String getPVPPrefix(Player player)
	{
		if(!player.hasPermission("Breakpoint.admin") && !player.hasPermission("Breakpoint.moderator") && !player.hasPermission("Breakpoint.helper"))
			if(player.hasPermission("Breakpoint.yt"))
				return prefixYT + " ";
			else if(player.hasPermission("Breakpoint.vip"))
				return prefixVIP + " ";
		return "";
	}*/
}
