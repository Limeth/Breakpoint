package cz.projectsurvive.me.limeth.breakpoint.listeners;

import org.bukkit.event.Listener;
//import org.kitteh.tag.PlayerReceiveNameTagEvent;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;

public class TagAPIListener implements Listener
{
	Breakpoint plugin;
	//public static final boolean hideOpponents = false;

	public TagAPIListener(Breakpoint p)
	{
		plugin = p;
	}

/*	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event)
	{
		Player nPlayer = event.getNamedPlayer();
		BPPlayer bpNPlayer = BPPlayer.get(nPlayer);
		String tag = bpNPlayer.getTag();
		event.setTag(tag);
	}*/
}
