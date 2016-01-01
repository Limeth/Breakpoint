package cz.projectsurvive.me.limeth.breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class AfkManager
{
	public static int defSTK = 60 * 3; // Defaultní secondsToKick
	public static final String afkKickProtectionNode = "Breakpoint.afkProtection";
	Breakpoint plugin;
	int loopId;

	public AfkManager(Breakpoint p)
	{
		plugin = p;
	}

	public boolean executeAfk(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		
		if(player.hasPermission(afkKickProtectionNode))
			return false;
		
		Location pastLocation = bpPlayer.getAfkPastLocation();
		Location presentLocation = player.getLocation();
		
		if (pastLocation != null)
			if (pastLocation.equals(presentLocation))
			{
				int secondsToKick = bpPlayer.getAfkSecondsToKick();
				
				if (secondsToKick <= 0)
				{
					player.kickPlayer(MessageType.OTHER_AFKKICK.getTranslation().getValue());
					return true;
				}
				else
					bpPlayer.setAfkSecondsToKick(secondsToKick - 1);
				
				bpPlayer.setAfkPastLocation(presentLocation);
				return false;
			}
		
		bpPlayer.clearAfkSecondsToKick();
		return false;
	}

	public void startLoop()
	{
		loopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run()
			{
				tick();
			}
		}, 0, 20L);
	}

	public void tick()
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if(bpPlayer == null)
				continue;
			
			executeAfk(bpPlayer);
		}
		
/*		Iterator<BPPlayer> iterator = BPPlayer.onlinePlayers.iterator();
		
		while(iterator.hasNext())
		{
			BPPlayer bpPlayer = iterator.next();
			boolean kicked = executeAfk(bpPlayer);
			
			if(kicked)
				iterator.remove();
		}*/
	}
}
