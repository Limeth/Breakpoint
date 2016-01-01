package cz.projectsurvive.me.limeth.breakpoint.listeners;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.achievements.Achievement;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;
import cz.projectsurvive.me.limeth.breakpoint.game.BPMap;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameProperties;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.AbilityManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.SoundManager;
import cz.projectsurvive.me.limeth.breakpoint.perks.Perk;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.Settings;
import cz.projectsurvive.me.limeth.breakpoint.sound.BPSound;
import cz.projectsurvive.me.limeth.breakpoint.statistics.PlayerStatistics;

public class PVPListener implements Listener
{
	Breakpoint plugin;

	public PVPListener(Breakpoint p)
	{
		plugin = p;
	}

	@EventHandler
	public void onPotionSplash(PotionSplashEvent event)
	{
		ThrownPotion ePotion = event.getPotion();
		Entity eShooter = ePotion.getShooter();
		if (eShooter instanceof Player)
		{
			Player shooter = (Player) eShooter;
			BPPlayer bpShooter = BPPlayer.get(shooter);
			if (bpShooter.isInGame())
			{
				Game game = bpShooter.getGame();
				
				for (LivingEntity eTarget : event.getAffectedEntities())
					if (eTarget instanceof Player)
					{
						Player target = (Player) eTarget;
						BPPlayer bpTarget = BPPlayer.get(target);
						
						if (bpTarget.isInGameWith(bpShooter))
							game.getListener().onPlayerSplashedByPotion(event, bpShooter, bpTarget);
						else
							event.setIntensity(eTarget, 0);
					}
					else
						event.setIntensity(eTarget, 0);
			}
			else
				event.setCancelled(true);
		}
		else
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityShootBow(EntityShootBowEvent event)
	{
		LivingEntity eShooter = event.getEntity();
		
		if(!(eShooter instanceof Player))
			return;
		
		Player shooter = (Player) eShooter;
		BPPlayer bpShooter = BPPlayer.get(shooter);
		Game game = bpShooter.getGame();
		
		if(game == null)
			return;
		
		game.getListener().onPlayerShootBow(event, bpShooter);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		event.setDeathMessage(null);
		Player player = event.getEntity();
		BPPlayer bpPlayer = BPPlayer.get(player);
		Game pGame = bpPlayer.getGame();
		Player killer = player.getKiller();
		String playerPVPName = bpPlayer.getPVPName();
		
		if(bpPlayer.isPlaying())
			bpPlayer.getStatistics().increaseDeaths();
		
		bpPlayer.setKilledThisLife(0);
		bpPlayer.setMultikills(0);
		bpPlayer.setLastTimeKilled(0);
		
		if (killer != null)
		{
			BPPlayer bpKiller = BPPlayer.get(killer);
			
			if(bpPlayer.isInGameWith(bpKiller))
			{
				Location kLoc = killer.getLocation();
				String killerName = killer.getName();
				String killerPVPName = bpKiller.getPVPName();
				PlayerStatistics kStats = bpKiller.getStatistics();
				GameProperties kProps = bpKiller.getGameProperties();
				
				if(kProps.hasCharacterType())
				{
					CharacterType ct = kProps.getCharacterType();
					
					kStats.increaseKills(ct);
					Achievement.checkCharacterKills(bpKiller, ct);
				}
				
				kStats.increaseKills();
				bpKiller.addMoney(1, true, true);
				Achievement.checkKills(bpKiller);
				PlayerManager.executeMultikill(bpKiller, kLoc, 5);
				PlayerManager.executeKillingSpree(bpKiller, kLoc);
				killer.setLevel(killer.getLevel() + 1);
				
				if (pGame.getFirstBloodPlayerName() == null)
				{
					if (Bukkit.getOnlinePlayers().length >= Bukkit.getMaxPlayers() / 2)
						bpKiller.checkAchievement(AchievementType.FIRST_BLOOD);
					
					pGame.setFirstBloodPlayerName(killerName);
					pGame.broadcast(ChatColor.DARK_RED + "" + ChatColor.BOLD + "> FIRST BLOOD! " + killerPVPName + ChatColor.DARK_RED + ChatColor.BOLD + " <", false);
					PlayerManager.spawnRandomlyColoredFirework(kLoc);
					SoundManager.playSoundAt(BPSound.FIRST_BLOOD, kLoc);
				}
				
				pGame.setLastBloodPlayerName(killerName);
				killer.playSound(kLoc, Sound.NOTE_PLING, 1F, 4F);
				
				if (!bpPlayer.getSettings().hasDeathMessages())
					player.sendMessage(MessageType.PVP_KILLINFO_KILLEDBY.getTranslation().getValue(killerPVPName));
				
				if (!bpKiller.getSettings().hasDeathMessages())
					killer.sendMessage(MessageType.PVP_KILLINFO_YOUKILLED.getTranslation().getValue(playerPVPName));
				
				pGame.broadcastDeathMessage(playerPVPName, killerPVPName);
			
				//{{revenge
				if(pGame.getPlayers().size() >= 5)
				{
					BPPlayer ltkb = bpKiller.getLastTimeKilledBy();
					
					if(bpPlayer.equals(ltkb))
					{
						bpKiller.setLastTimeKilledBy(null);
						killer.sendMessage(MessageType.PVP_PAYBACK.getTranslation().getValue());
						killer.playSound(kLoc, Sound.ANVIL_LAND, 0.5F, 0.5F);
					}
				}
				
				bpPlayer.setLastTimeKilledBy(bpKiller);
				bpPlayer.getLastTimeDamagedBy().remove(bpKiller);
				//}}revenge
			}
		}
		else
		{
			Settings pSettings = bpPlayer.getSettings();
			
			if (!pSettings.hasDeathMessages())
				player.sendMessage(MessageType.PVP_KILLINFO_DEATH.getTranslation().getValue());
			
			if(pGame != null)
				pGame.broadcastDeathMessage(playerPVPName);
		}
		
		//{{ASSISTS
		
		HashMap<BPPlayer, Long> lastTimeDamagedBy = bpPlayer.getLastTimeDamagedBy();
		long now = System.currentTimeMillis();
		
		for(Entry<BPPlayer, Long> entry : lastTimeDamagedBy.entrySet())
		{
			long when = entry.getValue();
			
			if(now - when > 5000)
				continue;
			
			BPPlayer bpOther = entry.getKey();
			Player other = bpOther.getPlayer();
			
			if(other == null)
				continue;
			
			Location oLoc = other.getEyeLocation();
			
			other.playSound(oLoc, Sound.NOTE_PLING, 0.5F, 1F);
			bpOther.getStatistics().increaseAssists();
			other.sendMessage(MessageType.PVP_KILLINFO_ASSIST.getTranslation().getValue(playerPVPName));
		}
		
		lastTimeDamagedBy.clear();
		
		//}}ASSISTS
		
		//{{PERKS
		if(bpPlayer.isPlaying())
			bpPlayer.decreasePerkLives(true);
		//}}
		
		bpPlayer.clearAfkSecondsToKick();
		event.getDrops().clear();
		event.setDroppedExp(0);
		
		if(pGame != null)
			pGame.getListener().onPlayerDeath(event, bpPlayer);
		
		if(bpPlayer.isLeaveAfterDeath())
		{
			if(pGame != null)
				pGame.onPlayerLeaveGame(bpPlayer);
			
			bpPlayer.updateArmorMinutesLeft();
			bpPlayer.setLeaveAfterDeath(false);
			bpPlayer.reset();
			
			bpPlayer.setLeaveAfterDeath(false);
		}
		
		PlayerManager.respawnWithDelay(player);
	}

	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		BPPlayer bpPlayer = BPPlayer.get(player);
		boolean leaveAfterDeath = bpPlayer.isLeaveAfterDeath();
		Game game = bpPlayer.getGame();
		Location stl = bpPlayer.getSingleTeleportLocation();
		
		//TODO REMOVE
	//	PlayerManager.updatePosDelayed(player);
		//TODO REMOVE
		
		if(game != null)
			game.getListener().onPlayerRespawn(event, bpPlayer, leaveAfterDeath);
		else
			bpPlayer.spawn();
		
		if(stl != null)
		{
			bpPlayer.teleport(stl, false);
			bpPlayer.setSingleTeleportLocation(null);
		}
	}
	
