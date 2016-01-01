package cz.projectsurvive.me.limeth.breakpoint.game.dm;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.GameProperties;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class DMProperties extends GameProperties
{
	private Location spawnedAt;
	
	public DMProperties(DMGame game, BPPlayer bpPlayer)
	{
		super(game, bpPlayer);
	}

	public void chooseCharacter(CharacterType ct, boolean spawnPlayer)
	{
		setCharacterType(ct);
		BPPlayer bpPlayer = getPlayer();
		
		if(spawnPlayer)
		{
			bpPlayer.setArmorWoreSince();
			bpPlayer.spawn();
		}
	}

	@SuppressWarnings("deprecation")
	public void equip()
	{
		BPPlayer bpPlayer = getPlayer();
		Player player = bpPlayer.getPlayer();
		
		if (isPlaying())
		{
			DMGame game = getGame();
			
			bpPlayer.equipArmor();
			getCharacterType().equipPlayer(player);
			getCharacterType().applyEffects(player);
			InventoryMenuManager.showIngameMenu(bpPlayer);
			
			if (game.votingInProgress())
			{
				String playerName = player.getName();
				if (game.getMapPoll().hasVoted(playerName))
					PlayerManager.clearHotBar(player.getInventory());
				else
				{
					game.getMapPoll().showOptions(bpPlayer);
					player.updateInventory();
				}
			}
		}
		else
			bpPlayer.clearInventory();
	}

	@Override
	public boolean isPlaying()
	{
		return getCharacterType() != null;
	}

	@Override
	public boolean hasSpawnProtection()
	{
		BPPlayer bpPlayer = getPlayer();
		long spawnTime = bpPlayer.getSpawnTime();
		
		if(spawnTime >= System.currentTimeMillis() - (1000 * DMGame.spawnProtectionSeconds))
		{
			Player player = bpPlayer.getPlayer();
			Location loc = player.getLocation();
			
			if(loc.distance(spawnedAt) <= 2)
				return true;
		}
		
		return false;
	}

	@Override
	public String getChatPrefix()
	{
		return "" + ChatColor.WHITE;
	}

	@Override
	public String getTagPrefix()
	{
		return "";
	}

	public Location getSpawnedAt()
	{
		return spawnedAt;
	}

	public void setSpawnedAt(Location spawnedAt)
	{
		this.spawnedAt = spawnedAt;
	}
	
	@Override
	public DMGame getGame()
	{
		return (DMGame) super.getGame();
	}
}
