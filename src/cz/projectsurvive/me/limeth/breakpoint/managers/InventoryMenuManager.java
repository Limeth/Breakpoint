package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.maps.MapManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.statistics.PlayerStatistics;

public class InventoryMenuManager
{
	public static ItemStack[] ingameItems = new ItemStack[27];
	public static ItemStack[] lobbyItems = new ItemStack[9];
	public static ItemStack wikiBook = new ItemStack(Material.WRITTEN_BOOK);

	public static void initialize()
	{
		defineIngameDefaultItems();
		wikiBook.setItemMeta(FileManager.loadWikiBook((BookMeta) wikiBook.getItemMeta()));
	}
	
	public static void defineIngameDefaultItems()
	{
		// Tlacitka
		ingameItems[0] = new ItemStack(Material.WOOD_DOOR);
		ItemMeta im0 = ingameItems[0].getItemMeta();
		im0.setDisplayName(MessageType.MENU_LOBBY_NAME.getTranslation().getValue());
		List<String> lore0 = MessageType.MENU_LOBBY_DESC.getTranslation().getValues();
		im0.setLore(lore0);
		ingameItems[0].setItemMeta(im0);
		
		ingameItems[1] = new ItemStack(Material.ITEM_FRAME);
		ItemMeta im1 = ingameItems[1].getItemMeta();
		im1.setDisplayName(MessageType.MENU_STORE_NAME.getTranslation().getValue());
		List<String> lore1 = MessageType.MENU_STORE_DESC.getTranslation().getValues();
		im1.setLore(lore1);
		ingameItems[1].setItemMeta(im1);
		
		ingameItems[2] = new ItemStack(Material.SKULL_ITEM);
		ItemMeta im2 = ingameItems[2].getItemMeta();
		im2.setDisplayName(MessageType.MENU_SUICIDE_NAME.getTranslation().getValue());
		List<String> lore2 = MessageType.MENU_SUICIDE_DESC.getTranslation().getValues();
		im2.setLore(lore2);
		ingameItems[2].setItemMeta(im2);
		
		ingameItems[7] = new ItemStack(Material.NETHER_STAR);
		ItemMeta im7 = ingameItems[7].getItemMeta();
		im7.setDisplayName(MessageType.MENU_VIPINFO_NAME.getTranslation().getValue());
		List<String> lore7 = MessageType.MENU_VIPINFO_DESC.getTranslation().getValues();
		im7.setLore(lore7);
		ingameItems[7].setItemMeta(im7);
		
		// Vejce
		ingameItems[18] = CharacterType.SWORDSMAN.getEgg();
		ingameItems[19] = CharacterType.ARCHER.getEgg();
		ingameItems[20] = CharacterType.KNIGHT.getEgg();
		ingameItems[21] = CharacterType.CHEMIST.getEgg();
		ingameItems[23] = CharacterType.NINJA.getEgg();
		ingameItems[24] = CharacterType.PYRO.getEgg();
		ingameItems[25] = CharacterType.HEAVY.getEgg();
		ingameItems[26] = CharacterType.CULTIST.getEgg();
		
		// Drevo
		for (int i = 0; i < 4; i++)
			ingameItems[9 + i] = getBorder();
		
		ingameItems[13] = getSettingsButton();
		
		for (int i = 0; i < 4; i++)
			ingameItems[14 + i] = getBorder();
	}

	public static ItemStack getBorder()
	{
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
		ItemMeta imSign = is.getItemMeta();
		imSign.setDisplayName(ChatColor.RESET + "");
		is.setItemMeta(imSign);
		return is;
	}

	@SuppressWarnings("deprecation")
	public static void showIngameMenu(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		PlayerInventory pi = player.getInventory();
		Game game = bpPlayer.getGame();
		
		for (int i = 0; i < 27; i++)
			pi.setItem(9 + i, ingameItems[i]);
		
		pi.setItem(15, getScoreMap(bpPlayer));
		pi.setItem(17, getMoneyEmerald(bpPlayer));
		game.showInGameMenu(bpPlayer);
		player.updateInventory();
	}

