package cz.projectsurvive.me.limeth.breakpoint.game.ctf;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameListener;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class CTFListener extends GameListener
{
	public CTFListener(Game game)
	{
		super(game, CTFGame.class);
	}
	public CTFListener(Game game, Class<? extends Game> gameClass)
	{
		super(game, gameClass);
	}
	
	@Override
	public void onPlayerDeath(PlayerDeathEvent event, BPPlayer bpPlayer)
	{
		CTFGame game = getGame();
		FlagManager flm = game.getFlagManager();
		
		if(flm.isHoldingFlag(bpPlayer))
			flm.dropFlag(bpPlayer);
		
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		Team team = props.getTeam();
		
		if(team != null)
		{
			Player player = bpPlayer.getPlayer();
			Location loc = player.getLocation();
			Location effectLoc = loc.clone().add(0, 1, 0);
			
			team.displayDeathEffect(effectLoc);
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event, BPPlayer bpPlayer, boolean leaveAfterDeath)
	{
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		CharacterType qct = bpPlayer.getQueueCharacter();
		CTFGame game = getGame();
		
		if(qct != null)
		{
			if(qct != null)
				props.chooseCharacter(qct, false);
			bpPlayer.setQueueCharacter(null);
		}
		
		if(leaveAfterDeath)
			game.updateTeamMapViews();
		else
			game.spawn(bpPlayer);
	}

	@Override
	public void onPlayerShootBow(EntityShootBowEvent event, BPPlayer bpPlayer)
	{
	}

	@Override
	public void onPlayerSplashedByPotion(PotionSplashEvent event, BPPlayer bpShooter, BPPlayer bpTarget)
	{
		CTFProperties targetProps = (CTFProperties) bpTarget.getGameProperties();
		Player target = bpTarget.getPlayer();
		CTFGame game = getGame();
		
		if(targetProps.isEnemy(bpShooter) && !game.hasRoundEnded())
		{
			if(targetProps.hasSpawnProtection())
			{
				Player shooter = bpShooter.getPlayer();
				shooter.sendMessage(MessageType.PVP_SPAWNKILLING.getTranslation().getValue());
				event.setIntensity(target, 0);
			}
		}
		else
			event.setIntensity(target, 0);
	}

	@Override
	public void onEntityDamage(EntityDamageEvent dmgEvent)
	{
		Entity eVictim = dmgEvent.getEntity();
		
		if(eVictim instanceof Player)
		{
			Player victim = (Player) eVictim;
			BPPlayer bpVictim = BPPlayer.get(victim);
			Game vGame = bpVictim.getGame();
			CTFGame game = getGame();
			
			if(!game.equals(vGame))
				return;
			
			if(!(dmgEvent instanceof EntityDamageByEntityEvent))
				return;
			
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dmgEvent;
			Entity eDamager = event.getDamager();
			
			if(eDamager instanceof Projectile)
				eDamager = ((Projectile) eDamager).getShooter();
			
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
			
			CTFProperties victimProps = (CTFProperties) bpVictim.getGameProperties();
			
			if(!victimProps.isEnemy(bpDamager))
				event.setCancelled(true);
		}
		else if(eVictim instanceof EnderCrystal)
		{
			dmgEvent.setCancelled(true);
			
			if(dmgEvent instanceof EntityDamageByEntityEvent)
			{
				CTFGame game = getGame();
				FlagManager flm = game.getFlagManager();
				EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) dmgEvent;
				
				flm.onTryFlagTake(event);
			}
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event, BPPlayer bpPlayer)
	{
		TeleportCause cause = event.getCause();
		
		if(cause == TeleportCause.ENDER_PEARL)
		{
			CTFGame game = getGame();
			FlagManager flm = game.getFlagManager();
			
			if(flm.isHoldingFlag(bpPlayer))
			{
				Player player = bpPlayer.getPlayer();
				event.setCancelled(true);
				player.sendMessage(MessageType.OTHER_WARNPEARL.getTranslation().getValue());
			}
		}
	}

	@Override
	public void onPlayerRightClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item)
	{
		Material type = item.getType();
		
		if(type == Material.ENDER_PEARL)
		{
			CTFGame game = getGame();
			FlagManager flm = game.getFlagManager();
			
			if(flm.isHoldingFlag(bpPlayer))
			{
				Player player = bpPlayer.getPlayer();
				event.setCancelled(true);
				player.setItemInHand(PlayerManager.decreaseItem(item));
				player.sendMessage(MessageType.OTHER_WARNPEARL.getTranslation().getValue());
			}
		}
	}

	@Override
	public void onPlayerLeftClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item)
	{
	}

	@Override
	public void onPlayerRightClickBlock(PlayerInteractEvent event, BPPlayer bpPlayer)
	{
		Block block = event.getClickedBlock();
		Material mat = block.getType();
		if(mat == Material.STONE_BUTTON)
		{
			Button button = (Button) block.getState().getData();
			Block attBlock = block.getRelative(button.getAttachedFace());
			if(attBlock.getType() == Material.WOOL)
				clickedWoolButton(event, attBlock, bpPlayer);
		}
		else if(mat == Material.WALL_SIGN || mat == Material.SIGN_POST)
		{
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			
			if(ChatColor.stripColor(lines[0]).equals(MessageType.CHARACTER_SELECT.getTranslation().getValue()))
			{
				Player player = bpPlayer.getPlayer();
				CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
				Team team = props.getTeam();
				if(team != null)
				{
					CharacterType selectedCT = props.getCharacterType();
					if(selectedCT == null)
					{
						String rawCharType = ChatColor.stripColor(lines[1]);
						CharacterType charType = null;
						for(CharacterType ct : CharacterType.values())
							if(rawCharType.equalsIgnoreCase(ct.getProperName()))
							{
								charType = ct;
								break;
							}
						if(charType != null)
						{
							String name = charType.getProperName();
							
							if(charType.requiresVIP() && !player.hasPermission("Breakpoint.vip"))
							{
								player.sendMessage(ChatColor.DARK_GRAY + "---");
								player.sendMessage(MessageType.LOBBY_CHARACTER_VIPSONLY.getTranslation().getValue(name));
								player.sendMessage(ChatColor.DARK_GRAY + "---");
								return;
							}
							props.chooseCharacter(charType, true);
							player.sendMessage(MessageType.LOBBY_CHARACTER_SELECTED.getTranslation().getValue(name));
						}
						else
							player.sendMessage(MessageType.LOBBY_CHARACTER_NOTFOUND.getTranslation().getValue(rawCharType));
					}
					else
					{
						String charName = selectedCT.getProperName();
						player.sendMessage(MessageType.LOBBY_CHARACTER_ALREADYSELECTED.getTranslation().getValue(charName));
					}
				}
				else
					player.sendMessage(MessageType.LOBBY_TEAM_WARN.getTranslation().getValue());
			}
		}
	}

	@Override
	public void onPlayerPhysicallyInteractWithBlock(PlayerInteractEvent event, BPPlayer bpPlayer, Block blockBelow)
	{
		Material type = blockBelow.getType();
		@SuppressWarnings("deprecation")
		byte data = blockBelow.getData();
		
		if(type == Material.WOOL && data == 2)
		{
			CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
			props.chooseRandomTeam();
		}
	}

	public void clickedWoolButton(PlayerInteractEvent event, Block block, BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		@SuppressWarnings("deprecation")
		byte data = block.getData();
		
		if(data == (byte) 11)
		{
			if(!player.hasPermission("Breakpoint.vip"))
			{
				event.setCancelled(true);
				player.sendMessage(MessageType.LOBBY_TEAM_SELECTVIPSONLY.getTranslation().getValue());
				return;
			}
			
			CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
			Team team = props.getTeam();
			
			if(team == null)
			{
				CTFGame game = getGame();
				if(game.canJoinTeam(Team.BLUE))
					props.chooseTeam(Team.BLUE);
				else
					player.sendMessage(MessageType.LOBBY_TEAM_BALANCEJOINRED.getTranslation().getValue());
			}
		}
		else if(data == (byte) 14)
		{
			if(!player.hasPermission("Breakpoint.vip"))
			{
				event.setCancelled(true);
				player.sendMessage(MessageType.LOBBY_TEAM_SELECTVIPSONLY.getTranslation().getValue());
				return;
			}
			
			CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
			Team team = props.getTeam();
			
			if(team == null)
			{
				CTFGame game = getGame();
				
				if(game.canJoinTeam(Team.RED))
					props.chooseTeam(Team.RED);
				else
					player.sendMessage(MessageType.LOBBY_TEAM_BALANCEJOINBLUE.getTranslation().getValue());
			}
		}
	}

	@Override
	public boolean onPlayerChat(AsyncPlayerChatEvent event, BPPlayer bpPlayer)
	{
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		Team team = props.getTeam();
		if(team != null)
		{
			String message = event.getMessage();
			if(message.charAt(0) == '@')
			{
				CTFGame game = getGame();
				Player player = bpPlayer.getPlayer();
				event.setCancelled(true);
				String playerName = player.getName();
				game.sendTeamMessage(playerName, message, team);
				Breakpoint.info("Team chat: " + playerName + ": " + message);
				return false;
			}
		}
		return true;
	}
	
	@Override
	public CTFGame getGame()
	{
		return (CTFGame) super.getGame();
	}
}
