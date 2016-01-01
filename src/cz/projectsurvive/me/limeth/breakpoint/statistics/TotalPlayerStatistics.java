package cz.projectsurvive.me.limeth.breakpoint.statistics;

import java.util.HashMap;

import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;

public class TotalPlayerStatistics extends PlayerStatistics
{
	private int playerAmount = 0;
	
	public TotalPlayerStatistics()
	{
		super(null, 0, 0, 0, 0, 0, 0, 0, null);
		
		HashMap<CharacterType, Integer> ctKills = new HashMap<CharacterType, Integer>();
		
		for(CharacterType ct : CharacterType.values())
			ctKills.put(ct, 0);
		
		setCharacterKills(ctKills);
	}
	
	public void add(PlayerStatistics stat)
	{
		setPlayerAmount(getPlayerAmount() + 1);
		
		this.increaseKills(stat.getKills());
		this.increaseAssists(stat.getAssists());
		this.increaseDeaths(stat.getDeaths());
		this.increaseFlagTakes(stat.getFlagTakes());
		this.increaseFlagCaptures(stat.getFlagCaptures());
		this.increaseBought(stat.getBought());
		this.increaseMoney(stat.getMoney());
		
		for(CharacterType ct : CharacterType.values())
			this.increaseKills(stat.getKills(ct), ct);
	}
	
	public double getAverageKills()
	{
		return (double) getKills() / (double) playerAmount;
	}
	
	public double getAverageAssists()
	{
		return (double) getAssists() / (double) playerAmount;
	}
	
	public double getAverageDeaths()
	{
		return (double) getDeaths() / (double) playerAmount;
	}
	
	public double getAverageFlagTakes()
	{
		return (double) getFlagTakes() / (double) playerAmount;
	}
	
	public double getAverageFlagCaptures()
	{
		return (double) getFlagCaptures() / (double) playerAmount;
	}
	
	public double getAverageBought()
	{
		return (double) getBought() / (double) playerAmount;
	}
	
	public double getAverageMoney()
	{
		return (double) getMoney() / (double) playerAmount;
	}
	
	public double getAverageKills(CharacterType ct)
	{
		return (double) getKills(ct) / (double) playerAmount;
	}

	public int getPlayerAmount()
	{
		return playerAmount;
	}

	public void setPlayerAmount(int playerAmount)
	{
		this.playerAmount = playerAmount;
	}
}
