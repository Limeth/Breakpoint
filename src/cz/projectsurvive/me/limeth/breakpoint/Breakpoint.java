package cz.projectsurvive.me.limeth.breakpoint;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import me.limeth.storageAPI.StorageType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.fijistudios.jordan.FruitSQL;

import cz.projectsurvive.me.limeth.breakpoint.language.Language;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.listeners.BanListener;
import cz.projectsurvive.me.limeth.breakpoint.listeners.ChatListener;
import cz.projectsurvive.me.limeth.breakpoint.listeners.PVPListener;
import cz.projectsurvive.me.limeth.breakpoint.listeners.PlayerConnectionListener;
import cz.projectsurvive.me.limeth.breakpoint.listeners.PlayerInteractListener;
import cz.projectsurvive.me.limeth.breakpoint.listeners.PlayerInventoryListener;
import cz.projectsurvive.me.limeth.breakpoint.managers.AbilityManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.AfkManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.ChatManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.DoubleMoneyManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.FileManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.LicenseManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.LobbyInfoManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.NametagEditManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.StatisticsManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.VIPManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.AchievementsCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.BPCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.CWCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.ClanCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.FlyCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.GMCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.HelpOPCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.RankCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.SkullCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.TopClansCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.commands.TopCommandExecutor;
import cz.projectsurvive.me.limeth.breakpoint.managers.events.EventManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.events.advent.AdventManager;
import cz.projectsurvive.me.limeth.breakpoint.maps.MapManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.Settings;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;

public class Breakpoint extends JavaPlugin
{
	private static Breakpoint instance;
	private static Configuration config;
	private static FruitSQL mySQL;
	public static final String PLUGIN_NAME = "Breakpoint";
	
	public AbilityManager am = new AbilityManager(this);
	public AfkManager afkm = new AfkManager(this);
	public MapManager mapm;
	public ProtocolManager prm; //BPPlayer-520, this-181 a 80, PlayerManager-355
	public EventManager evtm;
	public boolean successfullyEnabled;
	
	@Override
	public void onEnable()
	{
		if(LicenseManager.isAllowed())
			try
			{
				instance = this;
				prm = ProtocolLibrary.getProtocolManager();
				MapManager.setup();
				Clan.loadClans();
				config = Configuration.load();
				
				if(config.getStorageType() == StorageType.MYSQL)
					mySQL = config.connectToMySQL();
				
				BPPlayer.updateTable(mySQL);
				Language.loadLanguage(PLUGIN_NAME, config.getLanguageFileName());
				FileManager.loadOnlinePlayerHistory();
				config.getRandomShop().build();
				ChatManager.loadStrings();
				InventoryMenuManager.initialize();
				GameManager.loadGames();
				GameManager.startPlayableGames();
				redirectCommands();
				StatisticsManager.startLoop();
				NametagEditManager.setLoaded();
				registerListeners();
				afkm.startLoop();
				MapManager.startLoop();
				VIPManager.startLoops();
				LobbyInfoManager.startLoop();
				setEventManager();
				DoubleMoneyManager.update();
				getServer().clearRecipes();
				World world = config.getLobbyLocation().getWorld();
				world.setStorm(false);
				world.setThundering(false);
				world.setWeatherDuration(1000000000);
				successfullyEnabled = true;
				System.out.println("Breakpoint v" + getDescription().getVersion() + " by Limeth enabled!");
				
				return;
			}
			catch (Throwable t)
			{
				Breakpoint.warn("Fatal error when enabling Breakpoint!");
				t.printStackTrace();
			}
		else
		{
			ConsoleCommandSender ccs = Bukkit.getConsoleSender();
			
			ccs.sendMessage(ChatColor.YELLOW + "###");
			ccs.sendMessage(ChatColor.RED + "This server doesn't have the license to use Breakpoint. Contact us at " + ChatColor.WHITE + "license@projectsurvive.cz" + ChatColor.RED + ", please.");
			ccs.sendMessage(ChatColor.YELLOW + "###");
		}
		
		successfullyEnabled = false;
		getServer().getPluginManager().registerEvents(new BanListener(), this);
	}
	
	private void test()
	{
		FallingBlock block = null;
		Player player = null;
		
		player.setPassenger(block);
	}

	@Override
	public void onDisable()
	{
		if (!successfullyEnabled)
			return;
		
		trySave();
		kickPlayers();
		
		if(evtm != null)
			evtm.save();
		
		getServer().getScheduler().cancelTasks(this);
		instance = null;
		config = null;
		System.out.println("Breakpoint v" + getDescription().getVersion() + " by Limeth disabled!");
	}
	
	public void save() throws IOException
	{
		BPPlayer.saveOnlinePlayersData();
		Clan.saveClans();
		config.save();
		GameManager.saveGames();
		FileManager.saveOnlinePlayerHistory();
	}
	
