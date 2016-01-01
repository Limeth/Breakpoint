package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.util.Calendar;

public class DoubleMoneyManager
{
	private static boolean doubleXP;
	
	public static void update()
	{
		updateDoubleXP();
	}
	
	private static void updateDoubleXP()
	{
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		
		doubleXP = day == Calendar.SUNDAY;
	}
	
	public static boolean isDoubleXP()
	{
		return doubleXP;
	}
}
