package cz.projectsurvive.me.limeth.breakpoint.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.NametagEditManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SBManager;
import cz.projectsurvive.me.limeth.breakpoint.maps.BPMapPalette;
import cz.projectsurvive.me.limeth.breakpoint.maps.CurrentMapRenderer;
import cz.projectsurvive.me.limeth.breakpoint.maps.MapManager;
import cz.projectsurvive.me.limeth.breakpoint.maps.SizeRenderer;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public abstract class Game
{
	//{{STATIC
	
	public static final Game loadGame(YamlConfiguration yml, String name)
	{
		try
		{
			String rawType = yml.getString(name + ".type");
			GameType type = GameType.valueOf(rawType);
			
			String[] rawSignLoc = yml.getString(name + ".signLoc", "world,0,0,0").split(",");
			Location signLoc = new Location(Bukkit.getWorld(rawSignLoc[0]), Integer.parseInt(rawSignLoc[1]), Integer.parseInt(rawSignLoc[2]), Integer.parseInt(rawSignLoc[3]));
			
			return type.loadGame(yml, name, signLoc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	//}}
	
	public static final int defaultMapSeconds = 9 * 60;
	private final GameType type;
	private final GameListener listener;
	private String name;
	private Location signLoc;
	private LinkedList<? extends BPMap> maps;
	protected final List<BPPlayer> players;
	private int activeMapId, mapSecondsLeft;
	protected final short votingMapId, currentMapMapId, playerAmountRendererMapId;
	private Integer countdownTaskId = null;
	private boolean active, roundEnded;
	private MapPoll mapPoll;
	private final SizeRenderer playerAmountRenderer;
	private final CurrentMapRenderer currentMapRenderer;
	private String firstBloodPlayerName, lastBloodPlayerName;
	
	public Game(GameType type, String name, Location signLoc, LinkedList<? extends BPMap> maps)
	{
		if(type == null || name == null || type.getListenerClass() == null || name.length() <= 0 || signLoc == null || maps == null)
			throw new IllegalArgumentException();
		
		Class<? extends GameListener> listenerClass = type.getListenerClass();
		
		try
		{
			listener = listenerClass.getConstructor(Game.class).newInstance(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new IllegalArgumentException("Cannot create a new instance of GameListener. (Game: " + name + ")");
		}
		
		this.type = type;
		this.signLoc = signLoc.getBlock().getLocation();
		this.name = name;
		this.maps = maps;
		players = new ArrayList<BPPlayer>();
		
		votingMapId = MapManager.getNextFreeId(5);
		
		currentMapMapId = MapManager.getNextFreeId();
		currentMapRenderer = new CurrentMapRenderer();
		MapView cmmv = Bukkit.getMap(currentMapMapId);
		currentMapRenderer.set(cmmv);
		
		playerAmountRendererMapId = MapManager.getNextFreeId();
		playerAmountRenderer = new SizeRenderer(BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 0), BPMapPalette.getColor(BPMapPalette.WHITE, 2), 0);
		MapView rtsmv = Bukkit.getMap(playerAmountRendererMapId);
		playerAmountRenderer.set(rtsmv);
		active = isPlayable(true);
	}
	
	public Game(GameType type, String name, Location signLoc)
	{
		this(type, name, signLoc, new LinkedList<BPMap>());
	}
	
	public void start()
	{
		changeMap(getRandomMapWithCapacity(getPlayers().size()));
		startCountdown();
		
		startExtra();
		
		setActive(true);
	}
	
	//{{Requests
	public abstract void spawn(BPPlayer bpPlayer);
	public abstract void reset(BPPlayer bpPlayer);
	public abstract void updateProgressObjective(BPPlayer bpPlayer);
	public abstract void updateProgressObjectiveHeader(BPPlayer bpPlayer);
	public abstract void showInGameMenu(BPPlayer bpPlayer);
	protected abstract void endRoundExtra();
	protected abstract void changeMapExtra();
	protected abstract void saveExtra(YamlConfiguration yml);
	protected abstract void startExtra();
	//}}
	
	public abstract void onCommand(CommandSender sender, String[] args);
	
	public void onPlayerLeaveGame(BPPlayer bpPlayer)
	{
		players.remove(bpPlayer);
		bpPlayer.setGame(null);
		bpPlayer.setGameProperties(null);
		updatePlayerAmountRenderer();
		
		SBManager sbm = bpPlayer.getScoreboardManager();
		
		SBManager.updateLobbyObjectives();
		sbm.updateSidebarObjective();
		sbm.getProgressObj().unregister();
		sbm.setProgressObj(null);
		
		NametagEditManager.updateNametag(bpPlayer);
	}
	
	//{{Saving
	public final void save(YamlConfiguration yml)
	{
		String mapPath = name + ".maps";
		yml.set(name, null);
		
		yml.set(name + ".type", type.name());
		yml.set(name + ".signLoc", signLoc.getWorld().getName() + "," + signLoc.getBlockX() + "," + signLoc.getBlockY() + "," + signLoc.getBlockZ());
		
		saveExtra(yml);
		
		for(BPMap map : maps)
			if(map.isPlayable())
				map.save(yml, mapPath);
	}
	//}}
	
	public void updateLobbyMaps(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();

		player.sendMap(Bukkit.getMap(currentMapMapId));
		player.sendMap(Bukkit.getMap(playerAmountRendererMapId));
	}
	
	public boolean isPlayable(boolean skipActive)
	{
		if(!skipActive && !isActive())
			return false;
		
		return type != null && name != null && name.length() > 0 && signLoc != null && getPlayableMaps().size() > 0;
	}
	
	public boolean isPlayable()
	{
		return isPlayable(false);
	}
	
	public void join(BPPlayer bpPlayer) throws Exception
	{
		Player player = bpPlayer.getPlayer();
		
		player.setGameMode(GameMode.ADVENTURE);
		player.setFlying(false);
		player.setAllowFlight(false);
		InventoryMenuManager.saveLobbyMenu(bpPlayer);
		players.add(bpPlayer);
		bpPlayer.setGame(this);
		bpPlayer.getPlayer().sendMessage(MessageType.LOBBY_GAME_JOIN.getTranslation().getValue(getName()));
		updatePlayerAmountRenderer();
		bpPlayer.setPlayerListName();
		
		SBManager sbm = bpPlayer.getScoreboardManager();
		
		sbm.initProgressObj();
		updateProgressObjective(bpPlayer);
		sbm.updateSidebarObjective();
		SBManager.updateLobbyObjectives();
	}
	
	public void updatePlayerAmountRenderer()
	{
		playerAmountRenderer.setSize(players.size());
		MapManager.updateMapForNotPlayingPlayers(playerAmountRendererMapId);
	}
	
	public void second()
	{
		setMapSecondsLeft(getMapSecondsLeft() - 1);
		
		if(getMapSecondsLeft() > 0)
			scheduleNextSecond();
		else
		{
			endRound();
			scheduleMapPoll();
		}
		
		updateProgressHeaderTime();
	}
	
	public void updateProgressHeaderTime()
	{
		for(BPPlayer bpPlayer : players)
			updateProgressObjectiveHeader(bpPlayer);
	}
	
	public void scheduleMapPoll()
	{
		final Game game = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				setMapPoll(new MapPoll(game));
				getMapPoll().startCountdown();
			}
		}, 20L * 10);
	}
	
	public void endRound()
	{
		endRoundExtra();
		setRoundEnded(true);
		PlayerManager.clearHotBars();
		awardLastKiller();
		
		for(BPPlayer bpPlayer : players)
			NametagEditManager.updateNametag(bpPlayer);
	}

	public void awardLastKiller()
	{
		if(getLastBloodPlayerName() == null)
			return;
		
		Player player = Bukkit.getPlayerExact(getLastBloodPlayerName());
		
		if(player == null)
			return;
		
		BPPlayer bpPlayer = BPPlayer.get(player);
		
		if(Bukkit.getOnlinePlayers().length >= Bukkit.getMaxPlayers() / 2)
			bpPlayer.checkAchievement(AchievementType.LAST_BLOOD);
		
		Location playerLoc = player.getLocation();
		String playerPVPName = bpPlayer.getPVPName();
		broadcast(ChatColor.DARK_RED + "" + ChatColor.BOLD + "> LAST BLOOD! " + playerPVPName + ChatColor.DARK_RED + ChatColor.BOLD + " <", false);
		PlayerManager.spawnRandomlyColoredFirework(playerLoc);
	}

	public void broadcastDeathMessage(String victim, String killer)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if (bpPlayer.isInGame())
				if(bpPlayer.getSettings().hasDeathMessages())
					player.sendMessage(MessageType.PVP_KILLINFO_KILLED.getTranslation().getValue(victim, killer));
		}
	}

	public void broadcastDeathMessage(String victim)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if (bpPlayer.isInGame())
				if(bpPlayer.getSettings().hasDeathMessages())
					player.sendMessage(MessageType.PVP_KILLINFO_DIED.getTranslation().getValue(victim));
		}
	}

	public void startCountdown()
	{
		if(getCountdownTaskId() != null)
			Bukkit.getScheduler().cancelTask(getCountdownTaskId());
		
		setMapSecondsLeft(defaultMapSeconds);
		scheduleNextSecond();
	}
	
	public void scheduleNextSecond()
	{
		Breakpoint plugin = Breakpoint.getInstance();
		
		setCountdownTaskId(plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run()
			{
				second();
			}
		}, 20L));
	}

	public int getRandomMapWithCapacity(int players)
	{
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(int i = 0; i < getMaps().size(); i++)
		{
			BPMap map = getMaps().get(i);
			
			if(!map.isPlayable())
				continue;
			
			if(map.isPlayableWith(players))
				ids.add(i);
		}
		if(ids.size() > 0)
			return ids.get(new Random().nextInt(ids.size()));
		else
			return -1;
	}

	public void changeMap(int mapId)
	{
		setActiveMapId(mapId);
		BPMap map = getCurrentMap();
		String mapName = map.getName();
		
		map.setLastTimePlayed(System.currentTimeMillis());
		spawnPlayers();
		updateCurrentMapRenderer();
		setFirstBloodPlayerName(null);
		setLastBloodPlayerName(null);
		
		for(World world : Bukkit.getWorlds()) //TODO Remove and doDaylightCycle to false
			world.setTime(0);
		
		for(BPPlayer bpPlayer : players)
			NametagEditManager.updateNametag(bpPlayer);
		
		setRoundEnded(false);
		startCountdown();
		
		changeMapExtra();
		
		broadcast(MessageType.MAP_CHANGE.getTranslation().getValue(mapName), true);
	}
	
	public void updateCurrentMapRenderer()
	{
		BPMap map = getCurrentMap();
		currentMapRenderer.setCurrentMap(map);
		MapManager.updateMapForNotPlayingPlayers(currentMapMapId);
	}
	
	public void spawnPlayers()
	{
		for(BPPlayer bpPlayer : players)
			bpPlayer.spawn();
	}

	public void broadcastCombo(String playerName, Location loc, ChatColor color, String decor, String name)
	{
		broadcast(color + "" + decor + " " + ChatColor.BOLD + name + "! - " + playerName + color + " " + decor, false);
		PlayerManager.spawnRandomlyColoredFirework(loc);
	}
	
	public void broadcast(String string, boolean prefix)
	{
		for (BPPlayer bpPlayer : players)
			if (bpPlayer.isPlaying())
			{
				Player player = bpPlayer.getPlayer();
				
				player.sendMessage((prefix ? (ChatColor.DARK_GRAY + "[" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Breakpoint" + ChatColor.DARK_GRAY + "] ") : "") + ChatColor.YELLOW + string);
			}
		Bukkit.getConsoleSender().sendMessage("[Breakpoint] [" + name + "] " + string);
	}

	public void broadcast(String string)
	{
		broadcast(string, false);
	}

	public BPMap getMapByName(String mapName)
	{
		for(BPMap map : getMaps())
			if(mapName.equals(map.getName()))
				return map;
		return null;
	}

	public boolean votingInProgress()
	{
		if(getMapPoll() != null)
			return getMapPoll().getVoting();
		return false;
	}
	
	public boolean isMapActive(int index)
	{
		BPMap map = maps.get(index);
		return isMapActive(map);
	}
	
	public boolean isMapActive(BPMap map)
	{
		return getCurrentMap().equals(map);
	}

	public BPMap getCurrentMap()
	{
		return getMaps().get(getActiveMapId());
	}
	
	public GameType getType()
	{
		return type;
	}
	
	public LinkedList<? extends BPMap> getPlayableMaps()
	{
		LinkedList<BPMap> playableMaps = new LinkedList<BPMap>();
		
		for(BPMap map : maps)
			if(map.isPlayable())
				playableMaps.add(map);
		
		return playableMaps;
	}

	public LinkedList<? extends BPMap> getMaps()
	{
		return maps;
	}

	public void setMaps(LinkedList<BPMap> maps)
	{
		this.maps = maps;
	}

	public List<BPPlayer> getPlayers()
	{
		return players;
	}

	public int getActiveMapId()
	{
		return activeMapId;
	}

	public void setActiveMapId(int activeMapId)
	{
		this.activeMapId = activeMapId;
	}

	public int getMapSecondsLeft()
	{
		return mapSecondsLeft;
	}

	public void setMapSecondsLeft(int mapSecondsLeft)
	{
		this.mapSecondsLeft = mapSecondsLeft;
	}

	public Integer getCountdownTaskId()
	{
		return countdownTaskId;
	}

	public void setCountdownTaskId(Integer countdownTaskId)
	{
		this.countdownTaskId = countdownTaskId;
	}

	public boolean hasRoundEnded()
	{
		return roundEnded;
	}

	public void setRoundEnded(boolean roundEnded)
	{
		this.roundEnded = roundEnded;
	}

	public MapPoll getMapPoll()
	{
		return mapPoll;
	}

	public void setMapPoll(MapPoll mapPoll)
	{
		this.mapPoll = mapPoll;
	}

	public String getFirstBloodPlayerName()
	{
		return firstBloodPlayerName;
	}

	public void setFirstBloodPlayerName(String firstBloodPlayerName)
	{
		this.firstBloodPlayerName = firstBloodPlayerName;
	}

	public String getLastBloodPlayerName()
	{
		return lastBloodPlayerName;
	}

	public void setLastBloodPlayerName(String lastBloodPlayerName)
	{
		this.lastBloodPlayerName = lastBloodPlayerName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Location getSignLocation()
	{
		return signLoc;
	}

	public void setSignLocation(Location signLoc)
	{
		this.signLoc = signLoc;
	}

	public boolean isActive()
	{
		return active;
	}

	private void setActive(boolean active)
	{
		this.active = active;
	}
	
	public short getVotingMapId()
	{
		return votingMapId;
	}

	public GameListener getListener()
	{
		return listener;
	}
}