	public void trySave()
	{
		try
		{
			save();
		}
		catch(IOException e)
		{
			warn("Error when saving Breakpoint data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void redirectCommands()
	{
		Server server = getServer();
		
		server.getPluginCommand("bp").setExecutor(new BPCommandExecutor());
		server.getPluginCommand("helpop").setExecutor(new HelpOPCommandExecutor());
		server.getPluginCommand("clan").setExecutor(new ClanCommandExecutor());
		server.getPluginCommand("achievements").setExecutor(new AchievementsCommandExecutor());
		server.getPluginCommand("top").setExecutor(new TopCommandExecutor());
		server.getPluginCommand("topclans").setExecutor(new TopClansCommandExecutor());
		server.getPluginCommand("rank").setExecutor(new RankCommandExecutor());
		server.getPluginCommand("gm").setExecutor(new GMCommandExecutor());
		server.getPluginCommand("skull").setExecutor(new SkullCommandExecutor());
		server.getPluginCommand("fly").setExecutor(new FlyCommandExecutor());
		server.getPluginCommand("cw").setExecutor(new CWCommandExecutor());
	}

	public void registerListeners()
	{
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new PlayerConnectionListener(this), this);
		pm.registerEvents(new PVPListener(this), this);
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new PlayerInventoryListener(this), this);
		
	//	if(NametagEditManager.isLoaded())
	//		pm.registerEvents(new TagAPIListener(this), this);
		
		// Disable enchantments
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				
				if(player == null)
					return;
				
				World world = player.getWorld();
				PacketContainer packet = event.getPacket();
				Entity entity = packet.getEntityModifier(world).read(0);
				
				if (entity instanceof Player)
				{
					Player viewed = (Player) entity;
					String viewedName = viewed.getName();
					String playerName = player.getName();
					Clan viewedClan = Clan.getByPlayer(viewedName);
					Clan playerClan = Clan.getByPlayer(playerName);
					if (viewedClan != null && playerClan != null)
						if (viewedClan.equals(playerClan))
							return;
				}
				
				ItemStack stack = packet.getItemModifier().read(0);
				
				if (stack != null)
				{
					Set<Enchantment> encs = stack.getEnchantments().keySet();
					for (Enchantment enc : encs)
						stack.removeEnchantment(enc);
				}
			}
		});
		
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.WINDOW_ITEMS) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				BPPlayer bpPlayer = BPPlayer.get(player);
				
				if(bpPlayer == null || !bpPlayer.isInGame())
					return;
				
				Settings settings = bpPlayer.getSettings();
				
				if(settings.hasShowEnchantments())
					return;
				
				PacketContainer packet = event.getPacket();
				ItemStack[] stacks = packet.getItemArrayModifier().read(0);
				
				if(stacks != null)
					for(ItemStack stack : stacks)
						if(stack != null)
							removeEnchantments(stack);
			}
		});
		
		prm./*getAsynchronousManager().registerAsyncHandler*/addPacketListener(new PacketAdapter(this, PacketType.Play.Server.SET_SLOT) {
			@Override
			public void onPacketSending(PacketEvent event)
			{
				Player player = event.getPlayer();
				BPPlayer bpPlayer = BPPlayer.get(player);
				
				if(bpPlayer == null || !bpPlayer.isInGame())
					return;
				
				Settings settings = bpPlayer.getSettings();
				
				if(settings.hasShowEnchantments())
					return;
				
				PacketContainer packet = event.getPacket();
				ItemStack stack = packet.getItemModifier().read(0);
				
				if(stack != null)
					removeEnchantments(stack);
			}
		});
	}
	
	private static void removeEnchantments(ItemStack stack)
	{
		Map<Enchantment, Integer> entries = stack.getEnchantments();
		
		if(entries == null || entries.size() <= 0)
			return;
		
		ItemMeta im = stack.getItemMeta();
		List<String> lore = im.hasLore() ? im.getLore() : new LinkedList<String>();
		
		for(Entry<Enchantment, Integer> entry : entries.entrySet())
		{
			Enchantment type = entry.getKey();
			Integer level = entry.getValue();
			
			im.removeEnchant(type);
			lore.add(ChatColor.GRAY + type.getName() + " " + level);
		}
		
		im.setLore(lore);
		stack.setItemMeta(im);
	}

	public static void info(String string)
	{
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] " + string);
	}

	public static void warn(String string)
	{
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Warning] " + string);
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.hasPermission("Breakpoint.receiveWarnings"))
				player.sendMessage("[Breakpoint] " + ChatColor.RED + "[Warning] " + string);
	}

	public static void broadcast(String string, boolean prefix)
	{
		for (Player player : Bukkit.getOnlinePlayers())
			player.sendMessage((prefix ? MessageType.CHAT_BREAKPOINT.getTranslation().getValue() : ChatColor.YELLOW) + " " + string);
		
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [Broadcast] " + string);
	}

	public static void broadcast(String string)
	{
		broadcast(string, false);
	}
	
	public static void clearChat()
	{
		for (Player player : Bukkit.getOnlinePlayers())
			for (int i = 0; i < 10; i++)
				player.sendMessage("");
	}

	public void clearChat(Player player)
	{
		for (int i = 0; i < 10; i++)
			player.sendMessage("");
	}
	
	public void kickPlayers()
	{
		String msg = MessageType.CHAT_BREAKPOINT.getTranslation().getValue() + " " + MessageType.OTHER_RESTART.getTranslation().getValue();
		
		for (Player player : Bukkit.getOnlinePlayers())
			player.kickPlayer(msg);
	}
	
	public void setEventManager()
	{
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		
		if(month == Calendar.DECEMBER && dayOfMonth <= AdventManager.LAST_DAY)
			evtm = AdventManager.load(year);
	}
	
	public boolean hasEvent()
	{
		return evtm != null;
	}
	
	public EventManager getEventManager()
	{
		return evtm;
	}

	public static Breakpoint getInstance()
	{
		return instance;
	}

	public static Configuration getBreakpointConfig()
	{
		return config;
	}

	public static FruitSQL getMySQL()
	{
		return mySQL;
	}

	public static boolean hasMySQL()
	{
		return mySQL != null;
	}
}
