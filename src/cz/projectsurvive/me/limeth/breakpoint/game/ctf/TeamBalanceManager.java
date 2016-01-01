package cz.projectsurvive.me.limeth.breakpoint.game.ctf;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.NametagEditManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class TeamBalanceManager
{
	private final CTFGame game;
	private int loopId;

	public TeamBalanceManager(CTFGame game)
	{
		this.game = game;
	}
	
	public void checkTeams()
	{
		Random random = new Random();
		List<BPPlayer> red = game.getPlayersInTeam(Team.RED);
		List<BPPlayer> blue = game.getPlayersInTeam(Team.BLUE);
		while (red.size() > blue.size() + 1)
		{
			movePlayerToTeam(red.get(random.nextInt(red.size())), Team.BLUE);
			red = game.getPlayersInTeam(Team.RED);
			blue = game.getPlayersInTeam(Team.BLUE);
		}
		while (blue.size() > red.size() + 1)
		{
			movePlayerToTeam(blue.get(random.nextInt(blue.size())), Team.RED);
			red = game.getPlayersInTeam(Team.RED);
			blue = game.getPlayersInTeam(Team.BLUE);
		}
	}

	@SuppressWarnings("deprecation")
	public void movePlayerToTeam(BPPlayer bpPlayer, Team team)
	{
		Player player = bpPlayer.getPlayer();
		CTFProperties props = (CTFProperties) bpPlayer.getGameProperties();
		FlagManager flm = game.getFlagManager();
		
		props.setTeam(team);
		bpPlayer.spawn();
		
		if (flm.isHoldingFlag(bpPlayer))
			flm.dropFlag(bpPlayer);
		
		bpPlayer.setPlayerListName();
		NametagEditManager.updateNametag(bpPlayer);
		
		player.updateInventory();
		player.sendMessage(ChatColor.DARK_RED + "--- --- --- --- ---");
		
		if(team == Team.RED)
			player.sendMessage(MessageType.BALANCE_MOVERED.getTranslation().getValue());
		else if(team == Team.BLUE)
			player.sendMessage(MessageType.BALANCE_MOVEBLUE.getTranslation().getValue());
		
		player.sendMessage(ChatColor.DARK_RED + "--- --- --- --- ---");
	}

	public void startLoop()
	{
		loopId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Breakpoint.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				checkTeams();
			}
		}, 20L * 60, 20L * 60);
	}

	public CTFGame getGame()
	{
		return game;
	}

	public int getLoopId()
	{
		return loopId;
	}
}
