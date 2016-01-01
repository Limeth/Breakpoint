package cz.projectsurvive.me.limeth.breakpoint;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import cz.projectsurvive.me.limeth.breakpoint.managers.ShopManager;

public class RandomShop
{
	private Location location;
	private int direction;
	private final int costPerPiece;
	private final String color;
	
	public RandomShop(Location location, int direction)
	{
		setLocation(location);
		setDirection(direction);
		
		Random random = new Random(getWeekSeed());
		
		costPerPiece = getRandomCostPerPiece(random);
		color = getRandomColor(random);
	}
	
	public void build()
	{
		int[] cost = {costPerPiece, costPerPiece * 2, costPerPiece * 4};
		int[] time = {60, 120, 240};
		Calendar cal = Calendar.getInstance(Locale.GERMANY);
		String name = ChatColor.BOLD + "" + ChatColor.DARK_BLUE + cal.get(Calendar.WEEK_OF_YEAR) + "# " + cal.get(Calendar.YEAR);
		
		ShopManager.buildArmorShop(location, direction, color, cost, time, name);
	}
	
	public static int getRandomCostPerPiece(Random random)
	{
		return 7 + random.nextInt(3);
	}
	
	public static String getRandomColor(Random random)
	{
		char[] set = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < 6; i++)
			sb.append(set[random.nextInt(set.length)]);
		
		return sb.toString();
	}
	
	public long getWeekSeed()
	{
		Calendar cal = Calendar.getInstance(Locale.GERMANY);
		int week = cal.get(Calendar.WEEK_OF_YEAR);
		int year = cal.get(Calendar.YEAR);
		int weeksInYear = 64; //52, ale radši více.
		
		return year * weeksInYear + week;
	}

	public int getDirection()
	{
		return direction;
	}

	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	public Location getLocation()
	{
		return location;
	}

	public void setLocation(Location location)
	{
		this.location = location;
	}
}
