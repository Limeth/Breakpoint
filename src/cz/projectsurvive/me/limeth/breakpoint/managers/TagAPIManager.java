package cz.projectsurvive.me.limeth.breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
//import org.kitteh.tag.TagAPI;

public class TagAPIManager
{
	private static boolean loaded;
	
	public static boolean setLoaded()
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin("TagAPI");
		
		if(plugin == null)
			return false;
		
		return loaded = plugin.isEnabled();
	}
	
	public static boolean isLoaded()
	{
		return loaded;
	}
	
/*	public static void refreshTag(Player player)
	{
		if(!loaded)
			return;
		
		TagAPI.refreshPlayer(player);
		for (Player other : Bukkit.getOnlinePlayers())
			if (!other.equals(player))
				TagAPI.refreshPlayer(other, player);
	}
	
	public static void refreshPlayer(Player player)
	{
		if(!loaded)
			return;
		
		TagAPI.refreshPlayer(player);
	}*/
}
