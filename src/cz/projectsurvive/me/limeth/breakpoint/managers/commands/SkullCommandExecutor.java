package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPSkull;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class SkullCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(args.length <= 0)
		{
			player.sendMessage(getCommandInfo(MessageType.COMMAND_SKULL_PATH.getTranslation().getValue(), MessageType.COMMAND_SKULL_DESC.getTranslation().getValue(), ChatColor.AQUA));
			player.sendMessage(MessageType.COMMAND_SKULL_WARNING.getTranslation().getValue());
		}
		else
		{
			if(!bpPlayer.isInLobby())
			{
				player.sendMessage(MessageType.COMMAND_SKULL_EXE_NOTINLOBBY.getTranslation().getValue());
				return true;
			}
			
			PlayerInventory inv = player.getInventory();
			ItemStack helmet = inv.getHelmet();
			
			if(helmet == null)
			{
				player.sendMessage(MessageType.COMMAND_SKULL_EXE_NOTRENAMEABLE.getTranslation().getValue());
				return true;
			}
			
			Material helmetMat = helmet.getType();
			
			if(helmetMat == Material.SKULL_ITEM)
			{
				BPSkull bpSkull = (BPSkull) BPEquipment.parse(helmet);
				
				if(bpSkull == null)
				{
					player.sendMessage(MessageType.OTHER_ERROR.getTranslation().getValue());
					return true;
				}
				
				if(bpSkull.canBeRenamed())
				{
					if(BPSkull.canBeRenamedTo(args[0]))
					{
						bpSkull.setName(args[0]);
						inv.setHelmet(bpSkull.getItemStack());
						
						player.sendMessage(MessageType.COMMAND_SKULL_EXE_SUCCESS.getTranslation().getValue(args[0]));
					}
					else
						player.sendMessage(MessageType.COMMAND_SKULL_EXE_NAMEBANNED.getTranslation().getValue(args[0]));
				}
				else
					player.sendMessage(MessageType.COMMAND_SKULL_EXE_NOTRENAMEABLE.getTranslation().getValue());
			}
			else
				player.sendMessage(MessageType.COMMAND_SKULL_EXE_NOTRENAMEABLE.getTranslation().getValue());
		}
		
		return true;
	}
	
	private static String getCommandInfo(String command, String info, ChatColor color)
	{
		return color + "/" + command + ChatColor.GRAY + " - " + info;
	}
}
