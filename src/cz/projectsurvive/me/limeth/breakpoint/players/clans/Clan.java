package cz.projectsurvive.me.limeth.breakpoint.players.clans;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.limeth.storageAPI.Storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.statistics.CWMatchResult;

public class Clan
{
	//{{STATIC
	public static final char[] allowedChars = new char[] {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
	};
	public static final int maxLength = 8;
	public static final int minLength = 3;
	public static final List<Clan> clans = new LinkedList<Clan>();
	
	public static Clan create(String name, String leader)
	{
		Clan bpClan = new Clan(name, leader);
		clans.add(bpClan);
		return bpClan;
	}

	public static void remove(String name)
	{
		String colorLess = ChatColor.stripColor(name);
		for(Clan bpClan : clans)
			if(bpClan.getName().equalsIgnoreCase(colorLess))
			{
				clans.remove(bpClan);
				break;
			}
	}
	
	public static Clan load(File file)
	{
		try
		{
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
			String clanNameColored = yml.getString("name");
			String leader = yml.getString("leader");
			List<String> moderators = yml.getStringList("moderators");
			List<String> members = yml.getStringList("members");
			List<String> invited = yml.getStringList("invited");
			
			List<String> rawMatchResults = yml.getStringList("matchResults");
			LinkedList<CWMatchResult> matchResults = new LinkedList<CWMatchResult>();
			
			if(rawMatchResults != null)
				for(String rawMatchResult : rawMatchResults)
					try
					{
						matchResults.add(CWMatchResult.unserialize(rawMatchResult));
					}
					catch(Exception e)
					{
						Breakpoint.warn("Error when unserializing rawMatchResult (" + clanNameColored + "): " + e.getMessage());
					}
			
			return new Clan(clanNameColored, leader, moderators, members, invited, matchResults);
		}
		catch(Exception e)
		{
			Breakpoint.warn("Clan '" + file.getPath() + "' was not loaded properly.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Clan newLoad(Storage storage) throws Exception
	{
		try
		{
			String clanNameColored = storage.get(String.class, "name", null);
			String leader = storage.get(String.class, "leader");
			List<String> moderators = storage.getList("moderators", String.class);
			List<String> members = storage.getList("members", String.class);
			List<String> invited = storage.getList("invited", String.class);
			
			List<String> rawMatchResults = storage.getList("matchResults", String.class);
			LinkedList<CWMatchResult> matchResults = new LinkedList<CWMatchResult>();
			
			if(rawMatchResults != null)
				for(String rawMatchResult : rawMatchResults)
					try
					{
						matchResults.add(CWMatchResult.unserialize(rawMatchResult));
					}
					catch(Exception e)
					{
						Breakpoint.warn("Error when unserializing rawMatchResult (" + clanNameColored + "): " + e.getMessage());
					}
			
			return new Clan(clanNameColored, leader, moderators, members, invited, matchResults);
		}
		catch(Exception e)
		{
			Breakpoint.warn("Clan '" + storage.getName() + "' was not loaded properly.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Clan load(String clanName)
	{
		File file = new File("plugins/Breakpoint/clans/" + clanName + ".yml");
		return load(file);
	}
	
	public static Clan newLoad(String clanName) throws Exception
	{
		return newLoad(new Storage(clanName));
	}

	public static void loadClans()
	{
		File folder = new File("plugins/Breakpoint/clans");
		File[] clanFiles = folder.listFiles();
		if (clanFiles != null)
		{
			for(File clanFile : clanFiles)
				if(clanFile.getPath().endsWith(".yml"))
				{
					Clan bpClan = load(clanFile);
					clans.add(bpClan);
				}
			
			for(Clan clan : clans)
				clan.loadPendingChallenges();
		}
	}

	public static void saveClans() throws IOException
	{
		clearClansFolder();
		
		for(Clan bpClan : Clan.clans)
			bpClan.save();
	}

	private static void clearClansFolder()
	{
		File folder = new File("plugins/Breakpoint/clans");
		File[] clanFiles = folder.listFiles();
		if(clanFiles != null)
			for(File clanFile : clanFiles)
				clanFile.delete();
	}

	public static boolean isCorrectLength(String name)
	{
		String colorLess = ChatColor.stripColor(name);
		int length = colorLess.length();
		return length >= minLength && length <= maxLength;
	}

	public static boolean hasCorrectCharacters(String name)
	{
		String colorLess = ChatColor.stripColor(name);
		
		for(int i = 0; i < colorLess.length(); i++)
		{
			char c = colorLess.charAt(i);
			boolean isCorrect = false;
			for(char curC : allowedChars)
				if (curC == c)
				{
					isCorrect = true;
					break;
				}
			if(!isCorrect)
				return false;
		}
		return true;
	}

	public static boolean exists(String name)
	{
		String colorLess = ChatColor.stripColor(name);
		for(Clan bpClan : clans)
			if (bpClan.getName().equalsIgnoreCase(colorLess))
				return true;
		return false;
	}

	public static Clan get(String name)
	{
		String colorLess = ChatColor.stripColor(name);
		for(Clan bpClan : clans)
			if (bpClan.getName().equalsIgnoreCase(colorLess))
				return bpClan;
		return null;
	}

	public static Clan getByLeader(String leader)
	{
		for(Clan bpClan : clans)
			if (bpClan.isLeader(leader))
				return bpClan;
		return null;
	}

	public static Clan getByMember(String member)
	{
		for(Clan bpClan : clans)
			if (bpClan.isMember(member))
				return bpClan;
		return null;
	}
	
	public static Clan getByModerator(String moderator)
	{
		for(Clan bpClan : clans)
			if (bpClan.isModerator(moderator))
				return bpClan;
		return null;
	}

	public static Clan getByPlayer(String player)
	{
		for (Clan bpClan : clans)
			if (bpClan.isLeader(player) || bpClan.isMember(player) || bpClan.isModerator(player))
				return bpClan;
		return null;
	}

/*	public void updatePrefix(Player player)
	{
		String playerName = player.getName();
		BPClan bpClan = getClanByPlayer(playerName);
		if (bpClan != null)
			updatePlayerClan(player, bpClan);
		else
		{
			plugin.mm.removeMetadata(player, "Breakpoint.clan.name");
			plugin.mm.removeMetadata(player, "Breakpoint.clan.leader");
		}
	}

	public void updatePlayerClan(Player player, BPClan bpClan)
	{
		String playerName = player.getName();
		boolean leader = bpClan.getLeaderName().equals(playerName);
		String clanName = bpClan.getColoredName();
		plugin.mm.setMetadata(player, "Breakpoint.clan.name", clanName);
		plugin.mm.setMetadata(player, "Breakpoint.clan.leader", leader);
	}*/

	public static String getChatPrefix(BPPlayer bpPlayer)
	{
		Clan clan = bpPlayer.getClan();
		
		if(clan != null)
			return ChatColor.GRAY + clan.getColoredName() + " ";
		else
			return "";
	}
	
	public static String getColored(String raw)
	{
		raw = raw.replaceAll("&0", "" + ChatColor.BLACK);
		raw = raw.replaceAll("&1", "" + ChatColor.DARK_BLUE);
		raw = raw.replaceAll("&2", "" + ChatColor.DARK_GREEN);
		raw = raw.replaceAll("&3", "" + ChatColor.DARK_AQUA);
		raw = raw.replaceAll("&4", "" + ChatColor.DARK_RED);
		raw = raw.replaceAll("&5", "" + ChatColor.DARK_PURPLE);
		raw = raw.replaceAll("&6", "" + ChatColor.GOLD);
		raw = raw.replaceAll("&7", "" + ChatColor.GRAY);
		raw = raw.replaceAll("&8", "" + ChatColor.DARK_GRAY);
		raw = raw.replaceAll("&9", "" + ChatColor.BLUE);
		raw = raw.replaceAll("&a", "" + ChatColor.GREEN);
		raw = raw.replaceAll("&b", "" + ChatColor.AQUA);
		raw = raw.replaceAll("&c", "" + ChatColor.RED);
		raw = raw.replaceAll("&d", "" + ChatColor.LIGHT_PURPLE);
		raw = raw.replaceAll("&e", "" + ChatColor.YELLOW);
		raw = raw.replaceAll("&f", "" + ChatColor.WHITE);
		return raw;
	}
	//}}
	
	private final String leader;
	private final List<String> moderators, members, invited;
	private final LinkedList<ClanChallenge> pendingChallenges;
	private final LinkedList<CWMatchResult> matchResults;
	private String name, coloredName, coloredNameRaw;
	public Clan(String coloredNameRaw, String leader, List<String> moderators, List<String> members, List<String> invited, LinkedList<ClanChallenge> pendingChallenges, LinkedList<CWMatchResult> matchResults)
	{
		if(coloredNameRaw == null)
			throw new IllegalArgumentException("coloredNameRaw == null");
		else if(leader == null)
			throw new IllegalArgumentException("leader == null");
		
		setColoredNameRaw(coloredNameRaw);
		this.leader = leader;
		this.moderators = moderators != null ? moderators : new LinkedList<String>();
		this.members = members != null ? members : new LinkedList<String>();
		this.invited = invited != null ? invited : new LinkedList<String>();
		this.pendingChallenges = pendingChallenges != null ? pendingChallenges : new LinkedList<ClanChallenge>();
		this.matchResults = matchResults != null ? matchResults : new LinkedList<CWMatchResult>();
	}
	
	public Clan(String coloredNameRaw, String leader, List<String> moderators, List<String> members, List<String> invited, LinkedList<CWMatchResult> matchResults)
	{
		this(coloredNameRaw, leader, moderators, members, invited, null, matchResults);
	}

	public Clan(String coloredNameRaw, String leader)
	{
		this(coloredNameRaw, leader, null, null, null, null);
	}

	
	public void loadPendingChallenges()
	{
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(getFile());
		List<String> rawPendingChallenges = yml.getStringList("pendingChallenges");
		
		if(rawPendingChallenges != null)
			for(String rawChallenge : rawPendingChallenges)
				try
				{
					getPendingChallenges().add(ClanChallenge.unserialize(rawChallenge));
				}
				catch(Exception e)
				{
					Breakpoint.warn("Error when unserializing rawChallenge (" + name + "): " + e.getMessage());
				}
	}
	
	public void save() throws IOException
	{
		String clanNameColored = getColoredNameRaw();
		String clanName = getName();
		File dataFile = new File("plugins/Breakpoint/clans/" + clanName + ".yml");
		YamlConfiguration yamlData = YamlConfiguration.loadConfiguration(dataFile);
		LinkedList<String> rawPendingChallenges = new LinkedList<String>();
		LinkedList<String> rawMatchResults = new LinkedList<String>();
		
		for(ClanChallenge challenge : pendingChallenges)
			if(challenge.isWaiting())
				rawPendingChallenges.add(challenge.serialize());
		
		for(CWMatchResult result : matchResults)
			rawMatchResults.add(result.serialize());
		
		yamlData.set("name", clanNameColored);
		yamlData.set("leader", leader);
		yamlData.set("moderators", moderators);
		yamlData.set("members", members);
		yamlData.set("invited", invited);
		yamlData.set("pendingChallenges", rawPendingChallenges);
		yamlData.set("matchResults", rawMatchResults);
		
		yamlData.save(dataFile);
	}
	
	public void join(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		String playerName = player.getName();
		String clanNameColored = getColoredName();
		informPlayers(MessageType.CLAN_OTHERJOIN.getTranslation().getValue(playerName));
		addMember(playerName);
		removeInvited(playerName);
		bpPlayer.setClan(this);
		player.sendMessage(MessageType.CLAN_JOIN.getTranslation().getValue(clanNameColored));
	}

	public void kick(String targetName)
	{
		Player target = Bukkit.getPlayerExact(targetName);
		String clanNameColored = getColoredName();
		removeModerator(targetName);
		removeMember(targetName);
		informPlayers(MessageType.CLAN_OTHERKICK.getTranslation().getValue(targetName));
		
		if (target != null)
		{
			BPPlayer bpTarget = BPPlayer.get(target);
			bpTarget.setClan(null);
			target.sendMessage(MessageType.CLAN_KICK.getTranslation().getValue(clanNameColored));
		}
	}

	public void leave(Player player)
	{
		BPPlayer bpPlayer = BPPlayer.get(player);
		String playerName = player.getName();
		String clanNameColored = getColoredName();
		removeModerator(playerName);
		removeMember(playerName);
		bpPlayer.setClan(null);
		informPlayers(MessageType.CLAN_OTHERLEAVE.getTranslation().getValue(playerName));
		player.sendMessage(MessageType.CLAN_LEAVE.getTranslation().getValue(clanNameColored));
	}

	public void breakup(Player byWho)
	{
		clans.remove(this);
		for (String memberName : getMemberNames())
		{
			Player member = Bukkit.getPlayerExact(memberName);
			if (member != null)
			{
				BPPlayer bpMember = BPPlayer.get(member);
				
				bpMember.setClan(null);
				//			updatePrefix(member);
				member.sendMessage(MessageType.CLAN_OTHERBREAKUP.getTranslation().getValue());
			}
		}
		Player leader = Bukkit.getPlayerExact(getLeaderName());
		if (leader != null)
		{
			BPPlayer bpLeader = BPPlayer.get(leader);
			
			bpLeader.setClan(null);
			//		updatePrefix(leader);
			leader.sendMessage(MessageType.CLAN_OTHERBREAKUP.getTranslation().getValue());
		}
		byWho.sendMessage(MessageType.CLAN_BREAKUP.getTranslation().getValue());
	}
	
	public int getPoints()
	{
		int points = 0;
		
		for(CWMatchResult result : matchResults)
			if(result.hasWon())
				points++;
			else if(result.hasLost())
				if(--points < 0)
					points = 0;
		
		return points;
	}
	
	public int getWins()
	{
		int wins = 0;
		
		for(CWMatchResult result : matchResults)
			if(result.hasWon())
				wins++;
		
		return wins;
	}
	
	public int getDraws()
	{
		int draws = 0;
		
		for(CWMatchResult result : matchResults)
			if(result.wasDraw())
				draws++;
		
		return draws;
	}
	
	public int getLoses()
	{
		int loses = 0;
		
		for(CWMatchResult result : matchResults)
			if(result.hasLost())
				loses++;
		
		return loses;
	}
	
	public void informLeader(String info)
	{
		String leaderName = getLeaderName();
		Player leader = Bukkit.getPlayerExact(leaderName);
		if (leader != null)
			leader.sendMessage(info);
	}

	public void informMembers(String info)
	{
		for (String memberName : getMemberNames())
		{
			Player member = Bukkit.getPlayerExact(memberName);
			if (member != null)
				member.sendMessage(info);
		}
	}

	public void informPlayers(String info)
	{
		informLeader(info);
		informMembers(info);
	}

	public void addResult(CWMatchResult result)
	{
		matchResults.add(0, result);
	}
	
	public File getFile()
	{
		return new File("plugins/Breakpoint/clans/" + name + ".yml");
	}
	
	public ClanChallenge getPendingChallengeFrom(Clan clan)
	{
		for(ClanChallenge challenge : pendingChallenges)
			if(challenge.getChallengingClan().equals(clan))
				return challenge;
		
		return null;
	}
	
	public Player getLeader()
	{
		return Bukkit.getPlayerExact(leader);
	}
	
	public BPPlayer getBPLeader()
	{
		Player leader = getLeader();
		
		return leader == null ? null : BPPlayer.get(leader);
	}
	
	public List<Player> getOnlinePlayers()
	{
		List<Player> online = new ArrayList<Player>();
		List<String> names = getPlayerNames();
		
		for(String name : names)
		{
			Player player = Bukkit.getPlayerExact(name);
			
			if(player != null)
				online.add(player);
		}
		
		return online;
	}
	
	public List<String> getPlayerNames()
	{
		List<String> players = new ArrayList<String>();
		players.add(leader);
		players.addAll(getModeratorNames());
		players.addAll(getMemberNames());
		
		return players;
	}

	public String getLeaderName()
	{
		return leader;
	}

	public List<String> getModeratorNames()
	{
		return moderators;
	}

	public List<String> getMemberNames()
	{
		return members;
	}

	public List<String> getInvitedNames()
	{
		return invited;
	}
	
	public void addModerator(String playerName)
	{
		moderators.add(playerName);
	}
	
	public boolean removeModerator(String playerName)
	{
		return moderators.remove(playerName);
	}
	
	public void addMember(String playerName)
	{
		members.add(playerName);
	}

	public boolean removeMember(String playerName)
	{
		return members.remove(playerName);
	}

	public void addInvited(String playerName)
	{
		invited.add(playerName);
	}

	public boolean removeInvited(String playerName)
	{
		return invited.remove(playerName);
	}
	
	public final void setColoredNameRaw(String coloredNameRaw)
	{
		String coloredName = Clan.getColored(coloredNameRaw);
		String name = ChatColor.stripColor(coloredName);
		
		if(!Clan.isCorrectLength(name))
			throw new IllegalArgumentException(MessageType.COMMAND_CLAN_RENAME_EXE_INCORRECTLENGTH.getTranslation().getValue(Clan.minLength, Clan.maxLength));
		else if(!Clan.hasCorrectCharacters(name))
			throw new IllegalArgumentException(MessageType.COMMAND_CLAN_RENAME_EXE_BANNEDCHARACTERS.getTranslation().getValue());
		else if(Clan.exists(name))
			throw new IllegalArgumentException(MessageType.COMMAND_CLAN_RENAME_EXE_ALREADYEXISTS.getTranslation().getValue(name));
		
		this.coloredNameRaw = coloredNameRaw;
		this.coloredName = coloredName;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public String getColoredName()
	{
		return coloredName;
	}
	
	public String getColoredNameRaw()
	{
		return coloredNameRaw;
	}
	
	public boolean isAtLeastModerator(String playerName)
	{
		return isLeader(playerName) || isModerator(playerName);
	}
		
	public boolean isAtLeastMember(String playerName)
	{
		return isAtLeastModerator(playerName) || isMember(playerName);
	}
	
	public boolean isAtLeastInvited(String playerName)
	{
		return isAtLeastMember(playerName) || isInvited(playerName);
	}

	public boolean isLeader(String playerName)
	{
		return leader.equals(playerName);
	}
	
	public boolean isModerator(String playerName)
	{
		return moderators.contains(playerName);
	}

	public boolean isMember(String playerName)
	{
		return members.contains(playerName);
	}

	public boolean isInvited(String playerName)
	{
		return invited.contains(playerName);
	}

	public LinkedList<ClanChallenge> getPendingChallenges()
	{
		return pendingChallenges;
	}
	
	public LinkedList<CWMatchResult> getMatchResults()
	{
		return matchResults;
	}
}
