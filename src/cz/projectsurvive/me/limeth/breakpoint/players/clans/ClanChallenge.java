package cz.projectsurvive.me.limeth.breakpoint.players.clans;

import java.util.Calendar;

import cz.projectsurvive.me.limeth.breakpoint.game.ctf.Team;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class ClanChallenge
{
	//{{STATIC
	public static ClanChallenge unserialize(String string)
	{
		String[] values = string.split(",");
		int dayOfYear = Integer.parseInt(values[0]);
		Clan challenging = Clan.get(values[1]);
		Clan challenged = Clan.get(values[2]);
		int maxPlayers = Integer.parseInt(values[3]);
		
		return new ClanChallenge(dayOfYear, challenging, challenged, maxPlayers);
	}
	//}}
	
	private final int dayOfYear, maxPlayers;
	private final Clan[] clans = new Clan[2];
	
	public ClanChallenge(int dayOfYear, Clan challenging, Clan challenged, int maxPlayers)
	{
		if(challenging == null)
			throw new IllegalArgumentException("challenging == null");
		else if(challenged == null)
			throw new IllegalArgumentException("challenged == null");
		
		this.dayOfYear = dayOfYear;
		this.maxPlayers = maxPlayers;
		clans[0] = challenging;
		clans[1] = challenged;
	}
	
	public boolean isWaiting()
	{
		return dayOfYear > Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
	}
	
	public String serialize()
	{
		return dayOfYear + "," + getChallengingClan().getName() + "," + getChallengedClan().getName() + "," + maxPlayers;
	}
	
	public boolean isToday()
	{
		return Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == dayOfYear;
	}
	
	public int getDayOfWeek()
	{
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
		
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	public Team getTeam(Clan clan)
	{
		for(int i = 0; i < 2; i++)
			if(clan.equals(clans[i]))
				return Team.getById(i);
		
		return null;
	}
	
	public Team getTeam(BPPlayer bpPlayer)
	{
		Clan clan = bpPlayer.getClan();
		
		if(clan == null)
			return null;
		
		return getTeam(clan);
	}
	
	public Clan getChallengingClan()
	{
		return clans[0];
	}
	
	public Clan getChallengedClan()
	{
		return clans[1];
	}

	public int getDayOfYear()
	{
		return dayOfYear;
	}
	
	public Clan[] getClans()
	{
		return clans;
	}

	public int getMaximumPlayers()
	{
		return maxPlayers;
	}
}
