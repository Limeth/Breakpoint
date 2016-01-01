package cz.projectsurvive.me.limeth.breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import ca.wacos.nametagedit.NametagAPI;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class NametagEditManager
{
	private static boolean loaded;
	
	public static boolean setLoaded()
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin("NametagEdit");
		
		if(plugin == null)
			return false;
		
		return loaded = plugin.isEnabled();
	}
	
	public static boolean isLoaded()
	{
		return loaded;
	}
	
	public static void updateNametag(BPPlayer bpPlayer)
	{
		if(!loaded)
			return;
		
		String playerName = bpPlayer.getName();
		String prefix = bpPlayer.getTagPrefix(true);
		String suffix = bpPlayer.getTagSuffix();
		
		NametagAPI.setNametagHard(playerName, prefix, suffix);
	}
}
