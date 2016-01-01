package cz.projectsurvive.me.limeth.breakpoint.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFGame;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFProperties;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.Team;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.sound.BPSound;
import cz.projectsurvive.me.limeth.breakpoint.sound.BPSoundSet;

public class SoundManager
{
	public static final String BASE_PATH = "projectsurvive.breakpoint";
	public static final String COMBO_PATH = BASE_PATH + ".combo";
	public static final String MISC_PATH = BASE_PATH + ".misc";
	public static final String COUNTDOWN_PATH = BASE_PATH + ".countdown";
	
	public static void informPlayersAboutTime(Breakpoint plugin, Game game, int secondsRemaining)
	{
		if(secondsRemaining % 60 == 0 && secondsRemaining > 60)
		{
			int minutesRemaining = secondsRemaining / 60;
			
			if(minutesRemaining == 5 || minutesRemaining == 3 || minutesRemaining == 2)
			{
				BPSound sound = BPSound.parse(minutesRemaining);
				playSetForPlayers(plugin, new BPSoundSet(sound, BPSound.MINUTES, BPSound.REMAINING), game);
			}
		}
		else if(secondsRemaining <= 60 && secondsRemaining % 10 == 0 && secondsRemaining > 10)
		{
			BPSound sound = BPSound.parse(secondsRemaining);
			playSetForPlayers(plugin, new BPSoundSet(sound, BPSound.SECONDS, BPSound.REMAINING), game);
		}
		else if(secondsRemaining <= 10 && secondsRemaining > 0)
		{
			BPSound sound = BPSound.parse(secondsRemaining);
			playSoundForPlayers(game, sound);
		}
	}
	
	public static void playSetForPlayers(Breakpoint plugin, BPSoundSet set, final Game game)
	{
		BukkitScheduler scheduler = Bukkit.getScheduler();
		double delay = 0;
		
		for(final BPSound sound : set.getSounds())
		{
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				@Override
				public void run()
				{
					playSoundForPlayers(game, sound);
				}
			}, (long) (delay * 20L));
			
			delay += sound.getLengthInSeconds();
		}
	}
	
	public static void playSoundAt(BPSound sound, Location loc)
	{
		for(Player player : Bukkit.getOnlinePlayers())
		{
			BPPlayer bpPlayer = BPPlayer.get(player);
			
			if(bpPlayer.isInGame())
				tryPlay(player, bpPlayer, sound, loc);
		}
	}
	
	public static void playTeamSound(BPSound sound, CTFGame game, Team team, float volume, float pitch)
	{
		for(BPPlayer bpPlayer : game.getPlayers())
		{
			Team pTeam = ((CTFProperties) bpPlayer.getGameProperties()).getTeam();
			
			if(pTeam == team)
			{
				Player player = bpPlayer.getPlayer();
				tryPlay(player, bpPlayer, sound, volume, pitch);
			}
		}
	}
	
	public static void playTeamSound(BPSound sound, CTFGame game, Team team, float volume)
	{
		playTeamSound(sound, game, team, volume, 1F);
	}
	
	public static void playTeamSound(BPSound sound, CTFGame game, Team team)
	{
		playTeamSound(sound, game, team, 1F);
	}
	
	public static void playSoundForPlayers(Game game, BPSound sound)
	{
		for(BPPlayer bpPlayer : BPPlayer.onlinePlayers)
		{
			Game pGame = bpPlayer.getGame();
			
			if(game.equals(pGame))
			{
				Player player = bpPlayer.getPlayer();
				
				tryPlay(player, bpPlayer, sound);
			}
		}
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound, Location loc, float volume, float pitch)
	{
		if(!bpPlayer.getSettings().hasExtraSounds())
			return;
		
		sound.play(player, loc, volume, pitch);
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound, float volume, float pitch)
	{
		tryPlay(player, bpPlayer, sound, player.getLocation(), volume, pitch);
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound, float volume)
	{
		tryPlay(player, bpPlayer, sound, volume, 1F);
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound)
	{
		tryPlay(player, bpPlayer, sound, 1F);
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound, Location loc, float volume)
	{
		tryPlay(player, bpPlayer, sound, loc, volume, 1F);
	}
	
	public static void tryPlay(Player player, BPPlayer bpPlayer, BPSound sound, Location loc)
	{
		tryPlay(player, bpPlayer, sound, loc, 1F);
	}
	
	//VANILLA
	
	public static void playTeamSound(CTFGame game, Location loc, Sound sound, float volume, float pitch, Team team)
	{
		for (BPPlayer bpPlayer : game.getPlayersInTeam(team))
		{
			Player player = bpPlayer.getPlayer();
			
			player.playSound(loc, sound, volume, pitch);
		}
	}

	public static void playTeamSound(CTFGame game, Sound sound, float volume, float pitch, Team team)
	{
		for (BPPlayer bpPlayer : game.getPlayersInTeam(team))
		{
			Player player = bpPlayer.getPlayer();
			Location loc = player.getLocation();
			
			player.playSound(loc, sound, volume, pitch);
		}
	}
}
