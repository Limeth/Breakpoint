package cz.projectsurvive.me.limeth.breakpoint.game.ctf;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;

import darkBlade12.ParticleEffect;

public enum Team
{
	RED(ChatColor.RED, Color.RED, (byte) 14), BLUE(ChatColor.BLUE, Color.BLUE, (byte) 11);
	
	private final ChatColor chatColor;
	private final Color color;
	private final byte woolColor;
	
	private Team(ChatColor chatColor, Color color, byte woolColor)
	{
		this.chatColor = chatColor;
		this.color = color;
		this.woolColor = woolColor;
	}
	
	public static boolean areEnemies(Team team1, Team team2)
	{
		if(team1 == null || team2 == null)
			return false;
		
		return team1 != team2;
	}
	
	public static boolean areAllies(Team team1, Team team2)
	{
		if(team1 == null || team2 == null)
			return false;
		
		return team1 == team2;
	}
	
	public static int getId(Team team)
	{
		if(team == null)
			return -1;
		
		return team.ordinal();
	}
	
	public static Team getById(int id)
	{
		Team[] values = values();
		
		if(id >= 0 && id < values.length)
			return values[id];
		else
			return null;
	}
	
	public static Team getOpposite(Team team)
	{
		if(team == null)
			return null;
		
		switch(team)
		{
			case RED: return BLUE;
			case BLUE: return RED;
			default: return null;
		}
	}
	
	private ChatColor getRawChatColor()
	{
		return chatColor;
	}
	
	public static ChatColor getChatColor(Team team)
	{
		if(team == null)
			return ChatColor.WHITE;
		
		return team.getRawChatColor();
	}

	public Color getColor()
	{
		return color;
	}

	public byte getWoolColor()
	{
		return woolColor;
	}
	
	public void displayDeathEffect(Location loc)
	{
		ParticleEffect.displayBlockCrack(loc, Material.STAINED_GLASS.getId(), woolColor, 0.25F, 0.25F, 0.25F, 0.1F, 128);
	}
}
