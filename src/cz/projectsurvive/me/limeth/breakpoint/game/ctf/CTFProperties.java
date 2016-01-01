package cz.projectsurvive.me.limeth.breakpoint.game.ctf;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.GameProperties;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.NametagEditManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.PlayerManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

@SuppressWarnings("deprecation")
public class CTFProperties extends GameProperties
{
	private Team team;
	
	public CTFProperties(CTFGame game, BPPlayer bpPlayer, Team team, CharacterType characterType)
	{
		super(game, bpPlayer, characterType);
		
		this.team = team;
	}
	
	public CTFProperties(CTFGame game, BPPlayer bpPlayer)
	{
		super(game, bpPlayer);
	}
	
	@Override
	public boolean isPlaying()
	{
		return team != null && hasCharacterType();
	}
	
	@Override
	public boolean hasSpawnProtection()
	{
		CTFGame game = getGame();
		BPPlayer bpPlayer = getPlayer();
		long spawnTime = bpPlayer.getSpawnTime();
		
		if(spawnTime >= System.currentTimeMillis() - (1000 * CTFGame.spawnProtectionSeconds))
			if(team != null)
			{
				Player player = bpPlayer.getPlayer();
				Location loc = player.getLocation();
				
				if(loc.distance(game.getSpawnLocation(team)) <= 8)
					return true;
			}
		
		return false;
	}

	@Override
	public String getChatPrefix()
	{
		Team team = getTeam();
		ChatColor nameColor = Team.getChatColor(team);
		
		return "" + nameColor;
	}

	@Override
	public String getTagPrefix()
	{
		CTFGame game = getGame();
		Team team = getTeam();
		ChatColor nameColor = Team.getChatColor(team);
		boolean holdingFlag = game.getFlagManager().isHoldingFlag(getPlayer());
		
		return "" + nameColor + (holdingFlag ? ChatColor.BOLD : "");
	}

	public void equip()
	{
		BPPlayer bpPlayer = getPlayer();
		Player player = bpPlayer.getPlayer();
		
		if (isPlaying())
		{
			CTFGame game = getGame();
			CharacterType characterType = getCharacterType();
			
			bpPlayer.equipArmor();
			
			if(!NametagEditManager.isLoaded())
				colorChestplate();
			
			characterType.equipPlayer(player);
			characterType.applyEffects(player);
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
	
	public void colorChestplate()
	{
		Color color;
		
		if(team == Team.RED)
			color = Color.RED;
		else if(team == Team.BLUE)
			color = Color.BLUE;
		else
			return;
		
		BPPlayer bpPlayer = getPlayer();
		Player player = bpPlayer.getPlayer();
		PlayerInventory inv = player.getInventory();
		ItemStack[] contents = inv.getArmorContents();
		ItemStack chestplate = contents[2];
		ItemMeta im = chestplate.getItemMeta();
		
		if(!(im instanceof LeatherArmorMeta))
			return;
		
		LeatherArmorMeta lam = (LeatherArmorMeta) im;
		
		lam.setColor(color);
		chestplate.setItemMeta(lam);
		inv.setArmorContents(contents);
	}
	
	public void chooseTeam(Team team)
	{
		BPPlayer bpPlayer = getPlayer();
		Player player = bpPlayer.getPlayer();
		GameMode gm = player.getGameMode();
		CTFGame game = getGame();
		
		if (gm != GameMode.ADVENTURE)
			player.setGameMode(GameMode.ADVENTURE);
		
		setTeam(team);
		bpPlayer.spawn();
		bpPlayer.setPlayerListName();
		NametagEditManager.updateNametag(bpPlayer);
//		plugin.sbm.addPlayerToTeam(player, team);
		game.updateTeamMapViews();
		player.getInventory().clear();
		player.updateInventory();
		
		if(team == Team.RED)
			player.sendMessage(MessageType.OTHER_TEAMJOIN_RED.getTranslation().getValue());
		else if(team == Team.BLUE)
			player.sendMessage(MessageType.OTHER_TEAMJOIN_BLUE.getTranslation().getValue());
	}
	
	public void chooseRandomTeam()
	{
		CTFGame game = getGame();
		int[] teamSizes = game.getTeamSizes();
		
		if (teamSizes[0] == teamSizes[1])
		{
			int[] scores = game.getFlagManager().getScore();
			if (scores[0] == scores[1])
				chooseTeam(Team.getById(new Random().nextInt(2)));
			else
				if (scores[0] < scores[1])
					chooseTeam(Team.RED);
				else
					chooseTeam(Team.BLUE);
		}
		else
			if (teamSizes[0] < teamSizes[1])
				chooseTeam(Team.RED);
			else
				chooseTeam(Team.BLUE);
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
	
	public boolean isEnemy(BPPlayer bpPlayer)
	{
		return Team.areEnemies(team, ((CTFProperties) bpPlayer.getGameProperties()).getTeam());
	}
	
	public boolean isAlly(BPPlayer bpPlayer)
	{
		return Team.areAllies(team, ((CTFProperties) bpPlayer.getGameProperties()).getTeam());
	}

	public Team getTeam()
	{
		return team;
	}

	public void setTeam(Team team)
	{
		this.team = team;
	}
	
	@Override
	public CTFGame getGame()
	{
		return (CTFGame) super.getGame();
	}
}
