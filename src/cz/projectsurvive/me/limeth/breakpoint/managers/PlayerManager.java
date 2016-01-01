package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.server.v1_7_R1.Entity;
import net.minecraft.server.v1_7_R1.EnumClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_7_R1.PacketPlayOutEntityTeleport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.sound.BPSound;

public class PlayerManager
{
	public static void clearInventory(PlayerInventory pi)
	{
		pi.clear();
		pi.setArmorContents(new ItemStack[] { null, null, null, null });
	}

	public static void addRegenerationMatter(PlayerInventory pi, int amount)
	{
		ItemStack is = new ItemStack(Material.INK_SACK, 1, (short) 1);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(MessageType.OTHER_REGENMATTER_NAME.getTranslation().getValue());
		List<String> lore = new ArrayList<String>();
		lore.add(MessageType.OTHER_REGENMATTER_DESC.getTranslation().getValue());
		im.setLore(lore);
		is.setItemMeta(im);
		for (int i = 0; i < amount; i++)
			pi.addItem(is);
	}

	public static ItemStack decreaseItem(ItemStack item)
	{
		int amount = item.getAmount();
		if (amount > 1)
			item.setAmount(amount - 1);
		else
			item = null;
		return item;
	}

	public static ItemStack enchantItem(ItemStack item, Object[]... enchObjs)
	{
		for (Object[] enchObj : enchObjs)
			item.addUnsafeEnchantment((Enchantment) enchObj[0], (Integer) enchObj[1]);
		return item;
	}

	public static PlayerInventory enchantArmor(PlayerInventory inv, Object[]... enchObjs)
	{
		ItemStack[] armor = inv.getArmorContents();
		for (int i = 0; i < 4; i++)
			enchantItem(armor[i], enchObjs);
		return inv;
	}

	public static void executeMultikill(BPPlayer bpPlayer, Location loc, int delay)
	{
		if (bpPlayer.getLastTimeKilled() > System.currentTimeMillis() - delay * 1000)
		{
			int multikills = bpPlayer.getMultikills() + 1;
			bpPlayer.setMultikills(multikills);
			checkMultikill(bpPlayer, loc, multikills);
		}
		else
			bpPlayer.setMultikills(1);
		
		bpPlayer.setLastTimeKilled();
	}

	public static void executeKillingSpree(BPPlayer bpPlayer, Location loc)
	{
		int killedThisLife = bpPlayer.getKilledThisLife() + 1;
		bpPlayer.setKilledThisLife(killedThisLife);
		checkKillingSpree(bpPlayer, loc, killedThisLife);
	}

	public static void checkMultikill(BPPlayer bpPlayer, Location loc, int multikills)
	{
		if (multikills > 1)
		{
			String playerPVPName = bpPlayer.getPVPName();
			Game game = bpPlayer.getGame();
			
			switch (multikills)
			{
				case 2:
				{
					bpPlayer.checkAchievement(AchievementType.DOUBLEKILL);
					game.broadcastCombo(playerPVPName, loc, ChatColor.YELLOW, "=", "Multi-Kill");
					SoundManager.playSoundAt(BPSound.MULTI_KILL, loc);
					break;
				}
				case 3:
				{
					bpPlayer.checkAchievement(AchievementType.MULTIKILL);
					game.broadcastCombo(playerPVPName, loc, ChatColor.GOLD, "==", "Mega-Kill");
					SoundManager.playSoundAt(BPSound.MEGA_KILL, loc);
					break;
				}
				case 4:
				{
					bpPlayer.checkAchievement(AchievementType.MEGAKILL);
					game.broadcastCombo(playerPVPName, loc, ChatColor.RED, "===", "Ultra-Kill");
					SoundManager.playSoundAt(BPSound.ULTRA_KILL, loc);
					break;
				}
				case 5:
				{
					bpPlayer.checkAchievement(AchievementType.ULTRAKILL);
					game.broadcastCombo(playerPVPName, loc, ChatColor.DARK_RED, "====", "Monster-Kill");
					SoundManager.playSoundAt(BPSound.MONSTER_KILL, loc);
					break;
				}
				default:
				{
					int amount = multikills - 1;
					String decor = "";
					
					for(int i = 0; i < amount; i++)
						decor += "=";
					
					bpPlayer.checkAchievement(AchievementType.MONSTERKILL);
					game.broadcastCombo(playerPVPName, loc, ChatColor.DARK_PURPLE, decor, "Ludacriss-Kill");
					SoundManager.playSoundAt(BPSound.LUDACRISS_KILL, loc);
					break;
				}
			}
		}
	}

