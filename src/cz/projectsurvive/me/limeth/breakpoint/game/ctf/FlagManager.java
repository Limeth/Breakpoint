package cz.projectsurvive.me.limeth.breakpoint.game.ctf;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPArmor;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.NametagEditManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SoundManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class FlagManager
{
	private final CTFGame game;
	private Location[] defaultFlagLocations;
	private final EnderCrystal[] flags;
	private final BPPlayer[] holders;
	private int[] score;
	private final int[] timeoutIn;
	private final boolean[] isDropped;

	public FlagManager(CTFGame game)
	{
		this.game = game;
		defaultFlagLocations = new Location[2];
		flags = new EnderCrystal[2];
		holders = new BPPlayer[2];
		score = new int[2];
		timeoutIn = new int[2];
		isDropped = new boolean[] { false, false };
	}

	public void startLoops()
	{
		startPotionLoop();
		startCompassUpdateLoop();
		startTimeoutLoop();
		startHolderEffectLoop(1);
		startSpawnCampProtectionLoop();
	}
	
	public void startSpawnCampProtectionLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				damageHoldersNearSpawns();
			}
		}, 10L, 20L);
	}

	public void startPotionLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				potionLoopTick();
				damageHolders();
			}
		}, 80L, 80L);
	}

	public void startCompassUpdateLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				compassUpdateLoopTick();
			}
		}, 10L, 10L);
	}

	public void startTimeoutLoop()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				timeoutLoopTick();
			}
		}, 20L, 20L);
	}

	public void startHolderEffectLoop(double seconds)
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				holderEffectLoopTick();
			}
		}, (long) (20L * seconds), (long) (20L * seconds));
	}
	
	private void damageHoldersNearSpawns()
	{
		for (int i = 0; i < holders.length; i++)
		{
			BPPlayer bpHolder = holders[i];
			
			if (bpHolder == null)
				continue;
			
			Team team = Team.getById(i);
			Team oppositeTeam = Team.getOpposite(team);
			Player holder = bpHolder.getPlayer();
			Location spawnLoc = game.getSpawnLocation(oppositeTeam);
			Location holderLoc = holder.getLocation();
			double dist = spawnLoc.distance(holderLoc);
			
			if(dist <= 8)
			{
				holder.damage((8.0 - dist) / 2.0);
				holder.sendMessage(MessageType.FLAG_WARN_NEARSPAWN.getTranslation().getValue());
			}
		}
	}

	public void holderEffectLoopTick()
	{
		for (int i = 0; i < holders.length; i++)
		{
			BPPlayer bpHolder = holders[i];
			
			if (bpHolder == null)
				continue;
			
			Team team = Team.getById(i);
			Player holder = bpHolder.getPlayer();
			
			showHolderEffect(holder, team);
		}
	}

	public void potionLoopTick()
	{
		for (BPPlayer bpHolder : holders)
			if (bpHolder != null)
			{
				Player holder = bpHolder.getPlayer();
				
				slowDown(holder);
			}
	}

	public void timeoutLoopTick()
	{
		for (int i = 0; i < 2; i++)
			if (isDropped[i])
				if (timeoutIn[i] >= 0)
				{
					if (timeoutIn[i] == 0)
					{
						Team team = Team.getById(i);
						
						timeoutFlag(team);
					}
					timeoutIn[i]--;
				}
	}

	public void compassUpdateLoopTick()
	{
		updateCompasses();
	}

	public void updateCompasses()
	{
		if (game.hasRoundEnded())
			return;
		for (BPPlayer bpPlayer : game.getPlayers())
		{
			Team flagTeam = getFlagTeam(bpPlayer);
			
			if(flagTeam != null)
			{
				updateCompass(bpPlayer, flagTeam);
				continue;
			}
			
			Team team = ((CTFProperties) bpPlayer.getGameProperties()).getTeam();
			
			if (team != null)
				updateCompass(bpPlayer, team);
		}
	}

	public void updateCompass(BPPlayer bpPlayer, Team team)
	{
		if (game.hasRoundEnded())
			return;
		
		Team oppositeTeam = Team.getOpposite(team);
		Location loc = getFlagLocation(oppositeTeam);
		
		if (loc == null)
			return;
		
		Player player = bpPlayer.getPlayer();
		
		player.setCompassTarget(loc);
	}

	public static void slowDown(Player player)
	{
		player.removePotionEffect(PotionEffectType.SLOW);
		player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 160, 1));
	}

	public void reset()
	{
		removeFlags();
		removeHolders();
		score = new int[2];
		game.updateProgressObjectiveScore();
	}

	public void spawnFlags()
	{
		CTFMap curMap = game.getCurrentMap();
		defaultFlagLocations = curMap.getTeamFlags();
		for (int i = 0; i < 2; i++)
		{
			Team team = Team.getById(i);
			
			spawnFlagAtDefaultLocation(team);
		}
	}

	public EnderCrystal spawnFlag(Location loc, Team team)
	{
		int teamId = Team.getId(team);
		loc = ground(loc);
		World world = loc.getWorld();
		Chunk chunk = world.getChunkAt(loc);
		
		if (!chunk.isLoaded())
			chunk.load();
		
		EnderCrystal ec = (EnderCrystal) world.spawnEntity(loc, EntityType.ENDER_CRYSTAL);
		flags[teamId] = ec;
		
		return ec;
	}

	public EnderCrystal spawnFlagAtDefaultLocation(Team team)
	{
		int teamId = Team.getId(team);
		isDropped[teamId] = false;
		return spawnFlag(defaultFlagLocations[teamId], team);
	}

	public void removeHolders()
	{
		for (int i = 0; i < 2; i++)
			if (holders[i] != null)
			{
				BPPlayer bpPlayer = holders[i];
				Player player = bpPlayer.getPlayer();
				holders[i] = null;
				bpPlayer.setPlayerListName();
				NametagEditManager.updateNametag(bpPlayer);
				giveCompass(player);
			}
	}

	public void removeFlags()
	{
		for (int i = 0; i < 2; i++)
			removeFlag(Team.getById(i));
	}

	private boolean removeFlag(Team team)
	{
		int teamId = Team.getId(team);
		
		if (flags[teamId] != null)
		{
			Location loc = flags[teamId].getLocation();
			World world = loc.getWorld();
			Chunk chunk = world.getChunkAt(loc);
			if (!chunk.isLoaded())
				chunk.load();
			flags[teamId].remove();
			flags[teamId] = null;
			return true;
		}
		return false;
	}

	public void onTryFlagTake(EntityDamageByEntityEvent event)
	{
		Entity damagerEntity = event.getDamager();
		EntityType damagerEntityType = damagerEntity.getType();
		
		if (damagerEntityType == EntityType.PLAYER)
		{
			Player damager = (Player) damagerEntity;
			
			if (damager.isDead())
				return;
			
			BPPlayer bpDamager = BPPlayer.get(damager);
			
			if(!bpDamager.isInGame())
				return;
			
			Game damagerGame = bpDamager.getGame();
			
			if(!damagerGame.equals(game))
				return;
			
			EnderCrystal flag = (EnderCrystal) event.getEntity();
			Team damagerTeam = ((CTFProperties) bpDamager.getGameProperties()).getTeam();
			
			if(damagerTeam != null)
				flagTouch(bpDamager, flag, damagerTeam, true);
		}
	}
	
	public void flagTouch(BPPlayer bpPlayer, EnderCrystal flag, Team damagerTeam, boolean chain)
	{
		Team flagTeam = getFlagTeam(flag);
		
		if(flagTeam != null)
		{
			Player player = bpPlayer.getPlayer();
			if(Team.areEnemies(damagerTeam, flagTeam))
			{
				takeFlag(bpPlayer, flag, damagerTeam, flagTeam);
				flag.remove();
				player.sendMessage(MessageType.FLAG_STEAL.getTranslation().getValue());
			}
			else
				if (isAtDefaultLocation(flag))
				{
					if (isHoldingFlag(bpPlayer))
						captureFlag(bpPlayer, damagerTeam);
					else
						player.sendMessage(MessageType.FLAG_INFO.getTranslation().getValue());
				}
				else
					returnFlag(bpPlayer, flagTeam);
		}
		else
		{
			flag.remove();
			
			if(chain)
				for(Entity entity : flag.getNearbyEntities(0, 0, 0))
					if(entity instanceof EnderCrystal)
					{
						EnderCrystal cur = (EnderCrystal) entity;
						
						flagTouch(bpPlayer, cur, damagerTeam, false);
					}
		}
	}

	public void takeFlag(BPPlayer bpHolder, EnderCrystal flag, Team damagerTeam, Team flagTeam)
	{
		int flagTeamId = Team.getId(flagTeam);
		holders[flagTeamId] = bpHolder;
		String holderName = bpHolder.getPVPName();
		isDropped[flagTeamId] = false;
		Player holder = bpHolder.getPlayer();
		
		SoundManager.playTeamSound(game, Sound.ENDERDRAGON_HIT, 1F, 1F, flagTeam);
		SoundManager.playTeamSound(game, Sound.ORB_PICKUP, 1F, 1F, damagerTeam);
		bpHolder.getStatistics().increaseFlagTakes(1);
		Achievement.checkFlagTakes(bpHolder);
		slowDown(holder);
		showHolderEffect(holder, flagTeam);
		colorArmorByFlag(holder, flagTeam);
		giveCompass(holder);
		NametagEditManager.updateNametag(bpHolder);
		bpHolder.setPlayerListName();
		
		if(flagTeam == Team.RED)
			game.broadcast(MessageType.FLAG_TAKE_RED.getTranslation().getValue(holderName));
		else if(flagTeam == Team.BLUE)
			game.broadcast(MessageType.FLAG_TAKE_BLUE.getTranslation().getValue(holderName));
	}

	public void dropFlag(BPPlayer bpHolder)
	{
		Player holder = bpHolder.getPlayer();
		Location loc = holder.getLocation();
		Team flagTeam = getFlagTeam(bpHolder);
		Team oppositeTeam = Team.getOpposite(flagTeam);
		String holderName = bpHolder.getPVPName();
		int flagTeamId = Team.getId(flagTeam);
		
		SoundManager.playTeamSound(game, Sound.ENDERDRAGON_HIT, 1F, 1F, oppositeTeam);
		SoundManager.playTeamSound(game, Sound.ORB_PICKUP, 1F, 1F, flagTeam);
		
		spawnFlag(loc, flagTeam);
		holders[flagTeamId] = null;
		isDropped[flagTeamId] = true;
		timeoutIn[flagTeamId] = 10;
		
		giveCompass(holder);
		NametagEditManager.updateNametag(bpHolder);
		bpHolder.setPlayerListName();
		
		if(flagTeam == Team.RED)
			game.broadcast(MessageType.FLAG_DROP_RED.getTranslation().getValue(holderName));
		else if(flagTeam == Team.BLUE)
			game.broadcast(MessageType.FLAG_DROP_BLUE.getTranslation().getValue(holderName));
	}

	public void returnFlag(BPPlayer bpPlayer, Team flagTeam)
	{
		Team oppositeTeam = Team.getOpposite(flagTeam);
		String playerName = bpPlayer.getPVPName();
		int flagTeamId = Team.getId(flagTeam);
		Player player = bpPlayer.getPlayer();
		
		SoundManager.playTeamSound(game, Sound.ENDERDRAGON_HIT, 1F, 1F, oppositeTeam);
		SoundManager.playTeamSound(game, Sound.ORB_PICKUP, 1F, 1F, flagTeam);
		removeFlag(flagTeam);
		isDropped[flagTeamId] = false;
		spawnFlagAtDefaultLocation(flagTeam);
		giveCompass(player);
		
		if(flagTeam == Team.RED)
			game.broadcast(MessageType.FLAG_RETURN_RED.getTranslation().getValue(playerName));
		else if(flagTeam == Team.BLUE)
			game.broadcast(MessageType.FLAG_RETURN_BLUE.getTranslation().getValue(playerName));
	}

	@SuppressWarnings("deprecation")
	public void captureFlag(BPPlayer bpHolder, Team playerTeam)
	{
		Team oppositeTeam = Team.getOpposite(playerTeam);
		int oppositeTeamId = Team.getId(oppositeTeam);
		holders[oppositeTeamId] = null;
		String holderName = bpHolder.getPVPName();
		Player holder = bpHolder.getPlayer();
		
		increaseScore(playerTeam);
		spawnFlagAtDefaultLocation(oppositeTeam);
		SoundManager.playTeamSound(game, Sound.WITHER_SHOOT, 1F, 0.5F, oppositeTeam);
		SoundManager.playTeamSound(game, Sound.LEVEL_UP, 1F, 1F, playerTeam);
		holder.removePotionEffect(PotionEffectType.SLOW);
		bpHolder.getStatistics().increaseFlagCaptures();
		Achievement.checkFlagCaptures(bpHolder);
		bpHolder.colorArmor();
		giveCompass(holder);
		holder.updateInventory();
		NametagEditManager.updateNametag(bpHolder);
		bpHolder.setPlayerListName();
		bpHolder.addMoney(CTFGame.emeraldsForCapture, true, true);
		
		game.updateProgressObjectiveScores(bpHolder);
		
		if(oppositeTeam == Team.RED)
			game.broadcast(MessageType.FLAG_CAPTURE_RED.getTranslation().getValue(holderName));
		else if(oppositeTeam == Team.BLUE)
			game.broadcast(MessageType.FLAG_CAPTURE_BLUE.getTranslation().getValue(holderName));
	}

	public void timeoutFlag(Team flagTeam)
	{
		Team oppositeTeam = Team.getOpposite(flagTeam);
		
		removeFlag(flagTeam);
		spawnFlagAtDefaultLocation(flagTeam);
		SoundManager.playTeamSound(game, Sound.ENDERDRAGON_HIT, 1F, 1F, oppositeTeam);
		SoundManager.playTeamSound(game, Sound.ORB_PICKUP, 1F, 1F, flagTeam);
		
		if(flagTeam == Team.RED)
			game.broadcast(MessageType.FLAG_SHATTER_RED.getTranslation().getValue());
		else if(flagTeam == Team.BLUE)
			game.broadcast(MessageType.FLAG_SHATTER_BLUE.getTranslation().getValue());
	}

	public boolean isHoldingFlag(BPPlayer bpPlayer)
	{
		for (BPPlayer p : holders)
			if (p != null)
				if (p.equals(bpPlayer))
					return true;
		return false;
	}

	public Team getFlagTeam(BPPlayer bpPlayer)
	{
		for (int i = 0; i < 2; i++)
			if (holders[i] != null)
				if (holders[i].equals(bpPlayer))
					return Team.getById(i);
		return null;
	}

	public Team getFlagTeam(EnderCrystal ec)
	{
		for (int i = 0; i < 2; i++)
			if (flags[i] != null)
				if (flags[i].equals(ec))
					return Team.getById(i);
		return null;
	}

	public boolean isAtDefaultLocation(EnderCrystal ec)
	{
		Team team = getFlagTeam(ec);
		if (team != null)
		{
			int teamId = Team.getId(team);
			Location loc = ec.getLocation();
			Location defaultLoc = defaultFlagLocations[teamId];
			return sameXZ(loc, defaultLoc);
		}
		return false;
	}

	private boolean sameXZ(Location loc1, Location loc2)
	{
		double x1 = loc1.getX();
		double z1 = loc1.getZ();
		double x2 = loc2.getX();
		double z2 = loc2.getZ();
		return x1 == x2 && z1 == z2;
	}

	public boolean isTeamFlag(EnderCrystal ec)
	{
		for (int i = 0; i < 2; i++)
			if (flags[i] != null)
				if (flags[i].equals(ec))
					return true;
		return false;
	}

	public void playTeamSoundAtFlagLocation(Sound sound, float volume, float pitch, Team team)
	{
		int teamId = Team.getId(team);
		Location loc = (holders[teamId] != null ? holders[teamId].getPlayer().getLocation() : defaultFlagLocations[teamId]);
		SoundManager.playTeamSound(game, loc, sound, volume, pitch, team);
	}

	public void increaseScore(Team team)
	{
		int teamId = Team.getId(team);
		score[teamId]++;
		game.updateProgressObjectiveScore();
	}

	public void damageHolders()
	{
		for (int i = 0; i < 2; i++)
			if (holders[i] != null)
				damagePlayer(holders[i].getPlayer());
	}

	private void damagePlayer(Player target)
	{	
		if (!target.isDead())
		{
			if (target.getGameMode() == GameMode.CREATIVE)
				return;
			
			double newHealth = ((Damageable) target).getHealth() - 1;
			
			if (newHealth < 0)
				newHealth = 0;
			
			target.setHealth(newHealth);
			target.setLastDamageCause(new EntityDamageEvent(target, DamageCause.CUSTOM, 1.0));
		}
	}

	public Location getFlagLocation(Team team)
	{
		int teamId = Team.getId(team);
		
		if (holders[teamId] != null)
			return holders[teamId].getPlayer().getLocation();
		else
			if (flags[teamId] != null)
				return flags[teamId].getLocation();
			else
				return null;
	}

	public int[] getScore()
	{
		return score;
	}

	public static void showHolderEffect(Player player, Team flagTeam)
	{
		Location loc = player.getLocation();
		World world = loc.getWorld();
		int potionId;
		
		if(flagTeam == Team.RED)
			potionId = 5;
		else if(flagTeam == Team.BLUE)
			potionId = 0;
		else
			return;
		
		world.playEffect(loc, Effect.POTION_BREAK, potionId);
	}

	public static void colorArmorByFlag(Player player, Team flagTeam)
	{
		PlayerInventory inv = player.getInventory();
		ItemStack[] armor = inv.getArmorContents();
		for (ItemStack piece : armor)
		{
			Material mat = piece.getType();
			if (piece != null && BPArmor.getTypeId(mat) >= 0)
			{
				Color color = flagTeam.getColor();
				BPArmor.colorArmor(piece, color);
			}
		}
		inv.setArmorContents(armor);
	}
	
	@Deprecated
	public static Color getRandomTeamColor(Team team, int spread)
	{
		Random rnd = new Random();
		if (team == Team.RED)
			return Color.fromRGB(255, rnd.nextInt(spread), rnd.nextInt(spread));
		else if (team == Team.BLUE)
			return Color.fromRGB(rnd.nextInt(spread), rnd.nextInt(spread), 255);
		else
			return null;
	}

	public static Location ground(Location loc)
	{
		World world = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();
		if (world.getBlockAt(x, y, z).getType() != Material.AIR)
			while (world.getBlockAt(x, y, z).getType() != Material.AIR)
				y++;
		else
			while (world.getBlockAt(x, y, z).getType() == Material.AIR && y > 0)
				y--;
		return new Location(world, x, y + 1, z);
	}

	public static void giveCompass(Player player)
	{
		PlayerInventory pi = player.getInventory();
		giveCompass(player, pi);
	}

	public static void giveCompass(Player player, PlayerInventory pi)
	{
		pi.setItem(8, getCompass());
	}

	public static ItemStack getCompass()
	{
		ItemStack is = new ItemStack(Material.COMPASS);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(MessageType.MENU_COMPASS_NAME.getTranslation().getValue());
		List<String> lore = MessageType.MENU_COMPASS_DESC.getTranslation().getValues();
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
