package cz.projectsurvive.me.limeth.breakpoint.game.cw;

import org.bukkit.ChatColor;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFGame;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFProperties;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.Team;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class CWProperties extends CTFProperties
{
	public CWProperties(CTFGame game, BPPlayer bpPlayer, Team team, CharacterType characterType)
	{
		super(game, bpPlayer, team, characterType);
	}
	
	public CWProperties(CTFGame game, BPPlayer bpPlayer)
	{
		super(game, bpPlayer);
	}
	
	@Override
	public String getChatPrefix()
	{
		Team team = getTeam();
		ChatColor nameColor = Team.getChatColor(team);
		
		return ChatColor.WHITE + "»" + nameColor;
	}
}
