package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;

public class HelpOPCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("Players only.");
			return false;
		}
		
		Player player = (Player) sender;
		
		if(args.length <= 0)
		{
			player.sendMessage(MessageType.COMMAND_HELPOP_USAGE.getTranslation().getValue(label));
			return false;
		}
		
		String question = args[0];
		
		for(int i = 1; i < args.length; i++)
			question += " " + args[i];
		
		boolean success = askHelpers(player, question);
		
		if(success)
		{
			player.sendMessage(MessageType.COMMAND_HELPOP_SUCCESS.getTranslation().getValue());
			return true;
		}
		else
		{
			player.sendMessage(MessageType.COMMAND_HELPOP_FAILURE.getTranslation().getValue());
			return false;
		}
	}
	
	private static boolean askHelpers(Player sender, String question)
	{
		int count = 0;
		
		for(Player player : Bukkit.getOnlinePlayers())
			if(player.hasPermission("Breakpoint.helper"))
			{
				player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + sender.getName() + ": " + question);
				count++;
			}
		
		return count > 0;
	}
}