	@SuppressWarnings("deprecation")
	public static void showLobbyMenu(BPPlayer bpPlayer)
	{
		Breakpoint plugin = Breakpoint.getInstance();
		
		if(plugin.hasEvent())
			plugin.getEventManager().showLobbyMenu(bpPlayer);
		
		Player player = bpPlayer.getPlayer();
		PlayerInventory pi = player.getInventory();
		// Container
		// Border
		pi.setItem(13, getBorder());
		pi.setItem(22, getTrashbin());
		pi.setItem(31, getBorder());
		// Contents
		displayContents(bpPlayer, pi);
		// VIP Slots
		if (player.hasPermission("Breakpoint.vip"))
			displayVIPContents(bpPlayer, pi);
		else
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 4; j++)
					pi.setItem(14 + i * 9 + j, getVipSlot());
		// Hotbar
		pi.setItem(0, MapManager.getBreakpointMap(player));
		pi.setItem(1, MapManager.getPlayerGraphMap(player));
		pi.setItem(4, getWikiBook());
		pi.setItem(5, getPerkButton());
		pi.setItem(6, getSettingsButton());
		pi.setItem(7, getAchievementButton(bpPlayer));
		pi.setItem(8, getMoneyEmerald(bpPlayer));
		MapManager.updateLobbyMapsForPlayer(bpPlayer);
		player.updateInventory();
	}

	public static ItemStack getWikiBook()
	{
		ItemStack book = wikiBook.clone();
		BookMeta im = (BookMeta) book.getItemMeta();
		im.setDisplayName(MessageType.MENU_ENCYCLOPEDIA_NAME.getTranslation().getValue());
		im.setTitle(MessageType.MENU_ENCYCLOPEDIA_NAME.getTranslation().getValue());
		im.setAuthor(MessageType.MENU_ENCYCLOPEDIA_AUTHOR.getTranslation().getValue());
		book.setItemMeta(im);
		return book;
	}

	public static ItemStack getTrashbin()
	{
		ItemStack is = new ItemStack(Material.LAVA);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(MessageType.MENU_TRASH_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_TRASH_DESC.getTranslation().getValues();
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getVipSlot()
	{
		ItemStack is = new ItemStack(Material.IRON_FENCE);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(MessageType.MENU_VIPSLOT_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_VIPSLOT_DESC.getTranslation().getValues();
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	public static ItemStack getScoreMap(BPPlayer bpPlayer)
	{
		PlayerStatistics stats = bpPlayer.getStatistics();
		int kills = stats.getKills();
		int deaths = stats.getDeaths();
		String kdr = trimKdr(Double.toString((double) kills / (double) deaths));
		ItemStack map = new ItemStack(Material.EMPTY_MAP);
		ItemMeta im = map.getItemMeta();
		im.setDisplayName(MessageType.MENU_SCORE_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_SCORE_DESC.getTranslation().getValues(kills, deaths, kdr);
		im.setLore(lore);
		map.setItemMeta(im);
		return map;
	}

	public static ItemStack getMoneyEmerald(BPPlayer bpPlayer)
	{
		int money = bpPlayer.getMoney();
		ItemStack emerald = new ItemStack(Material.EMERALD);
		ItemMeta im = emerald.getItemMeta();
		im.setDisplayName(MessageType.MENU_EMERALDS_NAME.getTranslation().getValue(money));
		List<String> lore = MessageType.MENU_EMERALDS_DESC.getTranslation().getValues();
		im.setLore(lore);
		emerald.setItemMeta(im);
		return emerald;
	}

	public static String trimKdr(String kdr)
	{
		int j = 3;
		boolean foundDot = false;
		for (int i = 0; i < kdr.length(); i++)
		{
			char c = kdr.charAt(i);
			if (c == '.')
				foundDot = true;
			else
				if (foundDot)
				{
					j--;
					if (j <= 0)
						return kdr.substring(0, i);
				}
		}
		return kdr;
	}

	public static void displayContents(BPPlayer bpPlayer, PlayerInventory pi)
	{
		BPEquipment[] contents = bpPlayer.getLobbyInventory().getContents();
		ItemStack[] armor = new ItemStack[4];
		
		for (int i = 0; i < 4; i++)
		{
			BPEquipment bpEquipment = contents[i];
			if (bpEquipment != null)
			{
				armor[i] = bpEquipment.getItemStack();
				continue;
			}
			armor[i] = null;
		}
		pi.setArmorContents(armor);
		int k = 0;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
			{
				int slotId = 9 + i * 9 + j;
				BPEquipment bpEquipment = contents[k + 4];
				if (bpEquipment != null)
				{
					ItemStack is = bpEquipment.getItemStack();
					pi.setItem(slotId, is);
					k++;
					continue;
				}
				pi.setItem(slotId, null);
				k++;
			}
	}

	public static void displayVIPContents(BPPlayer bpPlayer, PlayerInventory pi)
	{
		BPEquipment[] contents = bpPlayer.getLobbyInventory().getContents();
		int k = 0;
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
			{
				int slotId = 14 + i * 9 + 3 - j;
				BPEquipment bpEquipment = contents[k + 16];
				if (bpEquipment != null)
				{
					ItemStack is = bpEquipment.getItemStack();
					pi.setItem(slotId, is);
					k++;
					continue;
				}
				pi.setItem(slotId, null);
				k++;
			}
	}

	public static void saveLobbyMenu(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		PlayerInventory pi = player.getInventory();
		BPEquipment[] contents = new BPEquipment[28];
		ItemStack[] armor = pi.getArmorContents();
		
		for (int i = 0; i < 4; i++)
		{
			ItemStack is = armor[i];
			if (is != null)
			{
				BPEquipment bpEquipment = BPEquipment.parse(is);
				if (bpEquipment != null)
				{
					contents[i] = bpEquipment;
					continue;
				}
			}
			contents[i] = null;
		}
		int k = 0;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
			{
				int slotId = 9 + i * 9 + j;
				ItemStack is = pi.getItem(slotId);
				if (is != null)
				{
					BPEquipment bpEquipment = BPEquipment.parse(is);
					if (bpEquipment != null)
					{
						contents[4 + k] = bpEquipment;
						k++;
						continue;
					}
				}
				k++;
			}
		if (player.hasPermission("Breakpoint.vip"))
		{
			k = 0;
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 4; j++)
				{
					int slotId = 14 + i * 9 + 3 - j;
					ItemStack is = pi.getItem(slotId);
					if (is != null)
					{
						BPEquipment bpEquipment = BPEquipment.parse(is);
						if (bpEquipment != null)
						{
							contents[16 + k] = bpEquipment;
							k++;
							continue;
						}
					}
					k++;
				}
		}
		
		bpPlayer.getLobbyInventory().setContents(contents);
	}

	public static boolean isLobbyBorder(int slotId)
	{
		return slotId == 13 || slotId == 31;
	}

	public static boolean isVipSlot(int slotId)
	{
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 4; j++)
				if (slotId == (14 + 9 * i + j))
					return true;
		return false;
	}
	
	public static ItemStack getToggleableButton(MessageType turnOn, MessageType turnOff, MessageType description, boolean enabled)
	{
		short dur;
		String name;
		
		if(enabled)
		{
			dur = 5;
			name = turnOff.getTranslation().getValue();
		}
		else
		{
			dur = 14;
			name = turnOn.getTranslation().getValue();
		}
		
		ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, dur);
		ItemMeta im = is.getItemMeta();
		List<String> lore = description.getTranslation().getValues();
		
		im.setDisplayName(name);
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}

	public static ItemStack getAchievementButton(BPPlayer player)
	{
		ItemStack button = new ItemStack(Material.NAME_TAG);
		ItemMeta im = button.getItemMeta();
		int got = Achievement.getUnlockedAchievementAmount(player);
		int amount = Achievement.getAchievementAmount();
		im.setDisplayName(MessageType.MENU_ACHIEVEMENTS_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_ACHIEVEMENTS_DESC.getTranslation().getValues(got, amount);
		im.setLore(lore);
		button.setItemMeta(im);
		return button;
	}

	public static ItemStack getSettingsButton()
	{
		ItemStack button = new ItemStack(Material.REDSTONE_COMPARATOR);
		ItemMeta im = button.getItemMeta();
		im.setDisplayName(MessageType.MENU_SETTINGS_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_SETTINGS_DESC.getTranslation().getValues();
		im.setLore(lore);
		button.setItemMeta(im);
		return button;
	}

	public static ItemStack getPerkButton()
	{
		ItemStack button = new ItemStack(Material.EXP_BOTTLE);
		ItemMeta im = button.getItemMeta();
		im.setDisplayName(MessageType.MENU_PERKS_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_PERKS_DESC.getTranslation().getValues();
		im.setLore(lore);
		button.setItemMeta(im);
		return button;
	}
	
	public static void updateInventoryDelayed(final Player player)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {

			@SuppressWarnings("deprecation")
			@Override
			public void run()
			{
				if(player != null)
					player.updateInventory();
				
				player.setItemOnCursor(player.getItemOnCursor());
			}
			
		});
	}
}
