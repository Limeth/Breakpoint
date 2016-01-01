package cz.projectsurvive.me.limeth.breakpoint.game.cw;

import java.util.Calendar;
import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.GameType;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFGame;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFMap;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFProperties;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.FlagManager;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.Team;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.SoundManager;
import cz.projectsurvive.me.limeth.breakpoint.perks.Perk;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;
import cz.projectsurvive.me.limeth.breakpoint.sound.BPSound;
import cz.projectsurvive.me.limeth.breakpoint.statistics.CWMatchResult;

public class CWGame extends CTFGame
{
	private final int[] wins = new int[2];
	private int round;
	private boolean begun;
	private final CWScheduler scheduler;
	private ClanChallenge day;
	
	public CWGame(String name, Location signLoc, Location characterSelectionLocation, LinkedList<CTFMap> maps, LinkedList<ClanChallenge> days)
	{
		super(GameType.CW, name, signLoc, null, characterSelectionLocation, maps, false);
		scheduler = new CWScheduler(this, days);
		setDay();
	}
	
	public CWGame(String name, Location signLoc)
	{
		this(name, signLoc, null, new LinkedList<CTFMap>(), new LinkedList<ClanChallenge>());
	}
	
	@Override
	public boolean isPlayable()
	{
		if(!super.isSuperPlayable())
			return false;
		return getFlagManager() != null && characterSelectionLocation != null;
	}
	
	@Override
	public void changeMapExtra()
	{
		super.changeMapExtra();
		
		if(canBegin())
			if(!hasBegun())
			{
				broadcastGlobally(MessageType.CW_BROADCAST_MATCHSTART.getTranslation().getValue());
				begun = true;
			}
	}
	
