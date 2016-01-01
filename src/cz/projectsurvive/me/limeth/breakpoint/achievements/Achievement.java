package cz.projectsurvive.me.limeth.breakpoint.achievements;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class Achievement
{
	private final AchievementType type;
	private boolean achieved;
	
	public Achievement(AchievementType type, boolean achieved)
	{
		this.type = type;
		this.achieved = achieved;
	}
	
	public String getName()
	{
		return type.name();
	}

	public boolean isAchieved()
	{
		return achieved;
	}

	public void setAchieved(boolean achieved)
	{
		this.achieved = achieved;
	}

	public AchievementType getType()
	{
		return type;
	}
	
	//{{STATIC
	
	public static List<Achievement> loadPlayerAchievements(Storage storage) throws Exception
	{
		List<Achievement> achievements = new ArrayList<Achievement>();
		
		for (AchievementType at : AchievementType.values())
		{
			String acName = at.name();
			if (acName.startsWith("CHARACTER"))
				for (CharacterType ct : CharacterType.values())
				{
					String ctName = ct.name();
					String name = acName + "_" + ctName;
					Boolean hasA = storage.get(Boolean.class, "achievements." + name, false);
					CharacterAchievement achievement = new CharacterAchievement(at, ct, hasA);
					
					achievements.add(achievement);
				}
			else
			{
				Boolean hasA = storage.get(Boolean.class, "achievements." + acName, false);
				Achievement achievement = new Achievement(at, hasA);
				
				achievements.add(achievement);
			}
		}
		
		return achievements;
	}
	
	public static void savePlayerAchievements(Storage storage, List<Achievement> achievements)
	{
		for(Achievement ac : achievements)
		{
			boolean value = ac.isAchieved();
			String name = ac.getName();
			
			storage.put("achievements." + name, value);
		}
	}

	public static List<Column> getRequiredMySQLColumns()
	{
		List<Column> list = new LinkedList<Column>();
		
		for (AchievementType at : AchievementType.values())
		{
			String acName = at.name();
			if (acName.startsWith("CHARACTER"))
				for (CharacterType ct : CharacterType.values())
				{
					String ctName = ct.name();
					String name = acName + "_" + ctName;
					Column column = new Column("achievements." + name, ColumnType.BOOLEAN);
					
					list.add(column);
				}
			else
			{
				Column column = new Column("achievements." + acName, ColumnType.BOOLEAN);
				
				list.add(column);
			}
		}
		
		return list;
	}
	
	//{{INVENTORY
	public static final String MENU_TITLE = "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "BREAKPOINT " + ChatColor.RESET + "ODZNAKY";
	
	public static void onMenuClick(InventoryClickEvent event, BPPlayer bpPlayer)
	{
		event.setCancelled(true);
		int slotId = event.getRawSlot();
		
		if(slotId == 9 || slotId == 17)
		{
			int curPage = bpPlayer.getAchievementViewPage();
			BPPlayer bpTarget = bpPlayer.getAchievementViewTarget();
			Player player = bpPlayer.getPlayer();
			Inventory inv = event.getInventory();
			
			if(bpTarget == null)
			{
				player.closeInventory();
				return;
			}
			if(slotId == 9 && curPage > 0)
			{
				int i = curPage - 1;
				bpPlayer.setAchievementViewPage(i);
				equipMenu(bpPlayer, inv, i);
			}
			else if(slotId == 17 && curPage < (getPageAmount() - 1))
			{
				int i = curPage + 1;
				bpPlayer.setAchievementViewPage(i);
				equipMenu(bpPlayer, inv, i);
			}
			
			InventoryMenuManager.updateInventoryDelayed(player);
		}
	}
	
	public static InventoryView showAchievementMenu(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		BPPlayer bpTarget = bpPlayer.getAchievementViewTarget();
		int curPage = bpPlayer.getAchievementViewPage();
		Inventory inv = Bukkit.getServer().createInventory(player, 27, MENU_TITLE);
		
		equipMenu(bpTarget, inv, curPage);
		player.closeInventory();
		
		return player.openInventory(inv);
	}

	public static void equipMenu(BPPlayer bpTarget, Inventory inv, int page)
	{
		int pageAmount = getPageAmount();
		inv.setItem(0, InventoryMenuManager.getBorder());
		inv.setItem(8, InventoryMenuManager.getBorder());
		inv.setItem(18, InventoryMenuManager.getBorder());
		inv.setItem(26, InventoryMenuManager.getBorder());
		inv.setItem(9, (page > 0 ? getButton(0) : InventoryMenuManager.getBorder()));
		inv.setItem(17, (page < (pageAmount - 1) ? getButton(1) : InventoryMenuManager.getBorder()));
		showAchievements(bpTarget, inv, page);
	}

	public static void showAchievements(BPPlayer bpTarget, Inventory inv, int page)
	{
		ArrayList<ItemStack> list = getAchievementItemStacks(bpTarget);
		ArrayList<ItemStack> used = new ArrayList<ItemStack>();
		
		for (int i = 0; i < 21 && (i + page * 21) < list.size(); i++)
			used.add(list.get(i + page * 21));
		
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 7; j++)
			{
				int slot = 1 + (i * 7) + j + i * 2;
				int id = i * 7 + j;
				if (used.size() > i * 7 + j)
					inv.setItem(slot, used.get(id));
				else
					inv.setItem(slot, null);
			}
	}

	public static ItemStack getButton(int i)
	{
		ItemStack is = new ItemStack(Material.THIN_GLASS);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.DARK_AQUA + (i == 0 ? MessageType.MENU_ACHIEVEMENTS_PREVIOUSPAGE.getTranslation().getValue() : MessageType.MENU_ACHIEVEMENTS_NEXTPAGE.getTranslation().getValue()));
		is.setItemMeta(im);
		return is;
	}

	public static int getPageAmount()
	{
		int i = 0;
		while (i * 3 * 7 < (AchievementType.values().length - 10 + (10 * CharacterType.values().length)))
			i++;
		return i;
	}

	public static ArrayList<ItemStack> getAchievementItemStacks(BPPlayer bpPlayer)
	{
		Object[] map = getAchievementsInOrder(bpPlayer);
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();
		
		for (Object raw : map)
		{
			Object[] obj = (Object[]) raw;
			AchievementType at = (AchievementType) obj[0];
			CharacterType ct = (CharacterType) obj[1];
			Boolean achieved = (Boolean) obj[2];
			ItemStack icon;
			String name;
			String description = "";
			
			if (ct != null)
			{
				icon = at.getIcon(ct);
				name = at.getName(ct);
				if (achieved)
					description = at.getDescription(ct);
			}
			else
			{
				icon = at.getIcon();
				AchievementTranslation att = at.getTranslation();
				name = att.getName();
				
				if (achieved)
					description = att.getDesc();
			}
			
			if (achieved)
				icon.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
			
			if (name.equals(""))
				name = at.name();
			
			ItemMeta im = icon.getItemMeta();
			im.setDisplayName(ChatColor.GOLD + name);
			
			if (description.length() > 0)
				im.setLore(breakUpText(description));
			
			icon.setItemMeta(im);
			list.add(icon);
		}
		
		return list;
	}

	public static Object[] getAchievementsInOrder(BPPlayer bpPlayer)
	{
		AchievementType[] order = AchievementType.values();
		Object[] map = new Object[order.length - 10 + (10 * CharacterType.values().length)];
		List<Object[]> achieved = new ArrayList<Object[]>();
		List<Object[]> notAchieved = new ArrayList<Object[]>();
		for (int i = 0; i < order.length; i++)
		{
			AchievementType at = order[i];
			String atName = at.name();
			if (atName.startsWith("CHARACTER"))
				for (CharacterType ct : CharacterType.values())
				{
					Object[] key = new Object[] { at, ct };
					boolean value = bpPlayer.hasAchievement(at, ct);
					
					if (value)
						achieved.add(key);
					else
						notAchieved.add(key);
				}
			else
			{
				Object[] key = new Object[] { at, null };
				boolean value = bpPlayer.hasAchievement(at);
				
				if (value)
					achieved.add(key);
				else
					notAchieved.add(key);
			}
		}
		int i;
		for (i = 0; i < achieved.size(); i++)
		{
			Object[] obj = achieved.get(i);
			map[i] = new Object[] { obj[0], obj[1], true };
		}
		for (int j = 0; j < notAchieved.size(); j++)
		{
			Object[] obj = notAchieved.get(j);
			map[i + j] = new Object[] { obj[0], obj[1], false };
		}
		return map;
	}

	public static List<String> breakUpText(String s)
	{
		String[] ss = s.split(" ");
		List<String> result = new ArrayList<String>();
		int k = 0;
		int j = 0;
		result.add(j, "");
		for (int i = 0; i < ss.length; i++)
		{
			result.set(j, result.get(j) + " " + ss[i]);
			if (k >= 3)
			{
				k = 0;
				result.set(j, ChatColor.YELLOW + result.get(j).substring(1));
				j++;
				result.add(j, "");
			}
			k++;
		}
		if (k > 0 && result.get(j).length() > 0)
			result.set(j, ChatColor.YELLOW + result.get(j).substring(1));
		return result;
	}

	public static int getAchievementAmount()
	{
		return AchievementType.values().length - 10 + (10 * CharacterType.values().length);
	}

	public static int getUnlockedAchievementAmount(BPPlayer bpPlayer)
	{
		int i = 0;
		for (AchievementType at : AchievementType.values())
		{
			String atName = at.name();
			if (atName.startsWith("CHARACTER"))
				for (CharacterType ct : CharacterType.values())
				{
					if(bpPlayer.hasAchievement(at, ct))
						i++;
				}
			else if(bpPlayer.hasAchievement(at))
				i++;
		}
		return i;
	}

	public static int getUnlockedAchievementAmount(String offlinePlayer)
	{
		File file = new File("plugins/Breakpoint/players/" + offlinePlayer + ".yml");
		if (!file.exists())
			return -1;
		YamlConfiguration yamlData = YamlConfiguration.loadConfiguration(file);
		int amount = 0;
		for (AchievementType ac : AchievementType.values())
		{
			String acName = ac.name();
			if (acName.startsWith("CHARACTER"))
				for (CharacterType ct : CharacterType.values())
				{
					String ctName = ct.name();
					String name = acName + "_" + ctName;
					Boolean hasA = yamlData.getBoolean("achievements." + name, false);
					if (hasA)
						amount++;
				}
			else
			{
				Boolean hasA = yamlData.getBoolean("achievements." + acName, false);
				if (hasA)
					amount++;
			}
		}
		return amount;
	}

	// Inventory end
	public static void checkKills(BPPlayer bpPlayer)
	{
		int kills = bpPlayer.getStatistics().getKills();
		
		if (kills >= 1)
			bpPlayer.checkAchievement(AchievementType.KILLS_1);
		else
			return;
		
		if (kills >= 50)
			bpPlayer.checkAchievement(AchievementType.KILLS_50);
		else
			return;
		
		if (kills >= 250)
			bpPlayer.checkAchievement(AchievementType.KILLS_250);
		else
			return;
		
		if (kills >= 1000)
			bpPlayer.checkAchievement(AchievementType.KILLS_1000);
		else
			return;
		
		if (kills >= 5000)
			bpPlayer.checkAchievement(AchievementType.KILLS_5000);
		else
			return;
		
		if (kills >= 25000)
			bpPlayer.checkAchievement(AchievementType.KILLS_25000);
		else
			return;
	}

	public static void checkCharacterKills(BPPlayer bpPlayer, CharacterType ct)
	{
		int ctKills = bpPlayer.getStatistics().getKills(ct);
		
		if (ctKills >= 10)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_10, ct);
		else
			return;
		
		if (ctKills >= 25)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_25, ct);
		else
			return;
		
		if (ctKills >= 50)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_50, ct);
		else
			return;
		
		if (ctKills >= 100)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_100, ct);
		else
			return;
		
		if (ctKills >= 250)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_250, ct);
		else
			return;
		
		if (ctKills >= 500)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_500, ct);
		else
			return;
		
		if (ctKills >= 1000)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_1000, ct);
		else
			return;
		
		if (ctKills >= 2500)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_2500, ct);
		else
			return;
		
		if (ctKills >= 5000)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_5000, ct);
		else
			return;
		
		if (ctKills >= 10000)
			bpPlayer.checkAchievement(AchievementType.CHARACTER_KILLS_10000, ct);
		else
			return;
	}

	public static void checkBought(BPPlayer bpPlayer)
	{
		int bought = bpPlayer.getStatistics().getBought();
		
		if (bought >= 1)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_1);
		else
			return;
		
		if (bought >= 5)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_5);
		else
			return;
		
		if (bought >= 25)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_25);
		else
			return;
		
		if (bought >= 125)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_125);
		else
			return;
		
		if (bought >= 500)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_500);
		else
			return;
		
		if (bought >= 2500)
			bpPlayer.checkAchievement(AchievementType.BOUGHT_2500);
		else
			return;
	}

	public static void checkFlagTakes(BPPlayer bpPlayer)
	{
		int flagTakes = bpPlayer.getStatistics().getFlagTakes();
		
		if (flagTakes < 0)
			return;
		
		if (flagTakes >= 1)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_1);
		else
			return;
		
		if (flagTakes >= 25)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_25);
		else
			return;
		
		if (flagTakes >= 100)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_100);
		else
			return;
		
		if (flagTakes >= 250)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_250);
		else
			return;
		
		if (flagTakes >= 500)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_500);
		else
			return;
		
		if (flagTakes >= 1000)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_1000);
		else
			return;
		
		if (flagTakes >= 5000)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_5000);
		else
			return;
		
		if (flagTakes >= 10000)
			bpPlayer.checkAchievement(AchievementType.FLAG_TAKE_10000);
		else
			return;
	}

	public static void checkFlagCaptures(BPPlayer bpPlayer)
	{
		int flagCaptures = bpPlayer.getStatistics().getFlagCaptures();
		
		if (flagCaptures < 0)
			return;
		
		if (flagCaptures >= 1)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_1);
		else
			return;
		
		if (flagCaptures >= 25)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_25);
		else
			return;
		
		if (flagCaptures >= 100)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_100);
		else
			return;
		
		if (flagCaptures >= 250)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_250);
		else
			return;
		
		if (flagCaptures >= 500)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_500);
		else
			return;
		
		if (flagCaptures >= 1000)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_1000);
		else
			return;
		
		if (flagCaptures >= 5000)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_5000);
		else
			return;
		
		if (flagCaptures >= 10000)
			bpPlayer.checkAchievement(AchievementType.FLAG_CAPTURE_10000);
		else
			return;
	}
	//}}
	//}}
}
