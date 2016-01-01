package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.VIPManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class FlyCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		
		if(!player.hasPermission("Breakpoint.VIP"))
		{
			player.sendMessage(MessageType.COMMAND_FLY_VIPSONLY.getTranslation().getValue());
			return true;
		}
		
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(!bpPlayer.isInLobby())
		{
			player.sendMessage(MessageType.COMMAND_FLY_NOTLOBBY.getTranslation().getValue());
			return true;
		}
		
		if(VIPManager.isFarFromSpawnToUseFly(player))
		{
			player.sendMessage(MessageType.COMMAND_FLY_TOOFAR.getTranslation().getValue());
			return true;
		}
		
		boolean value = !player.getAllowFlight();
		player.setAllowFlight(value);
		
		if(value)
			player.sendMessage(MessageType.COMMAND_FLY_ENABLED.getTranslation().getValue());
		else
			player.sendMessage(MessageType.COMMAND_FLY_DISABLED.getTranslation().getValue());
		
		return true;
	}
}
