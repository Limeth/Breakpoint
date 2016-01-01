package cz.projectsurvive.me.limeth.breakpoint.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.map.MapView;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SBManager;
import cz.projectsurvive.me.limeth.breakpoint.maps.VoteRenderer;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public class MapPoll
{
	private final Game game;
	private Map<String, Integer> votes;
	public String[] maps;
	private final List<String> haveVoted;
	private boolean voting;
	private int result;
	private final short mapViewId;

	public MapPoll(Game game)
	{
		this.game = game;
		votes = new HashMap<String, Integer>();
		haveVoted = new ArrayList<String>();
		voting = false;
		mapViewId = game.getVotingMapId();
		getMapsInOrder(game.getMaps(), game.getPlayers().size());
		setMapImages();
	}

	public void getMapsInOrder(List<? extends BPMap> availableMaps, int players)
	{
		List<BPMap> allowedMaps = new ArrayList<BPMap>();
		Map<String, Integer> result = new HashMap<String, Integer>();
		maps = new String[5];
		for (BPMap map : availableMaps)
			if(map.isPlayable())
				if (map.isPlayableWith(players))
					allowedMaps.add(map);
		for (int i = 0; i < 5; i++)
		{
			BPMap topMap = null;
			long topTime = 1000000000000000000L;
			for (BPMap map : allowedMaps)
			{
				long lastTimePlayed = map.getLastTimePlayed();
				if (lastTimePlayed < topTime)
				{
					topMap = map;
					topTime = lastTimePlayed;
				}
			}
			if (topMap != null)
			{
				allowedMaps.remove(topMap);
				result.put(topMap.getName(), 0);
				maps[i] = topMap.getName();
			}
			else
				break;
		}
		votes = result;
	}

	public int getBestMap()
	{
		int nejHlas = 0;
		ArrayList<Integer> nejMapy = new ArrayList<Integer>();
		for (int mapNo = 0; mapNo < maps.length; mapNo++)
			if (maps[mapNo] != null)
			{
				String stringMap = maps[mapNo];
				int hlasy = votes.get(stringMap);
				if (hlasy >= nejHlas)
				{
					if (hlasy > nejHlas)
						nejMapy.clear();
					nejMapy.add(mapNo);
					nejHlas = hlasy;
				}
			}
			else
				break;
		if (nejMapy.size() > 1)
			for (int i = 0; i < nejMapy.size(); i++)
			{
				int bestMapIndex = nejMapy.get(i);
				String bestMapName = maps[bestMapIndex];
				BPMap bestMap = game.getMapByName(bestMapName);
				if(game.isMapActive(bestMap))
				{
					nejMapy.remove(i);
					break;
				}
			}
		if (nejMapy.size() > 1)
		{
			Random rand = new Random();
			return nejMapy.get(rand.nextInt(nejMapy.size()));
		}
		else
			return nejMapy.get(0);
	}

	public void setMapImages()
	{
		for (int i = 0; i < maps.length; i++)
			if (maps[i] != null)
				setMapImage(i, maps[i]);
	}

	public void setMapImage(int mapId, String mapName)
	{
		VoteRenderer vr = new VoteRenderer(game.getMapByName(mapName));
		MapView mv = Bukkit.getMap((short) (mapViewId + mapId));
		vr.set(mv);
	}

	public void showOptions()
	{
		for (BPPlayer bpPlayer : game.getPlayers())
			if (bpPlayer.isInGame())
				showOptions(bpPlayer);
	}

	public void showOptions(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		PlayerManager.clearHotBar(inv);
		for (int i = 0; i < maps.length; i++)
			if (maps[i] != null)
				inv.setItem(i, getMapItem(i));
		updateOptions(player);
	}

	public void updateOptions(Player player)
	{
		for (int i = 0; i < maps.length; i++)
			if (maps[i] != null)
				player.sendMap(Bukkit.getMap((short) (mapViewId + i)));
	}

	public ItemStack getMapItem(int i)
	{
		ItemStack is = new ItemStack(Material.MAP, 1, (short) (mapViewId + i));
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("" + ChatColor.YELLOW + ChatColor.GOLD + maps[i]);
		is.setItemMeta(im);
		return is;
	}

	public boolean isColored(int amount)
	{
		if (amount <= 0)
			return false;
		for (int i : votes.values())
			if (i > amount)
				return false;
		return true;
	}

	public int getMapPercentage(double curVotes)
	{
		double allVotes = 0;
		for (int i : votes.values())
			allVotes += i;
		return (int) ((curVotes / allVotes) * 100);
	}

	public boolean hasVoted(String playerName)
	{
		return haveVoted.contains(playerName);
	}

	public boolean isIdCorrect(int id)
	{
		int size = 0;
		for (int i = 0; i < maps.length; i++)
			if (maps[i] != null)
				size++;
			else
				break;
		return id >= 0 && id <= size;
	}

	public int vote(String playerName, int mapId, int strength)
	{
		String mapName = maps[mapId];
		int curVotes = votes.get(mapName) + strength;
		votes.put(mapName, curVotes);
		haveVoted.add(playerName);
		
		for(BPPlayer bpPlayer : game.getPlayers())
			bpPlayer.getScoreboardManager().updateVoteOptions(votes);
		
		return curVotes;
	}

	public void endVoting()
	{
		voting = false;
		int mapId = getBestMap();
		String mapName = maps[mapId];
		int score = votes.get(mapName);
		result = game.getMaps().indexOf(game.getMapByName(mapName));
		int perc = getMapPercentage(score);
		
		PlayerManager.clearHotBars();
		
		for(BPPlayer bpPlayer : game.getPlayers())
			bpPlayer.getScoreboardManager().updateSidebarObjective();
		
		game.broadcast(MessageType.VOTING_END.getTranslation().getValue(mapName, score, perc), true);
	}

	public void endPoll()
	{
		game.changeMap(result);
		game.setMapPoll(null);
	}

	public void startCountdown()
	{
		voting = true;
		Breakpoint plugin = Breakpoint.getInstance();
		
		for(BPPlayer bpPlayer : game.getPlayers())
		{
			SBManager sbm = bpPlayer.getScoreboardManager();
			
			showOptions();
			sbm.restartVoteObj();
			sbm.updateVoteOptions(votes);
			sbm.updateSidebarObjective();
		}
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run()
			{
				endVoting();
			}
		}, 20L * 30);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run()
			{
				endPoll();
			}
		}, 20L * 40);
	}

	public boolean getVoting()
	{
		return voting;
	}

	public int getNumOfMaps()
	{
		return maps.length;
	}
}