	private void hitByArrow(EntityDamageByEntityEvent event, BPPlayer bpDamager, Player damager, BPPlayer bpVictim, Player victim)
	{
		Arrow arrow = (Arrow) event.getDamager();
		GameProperties damagerProps = bpDamager.getGameProperties();
		CharacterType ct = damagerProps.getCharacterType();
		if(ct == CharacterType.ARCHER)
		{
			Location damagerLocation = damager.getLocation();
			Location victimLocation = victim.getLocation();
			if(AbilityManager.isHeadshot(damagerLocation, victimLocation, arrow))
			{
				String displayName = bpVictim.getPVPName();
				event.setDamage(event.getDamage() * 2);
				AbilityManager.playHeadshotEffect(victim);
				damager.sendMessage(MessageType.PVP_HEADSHOT.getTranslation().getValue(displayName));
			}
		}
	}

	private void hitByFireball(EntityDamageByEntityEvent event, Player damager, Player victim)
	{
		double dmg = event.getDamage() * 1.5;
		event.setDamage(dmg);
		EntityDamageEvent dmgCause = new EntityDamageByEntityEvent(damager, victim, DamageCause.ENTITY_ATTACK, dmg);
		victim.setLastDamageCause(dmgCause);
	}

	private void hitBySmallFireball(EntityDamageByEntityEvent event, Player damager, Player victim)
	{
		double dmg = event.getDamage();
		EntityDamageEvent dmgCause = new EntityDamageByEntityEvent(damager, victim, DamageCause.ENTITY_ATTACK, dmg);
		victim.setLastDamageCause(dmgCause);
	}

