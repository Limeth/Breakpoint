package cz.projectsurvive.me.limeth.breakpoint.game.dm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import cz.projectsurvive.me.limeth.breakpoint.game.BPMap;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameType;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.language.Translation;
import cz.projectsurvive.me.limeth.breakpoint.managers.SBManager;
import cz.projectsurvive.me.limeth.breakpoint.perks.Perk;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class DMGame extends Game
{
	public static final int spawnProtectionSeconds = 3;
	private Location characterSelectionLocation;
	private final Translation scoreHeaderTranslation;
	private final HashMap<BPPlayer, Integer> points = new HashMap<BPPlayer, Integer>();
	
	public DMGame(String name, Location signLoc, Location characterSelectionLocation, LinkedList<DMMap> maps)
	{
		super(GameType.DM, name, signLoc, maps);
		
		this.characterSelectionLocation = characterSelectionLocation;
		scoreHeaderTranslation = MessageType.SCOREBOARD_PROGRESS_DM_HEADER.getTranslation();
	}

	public DMGame(String name, Location signLoc)
	{
		this(name, signLoc, null, new LinkedList<DMMap>());
	}
	
	@Override
	public boolean isPlayable(boolean skipActive)
	{
		if(!super.isPlayable(skipActive))
			return false;
		
		return characterSelectionLocation != null;
	}

	@Override
	public void spawn(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		
		if(player.isDead())
			return;
		
		DMProperties props = ((DMProperties) bpPlayer.getGameProperties());
		CharacterType ct = props.getCharacterType();
		bpPlayer.setSpawnTime(System.currentTimeMillis());
		bpPlayer.purify();
		props.equip();
		Perk.onSpawn(bpPlayer);
		
		if(ct == null)
		{
			bpPlayer.teleport(characterSelectionLocation, false);
			return;
		}
		Location spawnLoc = getSpawnLocation(bpPlayer);
		props.setSpawnedAt(spawnLoc);
		bpPlayer.teleport(spawnLoc, true);
	}

	@Override
	public void reset(BPPlayer bpPlayer)
	{
	}
	
	@Override
	public void updateProgressObjective(BPPlayer bpPlayer)
	{
		updateProgressObjective(bpPlayer.getScoreboardManager().getProgressObj());
	}
	
	public void updateProgressObjective(Objective progressObj)
	{
		updateProgressObjectiveHeader(progressObj);
		updateProgressObjectiveScores(progressObj);
	}
	
	public void updateProgressObjectiveScores(BPPlayer bpPlayer)
	{
		updateProgressObjectiveScores(bpPlayer.getScoreboardManager().getProgressObj());
	}
	
	public void updateProgressObjectiveScores(Objective progressObj)
	{
		for(BPPlayer bpPlayer : getPlayers())
		{
			int killsThisRound = getPoints(bpPlayer);
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(bpPlayer.getTag());
			Score score = progressObj.getScore(offlinePlayer);
			
			score.setScore(killsThisRound);
		}
	}
	
	public void updateProgressObjectiveScoresOf(BPPlayer target)
	{
		for(BPPlayer bpPlayer : getPlayers())
			updateProgressObjectiveScoresOf(bpPlayer.getScoreboardManager().getProgressObj(), target);
	}
	
	public void updateProgressObjectiveScoresOf(Objective obj, BPPlayer target)
	{
		int killsThisRound = getPoints(target);
		OfflinePlayer offlinePlayer = target.getOfflinePlayer();
		Score score = obj.getScore(offlinePlayer);
		
		score.setScore(killsThisRound);
	}
	
	@Override
	public void updateProgressObjectiveHeader(BPPlayer bpPlayer)
	{
		updateProgressObjectiveHeader(bpPlayer.getScoreboardManager().getProgressObj());
	}
	
	public void updateProgressObjectiveHeader(Objective progressObj)
	{
		progressObj.setDisplayName(scoreHeaderTranslation.getValue(SBManager.formatTime(getMapSecondsLeft())));
	}
	
	public void updateProgressObjectiveScores()
	{
		for(BPPlayer bpPlayer : getPlayers())
			updateProgressObjectiveScores(bpPlayer);
	}
	
	@Override
	public void showInGameMenu(BPPlayer bpPlayer)
	{
	}

	@Override
	protected void endRoundExtra()
	{
		LinkedList<BPPlayer> ordered = getPlayersOrderedByKillsThisRound();
		
		if(ordered.size() <= 0)
			return;
		
		BPPlayer bpWinner = ordered.get(0);
		String winnerName = bpWinner.getPVPName();
		int winnerKills = getPoints(bpWinner);
		
		broadcast(ChatColor.GRAY + "---------------------------------");
		broadcast(MessageType.RESULT_DM_WIN.getTranslation().getValue(winnerName, winnerKills));
		awardPlayers(ordered);
		broadcast(ChatColor.GRAY + "---------------------------------");
	}

	@Override
	protected void changeMapExtra()
	{
		resetPoints();
		updateProgressObjectiveScores();
	}

	@Override
	protected void saveExtra(YamlConfiguration yml)
	{
		yml.set(getName() + ".charSelLoc", characterSelectionLocation.getWorld().getName() + "," + characterSelectionLocation.getX() + "," + characterSelectionLocation.getY() + "," + characterSelectionLocation.getZ() + "," + characterSelectionLocation.getYaw() + "," + characterSelectionLocation.getPitch());
	}

	@Override
	protected void startExtra()
	{
	}
	
	@Override
	public void onCommand(CommandSender sender, String[] args)
	{
		if(args.length <= 0)
			sender.sendMessage("info, start, map, teamSelLoc, charSelLoc, spawnFlags, removeFlags");
		else if(args[0].equalsIgnoreCase("info"))
		{
			sender.sendMessage("Name: " + getName());
			sender.sendMessage("Active: " + isActive());
			sender.sendMessage("Playable: " + isPlayable(true));
			sender.sendMessage("MapSecondsLeft: " + getMapSecondsLeft());
			sender.sendMessage("#Maps: " + getMaps().size());
			sender.sendMessage("Current map: " + getCurrentMap().getName());
			sender.sendMessage("#Players: " + getPlayers().size());
		}
		else if(args[0].equalsIgnoreCase("start"))
		{
			if(isPlayable(true))
				start();
			else
				sender.sendMessage(ChatColor.RED + "The game is not playable!");
		}
		else if(args[0].equalsIgnoreCase("charSelLoc"))
		{
			if(!(sender instanceof Player))
				return;
			Player player = (Player) sender;
			Location loc = player.getLocation();
			characterSelectionLocation = loc;
			sender.sendMessage(ChatColor.GREEN + "Character selection location successfully set!");
		}
		else if(args[0].equalsIgnoreCase("map"))
			if(args.length <= 1)
			{
				sender.sendMessage("list");
				sender.sendMessage("add [MinPlayers] [MaxPlayers] [Name]");
				sender.sendMessage("remove [Name]");
				sender.sendMessage("setCapacity [Min] [Max] [Name]");
				sender.sendMessage("addSpawn [Name]");
				sender.sendMessage("removeSpawn [Index] [Name]");
				sender.sendMessage("TPSpawn [Index] [Name]");
			}
			else if(args[1].equalsIgnoreCase("list"))
			{
				List<DMMap> maps = getMaps();
				for(int i = 0; i < maps.size(); i++)
				{
					DMMap map = maps.get(i);
					String name = map.getName();
					boolean playable = map.isPlayable();
					boolean capPlayable = map.isPlayableWith(players.size());
					int min = map.getMinimumPlayers();
					int max = map.getMaximumPlayers();
					int spawnAmount = map.getSpawns().size();
					ChatColor color = !playable ? ChatColor.RED : (!capPlayable ? ChatColor.YELLOW : ChatColor.GREEN);
					sender.sendMessage(color + name + ChatColor.GRAY + " <" + min + "; " + max + "> [" + spawnAmount + "]");
				}
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				if(args.length <= 4)
				{
					sender.sendMessage("add [MinPlayers] [MaxPlayers] [Name]");
					return;
				}
				try
				{
					int min = Integer.parseInt(args[2]);
					int max = Integer.parseInt(args[3]);
					String name = args[4];
					for(int i = 5; i < args.length; i++)
						name += " " + args[i];
					DMMap map = new DMMap(name, min, max);
					getMaps().add(map);
					sender.sendMessage(ChatColor.GREEN + "Map '" + name + "' <" + min + "; " + max + "> successfully added.");
				}
				catch(Exception e)
				{
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "An error occured.");
				}
			}
			else if(args[1].equalsIgnoreCase("remove"))
			{
				if(args.length <= 2)
				{
					sender.sendMessage("remove [Name]");
					return;
				}
				String name = args[2];
				for(int i = 3; i < args.length; i++)
					name += " " + args[i];
				BPMap map = getMapByName(name);
				getMaps().remove(map);
				sender.sendMessage(ChatColor.GREEN + "Map '" + name + "' successfully removed.");
			}
			else if(args[1].equalsIgnoreCase("setCapacity"))
			{
				if(args.length <= 4)
				{
					sender.sendMessage("setCapacity [MinPlayers] [MaxPlayers] [Name]");
					return;
				}
				String name = args[4];
				int minPlayers, maxPlayers;
				for(int i = 5; i < args.length; i++)
					name += " " + args[i];
				DMMap map = getMapByName(name);
				if(map == null)
				{
					sender.sendMessage(ChatColor.RED + "Map '" + name + "' not found.");
					return;
				}
				try
				{
					minPlayers = Integer.parseInt(args[2]);
					maxPlayers = Integer.parseInt(args[3]);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "An error occured.");
					return;
				}
				map.setMinimumPlayers(minPlayers);
				map.setMaximumPlayers(maxPlayers);
				sender.sendMessage(ChatColor.GREEN + "Capacity for map '" + name + "' successfully set. <" + minPlayers + "; " + maxPlayers + ">");
			}
			else if(args[1].equalsIgnoreCase("addSpawn"))
			{
				if(!(sender instanceof Player))
				{
					sender.sendMessage(ChatColor.RED + "Only for players!");
					return;
				}
				
				Player player = (Player) sender;
				
				if(args.length <= 2)
					return;
				
				String mapName = args[2];
				for(int i = 3; i < args.length; i++)
					mapName += " " + args[i];
				
				DMMap map = getMapByName(mapName);
				
				if(map == null)
				{
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}
				
				Location loc = player.getLocation();
				map.addSpawn(loc);
				sender.sendMessage(ChatColor.GREEN + "Spawn location added to map '" + mapName + "'.");
			}
			else if(args[1].equalsIgnoreCase("removeSpawn"))
			{
				if(args.length <= 3)
					return;
				
				int index;
				
				try
				{
					index = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "ID is not a whole number!");
					return;
				}
				
				String name = args[3];
				
				for(int i = 4; i < args.length; i++)
					name += " " + args[i];
				
				DMMap map = getMapByName(name);
				
				if(map == null)
				{
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}
				
				LinkedList<Location> spawns = map.getSpawns();
				
				if(index < 0 || index >= spawns.size())
				{
					sender.sendMessage(ChatColor.RED + "Index out of bounds!");
					return;
				}
				
				Location spawn = spawns.get(index);
				
				map.removeSpawn(spawn);
				sender.sendMessage(ChatColor.GREEN + "Spawn  #" + index + " removed!");
			}
			else if(args[1].equalsIgnoreCase("TPSpawn"))
			{
				if(!(sender instanceof Player))
					return;
				
				Player player = (Player) sender;
				
				if(args.length <= 3)
					return;
				
				int index;
				
				try
				{
					index = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "ID is not a whole number!");
					return;
				}
				
				String name = args[3];
				
				for(int i = 4; i < args.length; i++)
					name += " " + args[i];
				
				DMMap map = getMapByName(name);
				
				if(map == null)
				{
					sender.sendMessage(ChatColor.RED + "Map not found!");
					return;
				}
				
				LinkedList<Location> spawns = map.getSpawns();
				
				if(index < 0 || index >= spawns.size())
				{
					sender.sendMessage(ChatColor.RED + "Index out of bounds!");
					return;
				}
				
				Location spawn = spawns.get(index);
				
				player.teleport(spawn);
				
				sender.sendMessage(ChatColor.GREEN + "Teleported to spawn #" + index + "!");
			}
	}
	
	@Override
	public void join(BPPlayer bpPlayer) throws Exception
	{
		bpPlayer.setGameProperties(new DMProperties(this, bpPlayer));
		
		if(!hasJoinedBefore(bpPlayer))
			setPoints(bpPlayer, 0);
		
		super.join(bpPlayer);
		bpPlayer.spawn();
	}
	
	@Override
	public DMMap getMapByName(String name)
	{
		return (DMMap) super.getMapByName(name);
	}
	
	@Override
	public DMMap getCurrentMap()
	{
		return (DMMap) super.getCurrentMap();
	}

	@SuppressWarnings("unchecked")
	@Override
	public LinkedList<DMMap> getMaps()
	{
		LinkedList<? extends BPMap> maps = super.getMaps();
		return (LinkedList<DMMap>) maps;
	}
	
	public void awardPlayers(LinkedList<BPPlayer> ordered)
	{
		int size = ordered.size();
		Translation placeTranslation = MessageType.RESULT_DM_POSITION.getTranslation();
		
		for(int i = 0; i < size; i++)
		{
			BPPlayer bpPlayer = ordered.get(i);
			Player player = bpPlayer.getPlayer();
			int pos = i + 1;
			int money = (size - pos) / 2;
			
			player.sendMessage(placeTranslation.getValue(pos, size));
			
			if(money > 0)
				bpPlayer.addMoney(money, true, true);
		}
	}
	
	public Location getSpawnLocation(BPPlayer bpPlayer)
	{
		DMProperties props = (DMProperties) bpPlayer.getGameProperties();
		Location lastTimeSpawnedAt = props.getSpawnedAt();
		return findMostAcceptableLocation(lastTimeSpawnedAt);
	}
	
	public Location findMostAcceptableLocation(Location exclude)
	{
		DMMap map = getCurrentMap();
		LinkedList<Location> spawns = map.getSpawns();
		HashMap<Location, Double> distanceMap = new HashMap<Location, Double>();
		
		for(int i = 0; i < spawns.size(); i++)
		{
			Location spawn = spawns.get(i);
			
			if(spawn.equals(exclude))
				continue;
			
			double distances = 0;
			
			for(BPPlayer bpPlayer : getPlayers())
			{
				if(!bpPlayer.isPlaying())
					continue;
				
				Player player = bpPlayer.getPlayer();
				Location pLoc = player.getLocation();
				
				distances += pLoc.distance(spawn);
			}
			
			distanceMap.put(spawn, distances);
		}
		
		Location furthestLocation = null;
		double furthestDistance = 0;
		
		for(Entry<Location, Double> entry : distanceMap.entrySet())
		{
			double distance = entry.getValue();
			
			if(distance >= furthestDistance)
			{
				furthestLocation = entry.getKey();
				furthestDistance = distance;
			}
		}
		
		return furthestLocation;
	}
	
	public int getPoints(BPPlayer bpPlayer)
	{
		return points.get(bpPlayer);
	}

	public void setPoints(BPPlayer bpPlayer, int points)
	{
		this.points.put(bpPlayer, points);
	}
	
	public void increasePoints(BPPlayer bpPlayer)
	{
		points.put(bpPlayer, points.get(bpPlayer) + 1);
	}
	
	public void increasePointsOfOthers(BPPlayer bpPlayer)
	{
		for(BPPlayer bpOther : getPlayers())
			if(!bpOther.equals(bpPlayer))
				increasePoints(bpOther);
	}
	
	public void resetPoints()
	{
		points.clear();
		
		for(BPPlayer bpPlayer : getPlayers())
			setPoints(bpPlayer, 0);
	}
	
	public boolean hasJoinedBefore(BPPlayer bpPlayer)
	{
		return points.containsKey(bpPlayer);
	}
	
	public LinkedList<BPPlayer> getPlayersOrderedByKillsThisRound()
	{
		LinkedList<BPPlayer> unordered = new LinkedList<BPPlayer>();
		LinkedList<BPPlayer> ordered = new LinkedList<BPPlayer>();
		
		//Copy list of players
		
		for(BPPlayer bpPlayer : getPlayers())
			unordered.add(bpPlayer);
		
		//Order players
		
		while(unordered.size() > 0)
		{
			BPPlayer pMostKills = null;
			int mostKills = 0;
			
			for(BPPlayer bpPlayer : unordered)
			{
				int killsThisRound = getPoints(bpPlayer);
				
				if(killsThisRound >= mostKills)
				{
					pMostKills = bpPlayer;
					mostKills = killsThisRound;
				}
			}
			
			if(pMostKills != null)
			{
				unordered.remove(pMostKills);
				ordered.add(pMostKills);
			}
			else
				break;
		}
		
		return ordered;
	}
}
