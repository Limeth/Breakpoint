package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class LobbyInfoManager
{
	public static void startLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {

			@Override
			public void run()
			{
				sendMessage();
			}
			
		}, 20L * 60, 20L * 60);
	}
	
	private static void sendMessage()
	{
		Configuration config = Breakpoint.getBreakpointConfig();
		List<String> messages = config.getLobbyMessages();
		
		if(messages.isEmpty())
			return;
		
		String message = MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + " " + messages.get(new Random().nextInt(messages.size()));
		
		for(BPPlayer bpPlayer : BPPlayer.onlinePlayers)
			if(bpPlayer.isInLobby())
			{
				Player player = bpPlayer.getPlayer();
				
				player.sendMessage(message);
			}
	}
}