	@Override
	protected void endRoundExtra()
	{
		FlagManager flm = getFlagManager();
		flm.removeFlags();
		flm.removeHolders();
		int[] score = flm.getScore();
		broadcast(ChatColor.GRAY + "---------------------------------");
		if(!hasBegun())
		{
			if(score[0] == score[1])
			{
				broadcast(MessageType.RESULT_CTF_DRAW.getTranslation().getValue());
				SoundManager.playTeamSound(this, Sound.ARROW_HIT, 16F, 0.5F, Team.RED);
				SoundManager.playTeamSound(this, Sound.ARROW_HIT, 16F, 0.5F, Team.BLUE);
			}
			else if(score[0] > score[1])
			{
				broadcast(MessageType.RESULT_CTF_WIN_RED.getTranslation().getValue());
				SoundManager.playTeamSound(this, Sound.ENDERDRAGON_DEATH, 16F, 0.5F, Team.BLUE);
				SoundManager.playTeamSound(this, Sound.LEVEL_UP, 16F, 4F, Team.RED);
				spawnFireworks(Team.RED);
				if(score[1] <= 0)
				{
					SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.RED);
					SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.BLUE);
				}
			}
			else if(score[1] > score[0])
			{
				broadcast(MessageType.RESULT_CTF_WIN_BLUE.getTranslation().getValue());
				SoundManager.playTeamSound(this, Sound.ENDERDRAGON_DEATH, 16F, 0.5F, Team.RED);
				SoundManager.playTeamSound(this, Sound.LEVEL_UP, 16F, 4F, Team.BLUE);
				spawnFireworks(Team.BLUE);
				
				if(score[0] <= 0)
				{
					SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.BLUE);
					SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.RED);
				}
			}
		}
		else 
		{
			if(score[0] == score[1])
			{
				Configuration config = Breakpoint.getBreakpointConfig();
				
				broadcastGlobally(MessageType.RESULT_CW_DRAW.getTranslation().getValue());
				SoundManager.playTeamSound(this, Sound.ARROW_HIT, 16F, 0.5F, Team.RED);
				SoundManager.playTeamSound(this, Sound.ARROW_HIT, 16F, 0.5F, Team.BLUE);
				
				if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= config.getCWEndHour())
				{
					endMatch();
					return;
				}
			}
			else if(score[0] > score[1])
			{
				Clan[] clans = day.getClans();
				Configuration config = Breakpoint.getBreakpointConfig();
				
				broadcastGlobally(MessageType.RESULT_CW_WIN.getTranslation().getValue(clans[0].getColoredName()));
				SoundManager.playTeamSound(this, Sound.ENDERDRAGON_DEATH, 16F, 0.5F, Team.BLUE);
				SoundManager.playTeamSound(this, Sound.LEVEL_UP, 16F, 4F, Team.RED);
				spawnFireworks(Team.RED);
				
				if(score[1] <= 0)
				{
					SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.RED);
					SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.BLUE);
				}
				
				if(++wins[0] >= config.getCWWinLimit() || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= config.getCWEndHour())
				{
					endMatch();
					return;
				}
			}
			else if(score[1] > score[0])
			{
				Clan[] clans = day.getClans();
				Configuration config = Breakpoint.getBreakpointConfig();
				
				broadcastGlobally(MessageType.RESULT_CTF_WIN_BLUE.getTranslation().getValue(clans[1].getColoredName()));
				SoundManager.playTeamSound(this, Sound.ENDERDRAGON_DEATH, 16F, 0.5F, Team.RED);
				SoundManager.playTeamSound(this, Sound.LEVEL_UP, 16F, 4F, Team.BLUE);
				spawnFireworks(Team.BLUE);
				
				if(score[0] <= 0)
				{
					SoundManager.playTeamSound(BPSound.FLAWLESS_VICTORY, this, Team.BLUE);
					SoundManager.playTeamSound(BPSound.HUMILIATING_DEFEAT, this, Team.RED);
				}
				
				if(++wins[1] >= config.getCWWinLimit() || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= config.getCWEndHour())
				{
					endMatch();
					return;
				}
			}
			
			broadcastGlobally(MessageType.RESULT_CW_MATCH_SCORES.getTranslation().getValue(wins[0], wins[1]));
		}
		broadcast(ChatColor.GRAY + "---------------------------------");
	}
	
	public void endMatch()
	{
		Clan[] clans = day.getClans();
		
		if(wins[0] > wins[1])
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			broadcastGlobally(MessageType.RESULT_CW_MATCH_WIN.getTranslation().getValue(clans[0].getColoredName()));
			awardPlayersInTeam(Team.RED, config.getCWEmeraldsForTotalWin());
		}
		else if(wins[1] > wins[0])
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			
			broadcastGlobally(MessageType.RESULT_CW_MATCH_WIN.getTranslation().getValue(clans[1].getColoredName()));
			awardPlayersInTeam(Team.BLUE, config.getCWEmeraldsForTotalWin());
		}
		else
			broadcastGlobally(MessageType.RESULT_CW_MATCH_DRAW.getTranslation().getValue());
		
		broadcastGlobally(MessageType.RESULT_CW_MATCH_SCORES.getTranslation().getValue(wins[0], wins[1]));
		
		clans[0].addResult(new CWMatchResult(clans[1], new int[] {wins[0], wins[1]}));
		clans[1].addResult(new CWMatchResult(clans[0], new int[] {wins[1], wins[0]}));
		
		begun = false;
		setRound(0);
		wins[0] = 0;
		wins[1] = 0;
	}

	@Override
	public void join(BPPlayer bpPlayer) throws Exception
	{
		if(day == null)
			throw new Exception(MessageType.LOBBY_GAME_CW_NOTALLOWED.getTranslation().getValue());
		
		Team team = day.getTeam(bpPlayer);
		
		if(team == null)
			throw new Exception(MessageType.LOBBY_GAME_CW_NOTALLOWED.getTranslation().getValue());
		
		if(!canJoinTeam(team))
			throw new Exception(MessageType.LOBBY_GAME_CW_TEAMFULL.getTranslation().getValue(day.getMaximumPlayers()));
		
		CWProperties props = new CWProperties(this, bpPlayer);
		bpPlayer.setGameProperties(props);
		super.superJoin(bpPlayer);
		props.chooseTeam(team);
	}
	
	@Override
	public boolean canJoinTeam(Team team)
	{
		int maxPlayers = day.getMaximumPlayers();
		int players = getPlayersInTeam(team).size();
		
		return players < maxPlayers;
	}
	
	@Override
	public void spawn(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		if(player.isDead())
			return;
		CTFProperties props = ((CTFProperties) bpPlayer.getGameProperties());
		Team team = props.getTeam();
		bpPlayer.setSpawnTime(System.currentTimeMillis());
		bpPlayer.purify();
		props.equip();
		Perk.onSpawn(bpPlayer);
		CharacterType ct = props.getCharacterType();
		if(ct == null)
		{
			bpPlayer.teleport(characterSelectionLocation, false);
			return;
		}
		Location spawnLoc = getSpawnLocation(team);
		bpPlayer.teleport(spawnLoc, true);
	}
	
	@Override
	protected void saveExtra(YamlConfiguration yml)
	{
		yml.set(getName() + ".charSelLoc", characterSelectionLocation.getWorld().getName() + "," + characterSelectionLocation.getX() + "," + characterSelectionLocation.getY() + "," + characterSelectionLocation.getZ() + "," + characterSelectionLocation.getYaw() + "," + characterSelectionLocation.getPitch());
		
		LinkedList<ClanChallenge> days = scheduler.getDays();
		LinkedList<String> rawDays = new LinkedList<String>();
		
		for(ClanChallenge day : days)
			rawDays.add(day.serialize());
		
		yml.set(getName() + ".schedule", rawDays);
	}
	
	public boolean canBegin()
	{
		int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		Configuration config = Breakpoint.getBreakpointConfig();
		
		return hour >= config.getCWBeginHour() && hour < config.getCWEndHour() && day != null;
	}
	
	public static boolean hasToEnd()
	{
		Configuration config = Breakpoint.getBreakpointConfig();
		
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= config.getCWEndHour();
	}
	
	public void broadcastGlobally(String message)
	{
		if(day == null)
			throw new IllegalArgumentException("day == null");
		
		Breakpoint.broadcast(MessageType.CW_BROADCAST_PREFIX.getTranslation().getValue(getName(), day.getChallengingClan().getColoredName(), day.getChallengedClan().getColoredName(), day.getMaximumPlayers()) + " " + ChatColor.GOLD + message);
	}

	public void setDay()
	{
		day = scheduler.getCurrentDay();
	}

	public ClanChallenge getDay()
	{
		return day;
	}

	public void setDay(ClanChallenge day)
	{
		this.day = day;
	}

	public CWScheduler getScheduler()
	{
		return scheduler;
	}

	public boolean hasBegun()
	{
		return begun;
	}

	public void setBegun(boolean begun)
	{
		this.begun = begun;
	}

	public int[] getWins()
	{
		return wins;
	}

	public int getRound()
	{
		return round;
	}

	public void setRound(int round)
	{
		this.round = round;
	}
}
