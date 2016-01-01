package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class GMCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(!player.hasPermission("Breakpoint.creative"))
		{
			player.sendMessage(MessageType.OTHER_NOPERMISSION.getTranslation().getValue());
			return true;
		}
		
		if(!bpPlayer.isInLobby())
		{
			player.sendMessage(ChatColor.RED + "Available in the lobby only.");
			return true;
		}
		
		GameMode gm = player.getGameMode();
		GameMode newGM = gm == GameMode.ADVENTURE ? GameMode.CREATIVE : GameMode.ADVENTURE;
		player.setGameMode(newGM);
		player.sendMessage(ChatColor.AQUA + "GameMode changed to " + newGM.name());
		
		return true;
	}
}
