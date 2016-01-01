package cz.projectsurvive.me.limeth.breakpoint.statistics;

import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;

public class CWMatchResult
{
	private long timestamp;
	private String opponent;
	private int[] points;
	
	public CWMatchResult(long timestamp, String opponent, int[] points)
	{
		setTimestamp(timestamp);
		setOpponent(opponent);
		setPoints(points);
	}
	
	public boolean hasWon()
	{
		return points[0] > points[1];
	}
	
	public boolean hasLost()
	{
		return points[1] > points[0];
	}
	
	public boolean wasDraw()
	{
		return points[0] == points[1];
	}
	
	public CWMatchResult(Clan opponent, int[] points)
	{
		this(System.currentTimeMillis(), opponent.getColoredName(), points);
	}
	
	public String serialize()
	{
		return timestamp + "," + opponent + "," + points[0] + "," + points[1];
	}
	
	public static CWMatchResult unserialize(String string)
	{
		String[] values = string.split(",");
		
		return new CWMatchResult(Long.parseLong(values[0]), values[1], new int[] {Integer.parseInt(values[2]), Integer.parseInt(values[3])});
	}

	public String getOpponent()
	{
		return opponent;
	}

	public final void setOpponent(String opponent)
	{
		if(opponent == null)
			throw new IllegalArgumentException("opponent == null");
		
		this.opponent = opponent;
	}

	public int[] getPoints()
	{
		return points;
	}

	public final void setPoints(int[] points)
	{
		if(points == null)
			throw new IllegalArgumentException("points == null");
		else if(points.length != 2)
			throw new IllegalArgumentException("points.length != 2");
		
		this.points = points;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}
}
