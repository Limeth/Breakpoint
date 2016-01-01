package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.StatisticsManager;

public class RankCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(args.length <= 0)
			sender.sendMessage(getCommandInfo(MessageType.COMMAND_RANK_PATH.getTranslation().getValue(), MessageType.COMMAND_RANK_DESC.getTranslation().getValue(), ChatColor.AQUA));
		else
			StatisticsManager.showStatistics(sender, args[0]);
		
		return true;
	}
	
	private static String getCommandInfo(String command, String info, ChatColor color)
	{
		return color + "/" + command + ChatColor.GRAY + " - " + info;
	}
}
