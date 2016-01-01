package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class AchievementsCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(args.length <= 0)
			sender.sendMessage(getCommandInfo(MessageType.COMMAND_ACHIEVEMENTS_PATH.getTranslation().getValue(), MessageType.COMMAND_ACHIEVEMENTS_DESC.getTranslation().getValue(), ChatColor.AQUA));
		else
		{
			String targetName = args[0];
			Player target = Bukkit.getPlayer(targetName);
			BPPlayer bpTarget = BPPlayer.get(target);
			
			if(bpTarget != null)
			{
				int page = 0;
				
				if(args.length >= 2)
					try
					{
						page = Integer.parseInt(args[1]);
					}
					catch(NumberFormatException e)
					{
					}
				
				bpPlayer.setAchievementViewTarget(bpTarget);
				bpPlayer.setAchievementViewPage(page);
				Achievement.showAchievementMenu(bpPlayer);
			}
			else
				player.sendMessage(MessageType.COMMAND_ACHIEVEMENTS_EXE_PLAYEROFFLINE.getTranslation().getValue(targetName));
		}
		
		return true;
	}
	
	private static String getCommandInfo(String command, String info, ChatColor color)
	{
		return color + "/" + command + ChatColor.GRAY + " - " + info;
	}
}