	private void hitByEntityExplosion(EntityDamageByEntityEvent event, BPPlayer bpDamager, Player shooter, BPPlayer bpVictim, Player victim)
	{
//		Entity exploded = event.getDamager();
//		if(exploded instanceof Projectile)
//		{
//			if(shooter != null)
//			{
//				CTFProperties damagerProps = (CTFProperties) bpDamager.getGameProperties();
//				if(damagerProps.isEnemy(bpVictim))
//				{
					EntityDamageEvent dmgCause = new EntityDamageByEntityEvent(shooter, victim, DamageCause.ENTITY_ATTACK, event.getDamage());
					victim.setLastDamageCause(dmgCause);
					return;
//				}
//			}
//			
//			event.setCancelled(true);
//		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		Entity entity = event.getEntity();
		
		if(entity instanceof ItemFrame)
			onEmptyItemFrame(event);
	}
	
	public void onEmptyItemFrame(EntityDamageByEntityEvent event)
	{
		Entity eDamager = event.getDamager();
		
		if(eDamager instanceof Player)
		{
			Player damager = (Player) eDamager;
			
			if(damager.hasPermission("Breakpoint.build") && damager.getGameMode() == GameMode.CREATIVE)
				return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent dmgEvent)
	{
		Entity eVictim = dmgEvent.getEntity();
		BPPlayer bpVictim = null;
		
		if(eVictim instanceof Player)
		{
			Player victim = (Player) eVictim;
			bpVictim = BPPlayer.get(victim);
			
			if(dmgEvent instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dmgEvent;
				Entity eDamager = event.getDamager();
				
				if(eVictim.equals(eDamager))
				{
					event.setCancelled(true);
					return;
				}
			}
			
			if(!bpVictim.isPlaying())
			{
				DamageCause cause = dmgEvent.getCause();
				
				if(cause != DamageCause.SUICIDE && cause != DamageCause.VOID)
					dmgEvent.setCancelled(true);
				
				return;
			}
			else
			{
				Game game = bpVictim.getGame();
				
				if(game.hasRoundEnded())
					dmgEvent.setCancelled(true);
			}
		}
		
		for(Game game : GameManager.getGames())
			game.getListener().onEntityDamage(dmgEvent);
		
		if(dmgEvent.isCancelled())
			return;
		
		if(eVictim instanceof Player)
		{
			Player victim = (Player) eVictim;
			Game game = bpVictim.getGame();
			
			if(dmgEvent instanceof EntityDamageByEntityEvent)
			{
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dmgEvent;
				Entity eDamager = event.getDamager();
				Projectile projectileOfDamager = null;
				
				if(eDamager instanceof Projectile)
				{
					projectileOfDamager = (Projectile) eDamager;
					eDamager = projectileOfDamager.getShooter();
					
					if(eDamager == null)
					{
						event.setCancelled(true);
						return;
					}
				}
				
				if(!(eDamager instanceof Player))
				{
					event.setCancelled(true);
					return;
				}
				
				Player damager = (Player) eDamager;
				BPPlayer bpDamager = BPPlayer.get(damager);
				
				if(!bpVictim.isInGameWith(bpDamager))
				{
					event.setCancelled(true);
					return;
				}
				
				if(bpVictim.getGameProperties().hasSpawnProtection())
				{
					damager.sendMessage(MessageType.PVP_SPAWNKILLING.getTranslation().getValue());
					event.setCancelled(true);
					return;
				}
				
				PlayerInventory inv = victim.getInventory();
				ItemStack helmet = inv.getHelmet();
				
				if(helmet != null)
				{
					Material mat = helmet.getType();
					if(mat == Material.SKULL)
						event.setDamage(event.getDamage() * (18.0 / 19.0));
				}
				
				if(projectileOfDamager != null)
				{
					Perk.onDamageDealtByProjectile(bpDamager, event);
					Perk.onDamageTakenFromProjectile(bpVictim, event);
					
					if(projectileOfDamager instanceof Arrow)
						hitByArrow(event, bpDamager, damager, bpVictim, victim);
					else if(projectileOfDamager instanceof Fireball)
					{
						DamageCause dmgCause = event.getCause();
						
						if(dmgCause == DamageCause.ENTITY_EXPLOSION)
							hitByEntityExplosion(event, bpDamager, damager, bpVictim, victim);
						else 
							hitByFireball(event, damager, victim);
					}
					else if(eDamager instanceof SmallFireball)
						hitBySmallFireball(event, damager, victim);
				}
				else
				{
					Perk.onDamageDealtByPlayer(bpDamager, event);
					Perk.onDamageTakenFromPlayer(bpVictim, event);
				}
				
				Perk.onDamageDealtByEntity(bpDamager, event);
				Perk.onDamageTakenFromEntity(bpVictim, event);
				
				if(!event.isCancelled())
					bpVictim.getLastTimeDamagedBy().put(bpDamager, System.currentTimeMillis());
			}
			else
			{
				DamageCause dmgCause = dmgEvent.getCause();
				
				if(dmgCause == DamageCause.FALL)
				{
					BPMap map = game.getCurrentMap();
					double fallDamageMultiplier = map.getFallDamageMultiplier();
					dmgEvent.setDamage(dmgEvent.getDamage() * fallDamageMultiplier);
				}
			}
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event)
	{
		EntityType et = event.getEntityType();
		
		if(et == EntityType.FIREBALL)
		{
			Projectile proj = event.getEntity();
			AbilityManager.fireballHit((Fireball) proj);
		}
		else if(et == EntityType.SMALL_FIREBALL)
		{
			Projectile proj = event.getEntity();
			AbilityManager.smallFireballHit((SmallFireball) proj);
		}
		else if(et == EntityType.ARROW)
		{
			final Arrow arrow = (Arrow) event.getEntity();
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Breakpoint.getInstance(), new Runnable() {

				@Override
				public void run()
				{
					arrow.remove();
				}
				
			}, 20L);
		}
	}

	@EventHandler
	public void playerRespawnDelayer(final PlayerRespawnEvent event)
	{
		Player player = event.getPlayer();
		event.setRespawnLocation(player.getLocation());
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run()
			{
				onPlayerRespawn(event);
			}
		}, 1);
	}
}
