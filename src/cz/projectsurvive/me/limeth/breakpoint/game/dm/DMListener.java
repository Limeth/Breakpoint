package cz.projectsurvive.me.limeth.breakpoint.game.dm;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameListener;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class DMListener extends GameListener
{
	public DMListener(Game game)
	{
		super(game, DMGame.class);
	}

	@Override
	public boolean onPlayerChat(AsyncPlayerChatEvent event, BPPlayer bpPlayer)
	{
		return true;
	}

	@Override
	public void onPlayerDeath(PlayerDeathEvent event, BPPlayer bpPlayer)
	{
		DMGame game = getGame();
		
		game.increasePointsOfOthers(bpPlayer);
		
		Player player = bpPlayer.getPlayer();
		Location loc = player.getLocation();
		Location effectLoc = loc.clone().add(0, 1, 0);
		Player killer = player.getKiller();
		
		if(killer != null)
		{
			BPPlayer bpKiller = BPPlayer.get(killer);
			game.increasePoints(bpKiller);
		}
		
		game.updateProgressObjectiveScores();
		showBlood(effectLoc);
	}

	public static void showBlood(Location loc)
	{
		World world = loc.getWorld();
		world.playEffect(loc, Effect.STEP_SOUND, 152);
		world.playEffect(loc, Effect.STEP_SOUND, 55);
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event, BPPlayer bpPlayer, boolean leaveAfterDeath)
	{
		DMProperties props = (DMProperties) bpPlayer.getGameProperties();
		CharacterType qct = bpPlayer.getQueueCharacter();
		
		if(qct != null)
		{
			if(qct != null)
				props.chooseCharacter(qct, false);
			
			bpPlayer.setQueueCharacter(null);
		}
		
		if(!leaveAfterDeath)
		{
			DMGame game = getGame();
			
			game.spawn(bpPlayer);
		}
	}

	@Override
	public void onEntityDamage(EntityDamageEvent dmgEvent)
	{
	}

	@Override
	public void onPlayerShootBow(EntityShootBowEvent event, BPPlayer bpPlayer)
	{
	}

	@Override
	public void onPlayerSplashedByPotion(PotionSplashEvent event, BPPlayer bpShooter, BPPlayer bpVictim)
	{
		DMGame game = getGame();
		
		if(game.hasRoundEnded())
			event.setIntensity(bpVictim.getPlayer(), 0);
		
		if(bpVictim.equals(bpShooter))
			event.setIntensity(bpVictim.getPlayer(), 0);
	}

	@Override
	public void onPlayerRightClickBlock(PlayerInteractEvent event, BPPlayer bpPlayer)
	{
		Block block = event.getClickedBlock();
		Material mat = block.getType();
		if(mat == Material.WALL_SIGN || mat == Material.SIGN_POST)
		{
			Sign sign = (Sign) block.getState();
			String[] lines = sign.getLines();
			
			if(ChatColor.stripColor(lines[0]).equals(MessageType.CHARACTER_SELECT.getTranslation().getValue()))
			{
				Player player = bpPlayer.getPlayer();
				DMProperties props = (DMProperties) bpPlayer.getGameProperties();
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
		}
	}

	@Override
	public void onPlayerPhysicallyInteractWithBlock(PlayerInteractEvent event, BPPlayer bpPlayer, Block blockBelow)
	{
	}

	@Override
	public void onPlayerRightClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item)
	{
	}

	@Override
	public void onPlayerLeftClickItem(PlayerInteractEvent event, BPPlayer bpPlayer, ItemStack item)
	{
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event, BPPlayer bpPlayer)
	{
	}
	
	@Override
	public DMGame getGame()
	{
		return (DMGame) super.getGame();
	}
}
