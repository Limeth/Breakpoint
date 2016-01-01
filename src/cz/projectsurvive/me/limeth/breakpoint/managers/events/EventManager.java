package cz.projectsurvive.me.limeth.breakpoint.managers.events;

import org.bukkit.event.player.PlayerInteractEvent;

import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public interface EventManager
{
	public void showLobbyMenu(BPPlayer bpPlayer);
	public void onPlayerInteract(PlayerInteractEvent event);
	public void save();
}
