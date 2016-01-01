package cz.projectsurvive.me.limeth.breakpoint.players;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.Storage;
import me.limeth.storageAPI.StorageType;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.fijistudios.jordan.FruitSQL;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementTranslation;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;
import cz.projectsurvive.me.limeth.breakpoint.achievements.CharacterAchievement;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPArmor;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameProperties;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.AfkManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.ChatManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.DoubleMoneyManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SBManager;
import cz.projectsurvive.me.limeth.breakpoint.perks.Perk;
import cz.projectsurvive.me.limeth.breakpoint.perks.PerkType;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;
import cz.projectsurvive.me.limeth.breakpoint.statistics.PlayerStatistics;

public class BPPlayer
{
	//{{STATIC
	public static LinkedList<BPPlayer> onlinePlayers = new LinkedList<BPPlayer>();
	
	public static final BPPlayer get(String playerName, boolean create)
	{
		if(playerName == null)
			return null;
		
		for(BPPlayer bpPlayer : onlinePlayers)
			if(bpPlayer.getOfflinePlayer().getName().equals(playerName))
				return bpPlayer;
		
		if(create)
			try
			{
				return createPlayer(playerName);
			}
			catch(Exception e)
			{
				Breakpoint.warn("Error when creating player '" + playerName + "': " + e.getMessage());
				e.printStackTrace();
				return null;
			}
		else
		{
			Breakpoint.warn("Returning null BPPlayer for " + playerName);
			
			for(BPPlayer bpPlayer : onlinePlayers)
				Breakpoint.warn(bpPlayer.getOfflinePlayer().getName());
			
			return null;
		}
	}
	
	public static final BPPlayer get(String playerName)
	{
		return get(playerName, false);
	}
	
	public static final BPPlayer get(Player player)
	{
		return get(player.getName());
	}
	
	private static final BPPlayer createPlayer(String playerName) throws Exception
	{
		BPPlayer bpPlayer = load(playerName);
		
		onlinePlayers.add(bpPlayer);
		
		return bpPlayer;
	}
	
	public static final void removePlayer(BPPlayer bpPlayer)
	{
		onlinePlayers.remove(bpPlayer);
		
		Game game = bpPlayer.getGame();
		
		if(game != null)
			game.onPlayerLeaveGame(bpPlayer);
		
		bpPlayer.getScoreboardManager().unregister();
	}
	
	public static final void removePlayer(String playerName)
	{
		BPPlayer bpPlayer = BPPlayer.get(playerName);
		
		if(bpPlayer != null)
			removePlayer(bpPlayer);
	}
	
	public static final BPPlayer load(String playerName, StorageType storageType, FruitSQL mySQL) throws Exception
	{
		Configuration config = Breakpoint.getBreakpointConfig();
		Storage storage = Storage.load(storageType, playerName, getFolder(), mySQL, config.getMySQLTablePlayers());
		
		Settings settings = Settings.load(storage);
		LobbyInventory lobbyInventory = LobbyInventory.load(storage);
		PlayerStatistics stats = PlayerStatistics.loadPlayerStatistics(storage);
		List<Achievement> achievements = Achievement.loadPlayerAchievements(storage);
		List<Perk> perks = Perk.loadPlayerPerks(storage);
		
		long timeJoined = System.currentTimeMillis();
		Clan clan = Clan.getByPlayer(playerName);
		BPPlayer bpPlayer = new BPPlayer(playerName, settings, lobbyInventory, stats, achievements, perks, clan, timeJoined);
		
		return bpPlayer;
	}
	
	public static final BPPlayer load(String playerName) throws Exception
	{
		StorageType storageType = Breakpoint.getBreakpointConfig().getStorageType();
		FruitSQL mySQL = Breakpoint.getMySQL();
		
		return load(playerName, storageType, mySQL);
	}
	
	public static final void saveOnlinePlayersData() throws IOException
	{
		for(BPPlayer bpPlayer : onlinePlayers)
			bpPlayer.trySave();
	}
	
	public static File getFile(String playerName)
	{
		return new File(getFolder(), playerName + ".yml");
	}
	
	public static File getFolder()
	{
		return new File("plugins/Breakpoint/players/");
	}
	
