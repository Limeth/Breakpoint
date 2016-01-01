package cz.projectsurvive.me.limeth.breakpoint.listeners;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPArmor;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.perks.Perk;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.Settings;

public class PlayerInventoryListener implements Listener
{
	Breakpoint plugin;

	public PlayerInventoryListener(Breakpoint p)
	{
		plugin = p;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		Inventory inv = event.getInventory();
		InventoryType it = inv.getType();
		if(it == InventoryType.CRAFTING)
		{
			Player player = (Player) event.getWhoClicked();
			BPPlayer bpPlayer = BPPlayer.get(player);
			SlotType st = event.getSlotType();
			int slotId = event.getRawSlot();
			if(bpPlayer.isInGame())
			{
				if(st == SlotType.ARMOR)
					event.setCancelled(true);
				else if(slotId >= 9 && slotId <= 35)
					onIngameMenuClick(event, bpPlayer);
			}
			else if(bpPlayer.isInLobby())
			{
				if(slotId >= 36 && slotId <= 44 || InventoryMenuManager.isLobbyBorder(slotId))
				{
					event.setCancelled(true);
					
					if(slotId == 43)
					{
						bpPlayer.setAchievementViewTarget(bpPlayer);
						bpPlayer.setAchievementViewPage(0);
						Achievement.showAchievementMenu(bpPlayer);
						InventoryMenuManager.updateInventoryDelayed(player);
						return;
					}
					else if(slotId == 42)
						Settings.showSettingsMenu(bpPlayer);
					else if(slotId == 41)
						Perk.showPerkMenu(bpPlayer);
					
					InventoryMenuManager.updateInventoryDelayed(player);
				}
				else if(slotId == 22)
				{
					ItemStack cursor = player.getItemOnCursor();
					if(cursor != null)
					{
						Material mat = cursor.getType();
						if(mat != Material.AIR)
						{
							String name = mat.name().replaceAll("_", " ").toLowerCase();
							if(cursor.hasItemMeta())
							{
								ItemMeta im = cursor.getItemMeta();
								if(im.hasDisplayName())
									name = im.getDisplayName();
							}
							player.setItemOnCursor(null);
							InventoryMenuManager.saveLobbyMenu(bpPlayer);
							player.sendMessage(MessageType.MENU_TRASH_USE.getTranslation().getValue(name));
						}
					}
					event.setCancelled(true);
					InventoryMenuManager.updateInventoryDelayed(player);
				}
				else
				{
					if(!player.hasPermission("Breakpoint.vip"))
						if(InventoryMenuManager.isVipSlot(slotId))
						{
							event.setCancelled(true);
							InventoryMenuManager.updateInventoryDelayed(player);
							return;
						}
					if(event.isShiftClick())
					{
						if(st != SlotType.ARMOR)
						{
							PlayerInventory pi = player.getInventory();
							ItemStack is = event.getCurrentItem();
							BPEquipment equipment = BPEquipment.parse(is);
							if(equipment == null)
								return;
							ItemStack[] armor = pi.getArmorContents();
							if(equipment instanceof BPArmor)
							{
								Material mat = is.getType();
								int armorId = BPArmor.getTypeId(mat);
								if(armorId < 0)
									return;
								if(armor[armorId].getTypeId() != 0)
									event.setCancelled(true);
							}
							else if(armor[3].getTypeId() != 0)
								event.setCancelled(true);
							else
							{
								event.setCancelled(true);
								armor[3] = is.clone();
								event.setCurrentItem(null);
								pi.setArmorContents(armor);
							}
						}
						else if(!bpPlayer.hasSpaceInLobbyInventory())
						{
							event.setCancelled(true);
							return;
						}
					}
					else if(st == SlotType.ARMOR)
					{
						int slot = event.getRawSlot();
						if(slot == 5)
						{
							ItemStack cursor = event.getCursor();
							if(cursor != null)
							{
								ItemStack clicked = event.getCurrentItem();
								event.setCursor(clicked);
								event.setCurrentItem(cursor);
								event.setCancelled(true);
							}
						}
					}
				}
			}
			else
				event.setCancelled(true);
		}
		else if(it == InventoryType.CHEST)
		{
			Player player = (Player) event.getWhoClicked();
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			onChestMenuClick(event, bpPlayer);
		}
	}
	
	public void onChestMenuClick(InventoryClickEvent event, BPPlayer bpPlayer)
	{
		Inventory inv = event.getInventory();
		
		if(inv == null)
			return;
		
		String title = inv.getTitle();
		
		if(title.equals(Settings.MENU_TITLE))
			Settings.onMenuClick(event, bpPlayer);
		else if(title.equals(Achievement.MENU_TITLE))
			Achievement.onMenuClick(event, bpPlayer);
		else if(title.equals(Perk.MENU_TITLE))
			Perk.onMenuClick(event, bpPlayer);
	}

	public void onIngameMenuClick(InventoryClickEvent event, BPPlayer bpPlayer)
	{
		event.setCancelled(true);
		Inventory inv = event.getInventory();
		InventoryHolder holder = inv.getHolder();
		if(!(holder instanceof Player))
			return;
		Player player = (Player) holder;
		ItemStack item = event.getCurrentItem();
		Material mat = item.getType();
		if(mat == Material.WOOD_DOOR)
		{
			bpPlayer.setSingleTeleportLocation(null);
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_LOBBY_USE.getTranslation().getValue());
		}
		else if(mat == Material.ITEM_FRAME)
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			bpPlayer.setSingleTeleportLocation(config.getShopLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_STORE_USE.getTranslation().getValue());
		}
		else if(mat == Material.SKULL_ITEM)
			player.setHealth(0.0);
		else if(mat == Material.NETHER_STAR)
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			bpPlayer.setSingleTeleportLocation(config.getVipInfoLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_VIPINFO_USE.getTranslation().getValue());
		}
		else if(mat == Material.EMERALD)
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			bpPlayer.setSingleTeleportLocation(config.getMoneyInfoLocation().clone());
			bpPlayer.setLeaveAfterDeath(true);
			player.sendMessage(MessageType.MENU_EMERALDS_USE.getTranslation().getValue());
		}
		else if(mat == Material.MONSTER_EGG)
		{
			CharacterType ct = CharacterType.getByMonsterEggId(item.getDurability());
			if(ct != null)
			{
				String name = ct.getProperName();
				if(ct.requiresVIP() && !player.hasPermission("Breakpoint.vip"))
				{
					player.sendMessage(ChatColor.DARK_GRAY + "---");
					player.sendMessage(MessageType.LOBBY_CHARACTER_VIPSONLY.getTranslation().getValue(name));
					player.sendMessage(ChatColor.DARK_GRAY + "---");
					return;
				}
				bpPlayer.setQueueCharacter(ct);
				player.sendMessage(MessageType.LOBBY_CHARACTER_SELECTED.getTranslation().getValue(name));
				player.sendMessage(MessageType.OTHER_CHARACTERRESPAWNINFO.getTranslation().getValue());
			}
		}
		else if(mat == Material.REDSTONE_COMPARATOR)
			Settings.showSettingsMenu(bpPlayer);
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		if(!(player.hasPermission("Breakpoint.admin") && player.getGameMode() == GameMode.CREATIVE))
			event.setCancelled(true);
	}
}