	public static void checkKillingSpree(BPPlayer bpPlayer, Location loc, int kills)
	{
		int step = 3;
		
		if (kills > 1 && kills % step == 0)
		{
			String playerPVPName = bpPlayer.getPVPName();
			Game game = bpPlayer.getGame();
			
			switch (kills)
			{
				case 3:
				{
					bpPlayer.checkAchievement(AchievementType.KILLING_SPREE);
					game.broadcastCombo(playerPVPName, loc, ChatColor.YELLOW, "×", "Killing spree");
					SoundManager.playSoundAt(BPSound.KILLING_SPREE, loc);
					break;
				}
				case 6:
				{
					bpPlayer.checkAchievement(AchievementType.RAMPAGE);
					game.broadcastCombo(playerPVPName, loc, ChatColor.GOLD, "××", "Rampage");
					SoundManager.playSoundAt(BPSound.RAMPAGE, loc);
					break;
				}
				case 9:
				{
					bpPlayer.checkAchievement(AchievementType.DOMINATING);
					game.broadcastCombo(playerPVPName, loc, ChatColor.RED, "×××", "Dominating");
					SoundManager.playSoundAt(BPSound.DOMINATING, loc);
					break;
				}
				case 12:
				{
					bpPlayer.checkAchievement(AchievementType.UNSTOPPABLE);
					game.broadcastCombo(playerPVPName, loc, ChatColor.DARK_RED, "××××", "Unstoppable");
					SoundManager.playSoundAt(BPSound.UNSTOPPABLE, loc);
					break;
				}
				case 15:
				{
					bpPlayer.checkAchievement(AchievementType.GODLIKE);
					game.broadcastCombo(playerPVPName, loc, ChatColor.DARK_PURPLE, "×××××", "Godlike");
					SoundManager.playSoundAt(BPSound.GODLIKE, loc);
					break;
				}
				default:
				{
					int amount = kills / step;
					String decor = "";
					
					for(int i = 0; i < amount; i++)
						decor += "×";
					
					bpPlayer.checkAchievement(AchievementType.MASSACRE);
					game.broadcastCombo(playerPVPName, loc, ChatColor.LIGHT_PURPLE, decor, "Massacre");
					SoundManager.playSoundAt(BPSound.MASSACRE, loc);
					break;
				}
			}
		}
	}

	public static Firework spawnRandomlyColoredFirework(Location loc)
	{
		return spawnColoredFirework(loc, Color.fromRGB(new Random().nextInt(0xFFFFFF)));
	}

	public static Firework spawnFirework(Location loc)
	{
		Firework f = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		return f;
	}

	public static Firework spawnColoredFirework(Location loc, Color color)
	{
		Firework firework = spawnFirework(loc);
		FireworkMeta meta = firework.getFireworkMeta();
		List<Color> colours = new ArrayList<Color>();
		colours.add(color);
		meta.setPower(0);
		meta.addEffect(FireworkEffect.builder().flicker(true).withColor(colours).withFade(Color.WHITE).with(FireworkEffect.Type.BURST).trail(false).build());
		firework.setFireworkMeta(meta);
		return firework;
	}

	public static void clearHotBar(PlayerInventory inv)
	{
		for (int i = 0; i < 9; i++)
			inv.setItem(i, null);
	}

	public static void clearHotBars()
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if (bpPlayer.isInGame())
				clearHotBar(player.getInventory());
		}
	}

	public static void respawnWithDelay(final Player player)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				respawnInstantlyPacket(player);
			}
		});
	}
	
	public static void updatePosDelayed(final Player player)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				updatePos(player);
			}
		});
	}
	
	public static void updatePos(Player player)
	{
		Entity entity = ((CraftPlayer) player).getHandle();
		
		PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(entity);
		
		for(Player online : Bukkit.getOnlinePlayers())
			if(!online.equals(player))
				((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
	}

	public static void respawnInstantlyPacket(Player player)
	{
		PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
		((CraftPlayer) player).getHandle().playerConnection.a(packet);
	}

	public void respawnInstantly(Player player)
	{
		//respawnInstantlyPacket(player);
		Breakpoint plugin = Breakpoint.getInstance();
		PacketContainer pContainer = plugin.prm.createPacket(PacketType.Play.Server.RESPAWN);
		
		try
		{
			plugin.prm.sendServerPacket(player, pContainer);
		}
		catch (InvocationTargetException e)
		{
			Breakpoint.warn("Error when trying to instantly respawn player '" + player.getName() + "'!");
			e.printStackTrace();
		}
	}
}