	public static List<String> getPlayerNames(StorageType storageType)
	{
		if(storageType == null)
			return null;
		
		if(storageType == StorageType.YAML)
		{
			File folder = getFolder();
			File[] files = folder.listFiles();
			List<String> list = new LinkedList<String>();
			
			for(File file : files)
			{
				String name = FilenameUtils.removeExtension(file.getName());
				
				list.add(name);
			}
			
			return list;
		}
		else if(storageType == StorageType.MYSQL)
			return Storage.queryKeyColumn(Breakpoint.getMySQL(), Breakpoint.getBreakpointConfig().getMySQLTablePlayers(), String.class);
		else
			throw new NotImplementedException();
	}
	
	public static void updateTable(FruitSQL mySQL)
	{
		try
		{
			if(mySQL == null)
				return;
			
			List<Column> columns = getRequiredMySQLColumns();
			String tableName = Breakpoint.getBreakpointConfig().getMySQLTablePlayers();
			
			Storage.createTable(mySQL, tableName, "VARCHAR(16)", columns.toArray(new Column[columns.size()]));
			addMissingColumns(mySQL, tableName, columns);
		}
		catch(Exception e)
		{
			Breakpoint.warn("Error when updating the MySQL player table: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void addMissingColumns(FruitSQL mySQL, String table, List<Column> available) throws SQLException
	{
		List<Column> missing = getMissingColumns(mySQL, table, available);
		
		if(missing.size() <= 0)
			return;
		
		StringBuilder sb = new StringBuilder();
		
		for(Column column : missing)
			sb.append("ALTER TABLE `").append(table).append("` ADD ").append(column.toString()).append("; ");
		
		mySQL.execute(sb.toString());
	}
	
	private static List<Column> getMissingColumns(FruitSQL mySQL, String table, List<Column> available) throws SQLException
	{
		List<Column> missing = new LinkedList<Column>();
		ResultSetMetaData rsmd = mySQL.getMetaData(table);
		int size = rsmd.getColumnCount();
		
		for(Column column : available)
		{
			boolean isMissing = true;
			
			for(int i = 1; i <= size; i++)
			{
				String name = rsmd.getColumnName(i);
				
				if(name.equals(column.getName()))
				{
					isMissing = false;
					break;
				}
			}
			
			if(isMissing)
				missing.add(column);
		}
		
		return missing;
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		List<Column> columns = new LinkedList<Column>();
		
		columns.addAll(PlayerStatistics.getRequiredMySQLColumns());
		columns.addAll(Perk.getRequiredMySQLColumns());
		columns.addAll(Settings.getRequiredMySQLColumns());
		columns.addAll(Achievement.getRequiredMySQLColumns());
		columns.addAll(LobbyInventory.getRequiredMySQLColumns());
		
		return columns;
	}
	//}}
	
	//Main
	private final String name;
	private Settings settings;
	private final LobbyInventory lobbyInventory;
	private PlayerStatistics statistics;
	private List<Achievement> achievements;
	private List<Perk> perks;
	private Clan bpClan;
	private Game game;
	private GameProperties gameProperties;
	private final SBManager scoreboardManager;
	
	//Chat
	private String lastMessage;
	
	//Others
	private final HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	private final HashMap<BPPlayer, Long> lastTimeDamagedBy = new HashMap<BPPlayer, Long>();
	private boolean leaveAfterDeath = false;
	private ItemStack[] quickChatInventoryContents = new ItemStack[0];
	private long spawnTime = 0, lastTimeKilled = 0, timeJoined = 0;
	private int armorWoreSince = 0, achievementViewPage = 0, afkSecondsToKick = AfkManager.defSTK, multikills = 0, killedThisLife = 0;
	private BPPlayer achievementViewTarget = null, lastTimeKilledBy = null;
	private Location afkPastLocation = null, shopItemLocation = null, singleTeleportLocation = null;
	private CharacterType queueCharacter = null;
	
	private BPPlayer(String name, Settings settings, LobbyInventory lobbyInventory, PlayerStatistics statistics, List<Achievement> achievements, List<Perk> perks, Clan bpClan, long timeJoined)
	{
		this.settings = settings;
		this.lobbyInventory = lobbyInventory;
		this.name = name;
		this.statistics = statistics;
		this.achievements = achievements;
		this.perks = perks;
		this.bpClan = bpClan;
		this.timeJoined = timeJoined;
		
		if(isOnline())
			scoreboardManager = new SBManager(this);
		else
			scoreboardManager = null;
	}
	
//	public void saveToYAML() throws IOException
//	{
//		String playerName = offlinePlayer.getName();
//		
//		if(hasDefaultData())
//		{
//			System.out.println("Data of player " + playerName + " have not been saved because of default values.");
//			return;
//		}
//		
//		File file = getFile();
//		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
//		
//		settings.save(yml);
//		lobbyInventory.save(yml);
//		statistics.savePlayerStatistics(yml);
//		Achievement.savePlayerAchievements(yml, achievements);
//		Perk.savePlayerPerks(yml, perks);
//		yml.save(file);
//	}
	
	public void save(StorageType storageType, FruitSQL mySQL) throws IOException, SQLException
	{
		if(hasDefaultData())
		{
			System.out.println("Data of player " + name + " have not been saved because of default values.");
			return;
		}
		
		Storage storage = new Storage(name);
		File folder = getFolder();
		
		settings.save(storage);
		lobbyInventory.save(storage);
		statistics.savePlayerStatistics(storage);
		Achievement.savePlayerAchievements(storage, achievements);
		Perk.savePlayerPerks(storage, perks);
		
		storage.save(storageType, folder, mySQL, Breakpoint.getBreakpointConfig().getMySQLTablePlayers());
	}
	
	public void trySave(StorageType storageType, FruitSQL mySQL)
	{
		try
		{
			save(storageType, mySQL);
		}
		catch(SQLException | IOException e)
		{
			Breakpoint.warn("Error when saving player '" + getName() + "': " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void save() throws IOException, SQLException
	{
		save(Breakpoint.getBreakpointConfig().getStorageType(), Breakpoint.getMySQL());
	}
	
	public void trySave()
	{
		try
		{
			save();
		}
		catch(SQLException | IOException e)
		{
			Breakpoint.warn("Error when saving player '" + getName() + "': " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void deleteFile()
	{
		getFile().delete();
	}
	
	public File getFile()
	{
		return getFile(name);
	}
	
	public String getName()
	{
		return name;
	}
	
	public boolean hasDefaultData()
	{
		return settings.areDefault() && lobbyInventory.isEmpty() && statistics.areDefault();
	}
	
	public void leaveGame()
	{
		game.onPlayerLeaveGame(this);
		
		game = null;
		gameProperties = null;
	}
	
	public boolean isInLobby()
	{
		return game == null && gameProperties == null;
	}
	
	//{{CHAT
	public void setPlayerListName()
	{
		Player player = getPlayer();
		String tag = getTag(false, true);
		
		player.setPlayerListName(tag);
	}
	
	public String getTagPrefix(boolean brackets)
	{
		Player player = getPlayer();
		String gamePrefix = gameProperties != null ? gameProperties.getTagPrefix() : null;
		boolean vip = player.hasPermission("Breakpoint.vip");
		boolean staff = isStaff();
		boolean sponsor = staff ? false : player.hasPermission("Breakpoint.sponsor");
		boolean yt = staff ? false : player.hasPermission("Breakpoint.yt");
		
		return getTagPrefix(gamePrefix, vip, sponsor, yt, brackets);
	}
	
	public String getTagSuffix()
	{
		return ""; //max length 16
	}
	
	public String getTag(boolean brackets, boolean cut)
	{
		Player player = getPlayer();
		String playerName = player.getName();
		String prefix = getTagPrefix(brackets);
		String suffix = getTagSuffix();
		String tag = prefix + playerName + suffix;
		
		if(cut && tag.length() > 16)
			return tag.substring(0, 16);
		else
			return tag;
	}
	
	public String getTag()
	{
		return getTag(false, true);
	}
	
	private static String getTagPrefix(String gamePrefix, boolean vip, boolean sponsor, boolean yt, boolean brackets)
	{
		StringBuilder builder = new StringBuilder();
		String prefix = "";
		
		if(yt)
			prefix = (brackets ? brackets(ChatManager.tagPrefixYT) : ChatManager.tagPrefixYT) + ChatColor.WHITE + " ";
		else if(sponsor)
			prefix = (brackets ? brackets(ChatManager.tagPrefixSponsor) : ChatManager.tagPrefixSponsor) + ChatColor.WHITE + " ";
		else if(vip)
			prefix = (brackets ? brackets(ChatManager.tagPrefixVIP) : ChatManager.tagPrefixVIP) + ChatColor.WHITE + " ";
		
		builder.append(prefix).append(gamePrefix != null ? gamePrefix : "" + ChatColor.ITALIC);
		
		return builder.length() > 16 ? builder.substring(0, 16) : builder.toString();
	}
	
	private static String brackets(String string)
	{
		return ChatColor.DARK_GRAY + "[" + string + ChatColor.DARK_GRAY + "]";
	}
	
	public void sendClanMessage(String message)
	{
		if(bpClan == null)
			return;
		
		for(Player member : bpClan.getOnlinePlayers())
			member.sendMessage(ChatColor.GOLD + "" + ChatColor.ITALIC + name + ": " + message);
	}

	public boolean isStaff()
	{
		Player player = getPlayer();
		return player.hasPermission("Breakpoint.admin") || player.hasPermission("Breakpoint.moderator") || player.hasPermission("Breakpoint.helper");
	}

	public void sendStaffMessage(String message)
	{
		if(!isStaff())
			return;
		
		for(Player target : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpTarget = BPPlayer.get(target);
			
			if(bpTarget.isStaff())
				target.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.ITALIC + name + ": " + message);
		}
	}
	
	/**
	 * @param player
	 * @return Chat name without admin's prefix
	 */
	public String getPVPName()
	{
		Player player = getPlayer();
		String prefix = getPVPPrefix();
		String playerName = player.getName();
		return prefix + playerName;
	}

	public String getPVPPrefix()
	{
		Clan clan = getClan();
		String prefix = getRawPVPPrefix();
		String clanName = clan != null ? (ChatColor.GRAY + clan.getColoredName() + " " + ChatColor.WHITE) : "";//getChatPrefix(player);
		String gamePrefix = gameProperties != null ? gameProperties.getChatPrefix() : "" + ChatColor.ITALIC;
		return clanName + prefix + gamePrefix;
	}

	/**
	 * @param player
	 * @return Fully prefixed chat name
	 */
	public String getChatName()
	{
		Player player = getPlayer();
		String prefix = getChatPrefix();
		String playerName = player.getName();
		return prefix + playerName;
	}

	public String getChatPrefix()
	{
		Player player = getPlayer();
		Clan clan = getClan();
		String prefix = getPrefix(player);
		String clanName = clan != null ? (ChatColor.GRAY + clan.getColoredName() + " " + ChatColor.WHITE) : "";//getChatPrefix(player);
		String gamePrefix = gameProperties != null ? gameProperties.getChatPrefix() : "" + ChatColor.ITALIC;
		return clanName + prefix + gamePrefix;
	}

	public String getPrefix(Player player)
	{
		if(player.hasPermission("Breakpoint.admin"))
			return ChatManager.prefixAdmin + " ";
		else if(player.hasPermission("Breakpoint.moderator"))
			return ChatManager.prefixModerator + " ";
		else if(player.hasPermission("Breakpoint.helper"))
			return ChatManager.prefixHelper + " ";
		else if(player.hasPermission("Breakpoint.sponsor"))
			return ChatManager.prefixSponsor + " ";
		else if(player.hasPermission("Breakpoint.yt"))
			return ChatManager.prefixYT + " ";
		else if(player.hasPermission("Breakpoint.vip"))
			return ChatManager.prefixVIP + " ";
		else
			return "";
	}
	
	public String getRawPVPPrefix()
	{
		if(isStaff())
			return ChatManager.prefixVIP + " ";
		
		Player player = getPlayer();
		
		if(player.hasPermission("Breakpoint.sponsor"))
			return ChatManager.prefixSponsor + " ";
		else if(player.hasPermission("Breakpoint.yt"))
			return ChatManager.prefixYT + " ";
		else if(player.hasPermission("Breakpoint.vip"))
			return ChatManager.prefixVIP + " ";
		else
			return "";
	}
	//}}

	public boolean hasCooldown(String path, double seconds, boolean setCooldown)
	{
		long now = System.currentTimeMillis();
		
		if(cooldowns.containsKey(path))
		{
			long lastTimeUsed = cooldowns.get(path);
			boolean hasCooldown = lastTimeUsed >= now - (seconds * 1000);
			
			if (hasCooldown)
				return true;
		}
		
		if(setCooldown)
			cooldowns.put(path, now);
		return false;
	}
	
	public void removeCooldown(String path)
	{
		cooldowns.remove(path);
	}
	
	public void clearCooldowns()
	{
		cooldowns.clear();
	}
	
	public void reset()
	{
		setMultikills(0);
		setKilledThisLife(0);
		setQueueCharacter(null);
		
		if(game != null)
		{
			game.reset(this);
			game.onPlayerLeaveGame(this);
		}
		
		setGame(null);
		setGameProperties(null);
		
		setPlayerListName();
	}
	
	public void colorArmor()
	{
		Player player = getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack[] armor = inv.getArmorContents();
		BPEquipment[] contents = getLobbyInventory().getContents();
		for (int i = 0; i < 4; i++)
		{
			if (!(contents[i] instanceof BPArmor) || armor[i] == null)
				continue;
			BPArmor bpArmor = ((BPArmor) contents[i]).clone();
			bpArmor.colorArmor(armor[i]);
		}
		inv.setArmorContents(armor);
	}

	public void equipArmor()
	{
		Player player = getPlayer();
		
		PlayerInventory pi = player.getInventory();
		ItemStack[] armor = getWornArmor();
		armor[2] = applyPerks(armor[2]);
		pi.setArmorContents(armor);
	}
	
	public ItemStack applyPerks(ItemStack is)
	{
		for(Perk perk : getEnabledPerks())
			is = perk.getType().applyToItemStack(is);
		
		return is;
	}

	public ItemStack[] getWornArmor()
	{
		int playingSince = getArmorWoreSince();
		ItemStack[] armor = new ItemStack[4];
		int decreaseMinutesBy = (int) ((System.currentTimeMillis() / (1000 * 60)) - playingSince);
		BPEquipment[] contents = getLobbyInventory().getContents();
		
		for (int i = 0; i < 4; i++)
		{
			if(contents[i] == null)
				continue;
			
			BPEquipment bpEquipment = contents[i].clone();
			bpEquipment.decreaseMinutesLeft(decreaseMinutesBy);
			
			if (bpEquipment.hasExpired())
				continue;
			
			armor[i] = bpEquipment.getItemStack();
		}
		
		for (int i = 0; i < 4; i++)
			if (armor[i] == null)
			{
				ItemStack is = new ItemStack(BPArmor.getMaterial(i));
				ItemMeta im = is.getItemMeta();
				im.setDisplayName(MessageType.SHOP_ITEM_ARMOR_NOCOLOR.getTranslation().getValue());
				is.setItemMeta(im);
				armor[i] = is;
			}
		
		return armor;
	}
	
	public void updateArmorMinutesLeft()
	{
		int playingSince = getArmorWoreSince();
		int decreaseMinutesBy = (int) ((System.currentTimeMillis() / (1000 * 60)) - playingSince);
		BPEquipment[] contents = getLobbyInventory().getContents();
		
		for (int i = 0; i < 4; i++)
		{
			if (contents[i] != null)
			{
				BPEquipment bpEquipment = contents[i];
				bpEquipment.decreaseMinutesLeft(decreaseMinutesBy);
				if (!bpEquipment.hasExpired())
				{
					contents[i] = bpEquipment;
					continue;
				}
			}
			contents[i] = null;
		}
		
		getLobbyInventory().setContents(contents);
	}

	public boolean hasSpaceInLobbyInventory()
	{
		Player player = getPlayer();
		BPEquipment[] contents = getLobbyInventory().getContents();
		int size = player.hasPermission("Breakpoint.vip") ? 24 : 12;
		for (int i = 0; i < size; i++)
			if (contents[4 + i] == null)
				return true;
		return false;
	}

	public int getLobbyInventorySpaceSlot()
	{
		Player player = getPlayer();
		BPEquipment[] contents = getLobbyInventory().getContents();
		int size = player.hasPermission("Breakpoint.vip") ? 24 : 12;
		for (int i = 0; i < size; i++)
			if (contents[4 + i] == null)
				return 4 + i;
		return -1;
	}

	public void purify()
	{
		Player player = getPlayer();
		
		clearInventory();
		player.setHealth(((Damageable) player).getMaxHealth());
		player.setFoodLevel(15);
		player.setSaturation(Float.MAX_VALUE);
		for (PotionEffect pe : player.getActivePotionEffects())
			player.removePotionEffect(pe.getType());
	}

	public void teleport(Location loc, boolean updatePos)
	{
		Chunk chunk = loc.getWorld().getChunkAt(loc);
		Player player = getPlayer();
		
		if (!chunk.isLoaded())
			chunk.load();
		player.teleport(loc);
		if (updatePos)
			Breakpoint.getInstance().prm.updateEntity(player, Arrays.asList(Bukkit.getOnlinePlayers()));
	}

	public void clearInventory()
	{
		Player player = getPlayer();
		PlayerInventory pi = player.getInventory();
		pi.clear();
		pi.setArmorContents(new ItemStack[] { null, null, null, null });
	}
	
	public void spawn()
	{
		Player player = getPlayer();
		
		if(player.isDead())
			return;
		
		if(isInGame())
			game.spawn(this);
		else
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			purify();
			teleport(config.getLobbyLocation(), false);
			InventoryMenuManager.showLobbyMenu(this);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true), true);
		}
	}

	public int addMoney(int amount, boolean inform, boolean allowMultiplication)
	{
		Player player = getPlayer();
		boolean positive = amount >= 0;
		boolean multiply = positive && DoubleMoneyManager.isDoubleXP() && allowMultiplication;
		
		if(multiply)
			amount *= 2;
		
		statistics.increaseMoney(amount);
		
		if(inform)
			if(player != null)
			{
				MessageType msgType = positive ? MessageType.OTHER_EMERALDS_INCREASE : MessageType.OTHER_EMERALDS_DECREASE;
				
				player.sendMessage(msgType.getTranslation().getValue(amount, multiply ? "2x" : ""));
			}
		
		return statistics.getMoney();
	}
	
	public boolean isInGameWith(BPPlayer bpPlayer)
	{
		if(!isInGame())
			return false;
		
		return game.equals(bpPlayer.getGame());
	}
	
	public boolean isInGame()
	{
		return game != null && gameProperties != null;
	}
	
	public boolean isPlaying()
	{
		if(!isInGame())
			return false;
		
		return gameProperties.isPlaying();
	}
	
	public Player getPlayer()
	{
		return Bukkit.getPlayerExact(name);
	}

	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(name);
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}

	public PlayerStatistics getStatistics()
	{
		return statistics;
	}

	public void setStatistics(PlayerStatistics statistics)
	{
		this.statistics = statistics;
	}
	
	public int getMaxEquippedPerks()
	{
		return 2;//getPlayer().hasPermission("Breakpoint.vip") ? 2 : 1;
	}
	
	public int getPerkInventoryRows()
	{
		int disabled = 0, enabled = 0;
		
		for(Perk perk : perks)
			if(perk.isEnabled())
				enabled++;
			else
				disabled++;
		
		int disabledRows = (int) Math.ceil(disabled / 9.0);
		int enabledRows = (int) Math.ceil(enabled / 9.0);
		
		return 1 + disabledRows + enabledRows;
	}
	
	public Perk getPerk(PerkType type)
	{
		for(Perk perk : perks)
			if(perk.getType() == type)
				return perk;
		
		return null;
	}
	
	public Perk getOrAddPerk(PerkType type)
	{
		Perk perk = getPerk(type);
		
		if(perk != null)
			return perk;
		
		perk = new Perk(type, 0, false);
		
		perks.add(perk);
		
		return perk;
	}
	
	public LinkedList<Perk> getDisabledPerks()
	{
		return getPerks(false);
	}
	
	public LinkedList<Perk> getEnabledPerks()
	{
		return getPerks(true);
	}
	
	private LinkedList<Perk> getPerks(boolean enabled)
	{
		LinkedList<Perk> list = new LinkedList<Perk>();
		
		for(Perk perk : perks)
			if(perk.isEnabled() == enabled)
				list.add(perk);
		
		return list;
	}
	
	public void decreasePerkLives(boolean notice)
	{
		for(Perk perk : getEnabledPerks())
		{
			perk.decreaseLivesLeft();
			
			if(perk.hasExpired())
			{
				perks.remove(perk);
				
				if(notice)
					getPlayer().sendMessage(MessageType.PERK_NOTICE_BROKEN.getTranslation().getValue(perk.getType().getName()));
			}
		}
	}
	
	public void checkAchievement(AchievementType ac)
	{
		if (!hasAchievement(ac))
			giveAchievement(ac);
	}

	public void checkAchievement(AchievementType ac, CharacterType ct)
	{
		if (!hasAchievement(ac, ct))
			giveAchievement(ac, ct);
	}

	public void giveAchievement(AchievementType ac)
	{
		AchievementTranslation att = ac.getTranslation();
		String propName = att.getName();
		
		if (propName.equals(""))
			propName = ac.name();
		
		setAchievement(ac, true);
		
		Player player = getPlayer();
		
		if(player == null)
			return;
		
		String desc = att.getDesc();
		Location loc = player.getLocation();
		
		player.playSound(loc, Sound.ZOMBIE_UNFECT, 16F, 4F);
		player.sendMessage(MessageType.ACHIEVEMENT_GET.getTranslation().getValue(propName));
		player.sendMessage(ChatColor.LIGHT_PURPLE + desc);
	}

	public void giveAchievement(AchievementType ac, CharacterType ct)
	{
		String propName = ac.getName(ct);
		
		setAchievement(ac, ct, true);
		
		Player player = getPlayer();
		
		if(player == null)
			return;
		
		String desc = ac.getDescription(ct);
		Location loc = player.getLocation();
		
		player.playSound(loc, Sound.ZOMBIE_UNFECT, 16F, 4F);
		player.sendMessage(MessageType.ACHIEVEMENT_GET.getTranslation().getValue(propName));
		player.sendMessage(ChatColor.LIGHT_PURPLE + desc);
	}
	
	public boolean hasAchievement(AchievementType type)
	{
		for(Achievement ac : achievements)
			if(ac.getType() == type)
				return ac.isAchieved();
		
		return false;
	}
	
	public boolean hasAchievement(AchievementType type, CharacterType ct)
	{
		for(Achievement ac : achievements)
			if(ac.getType() == type)
			{
				CharacterAchievement cac = (CharacterAchievement) ac;
				
				if(cac.getCharacterType() == ct)
					return cac.isAchieved();
			}
		
		return false;
	}
	
	private Achievement getAchievement(AchievementType type)
	{
		for(Achievement ac : achievements)
			if(ac.getType() == type)
				return ac;
		
		return null;
	}
	
	private Achievement getAchievement(AchievementType type, CharacterType ct)
	{
		for(Achievement ac : achievements)
			if(ac.getType() == type)
			{
				CharacterAchievement cac = (CharacterAchievement) ac;
				
				if(cac.getCharacterType() == ct)
					return ac;
			}
		
		return null;
	}
	
	public void setAchievement(AchievementType type, boolean value)
	{
		Achievement ac = getAchievement(type);
		
		ac.setAchieved(value);
	}
	
	public void setAchievement(AchievementType type, CharacterType ct, boolean value)
	{
		Achievement ac = getAchievement(type, ct);
		
		ac.setAchieved(value);
	}

	public List<Achievement> getAchievements()
	{
		return achievements;
	}

	public void setAchievements(List<Achievement> achievements)
	{
		this.achievements = achievements;
	}

	public Settings getSettings()
	{
		return settings;
	}

	public void setSettings(Settings settings)
	{
		this.settings = settings;
	}

	public LobbyInventory getLobbyInventory()
	{
		return lobbyInventory;
	}

	public int getAchievementViewPage()
	{
		return achievementViewPage;
	}

	public void setAchievementViewPage(int achievementViewPage)
	{
		this.achievementViewPage = achievementViewPage;
	}

	public BPPlayer getAchievementViewTarget()
	{
		return achievementViewTarget;
	}

	public void setAchievementViewTarget(BPPlayer achievementViewTarget)
	{
		this.achievementViewTarget = achievementViewTarget;
	}

	public int getAfkSecondsToKick()
	{
		return afkSecondsToKick;
	}

	public void setAfkSecondsToKick(int afkSecondsToKick)
	{
		this.afkSecondsToKick = afkSecondsToKick;
	}

	public void clearAfkSecondsToKick()
	{
		afkSecondsToKick = AfkManager.defSTK;
	}

	public Location getAfkPastLocation()
	{
		return afkPastLocation;
	}

	public void setAfkPastLocation(Location afkPastLocation)
	{
		this.afkPastLocation = afkPastLocation;
	}

	public int getArmorWoreSince()
	{
		return armorWoreSince;
	}

	public void setArmorWoreSince(int armorWoreSince)
	{
		this.armorWoreSince = armorWoreSince;
	}

	public void setArmorWoreSince()
	{
		armorWoreSince = (int) (System.currentTimeMillis() / (1000 * 60));
	}

	public Clan getClan()
	{
		return bpClan;
	}

	public void setClan(Clan bpClan)
	{
		this.bpClan = bpClan;
	}

	public int getMoney()
	{
		return statistics.getMoney();
	}

	public void setMoney(int money)
	{
		statistics.setMoney(money);
	}

	public Location getShopItemLocation()
	{
		return shopItemLocation;
	}

	public void setShopItemLocation(Location shopItemLocation)
	{
		this.shopItemLocation = shopItemLocation;
	}

	public long getSpawnTime()
	{
		return spawnTime;
	}

	public void setSpawnTime(long spawnTime)
	{
		this.spawnTime = spawnTime;
	}

	public CharacterType getQueueCharacter()
	{
		return queueCharacter;
	}

	public void setQueueCharacter(CharacterType queueCharacter)
	{
		this.queueCharacter = queueCharacter;
	}

	public long getLastTimeKilled()
	{
		return lastTimeKilled;
	}

	public void setLastTimeKilled(long lastTimeKilled)
	{
		this.lastTimeKilled = lastTimeKilled;
	}

	public void setLastTimeKilled()
	{
		lastTimeKilled = System.currentTimeMillis();
	}

	public int getMultikills()
	{
		return multikills;
	}

	public void setMultikills(int multikills)
	{
		this.multikills = multikills;
	}

	public int getKilledThisLife()
	{
		return killedThisLife;
	}

	public void setKilledThisLife(int killedThisLife)
	{
		this.killedThisLife = killedThisLife;
	}

	public ItemStack[] getQuickChatInventoryContents()
	{
		return quickChatInventoryContents;
	}

	public void setQuickChatInventoryContents(ItemStack[] quickChatInventoryContents)
	{
		this.quickChatInventoryContents = quickChatInventoryContents;
	}

	public HashMap<String, Long> getCooldowns()
	{
		return cooldowns;
	}

	public String getLastMessage()
	{
		return lastMessage;
	}

	public void setLastMessage(String lastMessage)
	{
		this.lastMessage = lastMessage;
	}

	public long getTimeJoined()
	{
		return timeJoined;
	}

	public void setTimeJoined(long timeJoined)
	{
		this.timeJoined = timeJoined;
	}

	public Location getSingleTeleportLocation()
	{
		return singleTeleportLocation;
	}

	public void setSingleTeleportLocation(Location singleTeleportLocation)
	{
		this.singleTeleportLocation = singleTeleportLocation;
	}

	public boolean isLeaveAfterDeath()
	{
		return leaveAfterDeath;
	}

	public void setLeaveAfterDeath(boolean leaveAfterDeath)
	{
		this.leaveAfterDeath = leaveAfterDeath;
	}
	
	public BPPlayer getLastTimeKilledBy()
	{
		return lastTimeKilledBy;
	}

	public void setLastTimeKilledBy(BPPlayer lastTimeKilledBy)
	{
		this.lastTimeKilledBy = lastTimeKilledBy;
	}

	public GameProperties getGameProperties()
	{
		return gameProperties;
	}

	public void setGameProperties(GameProperties gameProperties)
	{
		this.gameProperties = gameProperties;
	}

	public SBManager getScoreboardManager()
	{
		return scoreboardManager;
	}
	
	public boolean isOnline()
	{
		return getOfflinePlayer().isOnline();
	}

	public HashMap<BPPlayer, Long> getLastTimeDamagedBy()
	{
		return lastTimeDamagedBy;
	}

	public List<Perk> getPerks()
	{
		return perks;
	}

	public void setPerks(List<Perk> perks)
	{
		this.perks = perks;
	}
}
