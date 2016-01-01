package cz.projectsurvive.me.limeth.breakpoint.game.cw;

import java.util.Calendar;
import java.util.LinkedList;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;

public class CWScheduler
{
	private final CWGame game;
	private final LinkedList<ClanChallenge> days;
	
	public CWScheduler(CWGame game, LinkedList<ClanChallenge> days)
	{
		if(game == null)
			throw new IllegalArgumentException("game == null");
		else if(days == null)
			throw new IllegalArgumentException("days == null");
		
		this.game = game;
		this.days = days;
	}
	
	public CWScheduler(CWGame game)
	{
		this(game, new LinkedList<ClanChallenge>());
	}
	
	public ClanChallenge getCurrentDay()
	{
		int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
		
		return getDay(dayOfYear);
	}
	
	public ClanChallenge getDay(int dayOfYear)
	{
		for(ClanChallenge day : days)
			if(day.getDayOfYear() == dayOfYear)
				return day;
		
		return null;
	}
	
	public void addDay(ClanChallenge day)
	{
		if(day == null)
			throw new IllegalArgumentException("day == null");
		else if(getDay(day.getDayOfYear()) != null)
			throw new IllegalArgumentException("getDay(day.getDayOfYear()) != null");
		
		days.add(day);
	}
	
	public static Integer getDayIndex(String name)
	{
		if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_SUNDAY.getTranslation().getValue()))
			return Calendar.SUNDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_MONDAY.getTranslation().getValue()))
			return Calendar.MONDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_TUESDAY.getTranslation().getValue()))
			return Calendar.TUESDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_WEDNESDAY.getTranslation().getValue()))
			return Calendar.WEDNESDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_THURSDAY.getTranslation().getValue()))
			return Calendar.THURSDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_FRIDAY.getTranslation().getValue()))
			return Calendar.FRIDAY;
		else if(name.equalsIgnoreCase(MessageType.CALENDAR_DAY_SATURDAY.getTranslation().getValue()))
			return Calendar.SATURDAY;
		else
			return null;
	}
	
	public static String getDayName(int index)
	{
		switch(index)
		{
			case Calendar.SUNDAY: return MessageType.CALENDAR_DAY_SUNDAY.getTranslation().getValue();
			case Calendar.MONDAY: return MessageType.CALENDAR_DAY_MONDAY.getTranslation().getValue();
			case Calendar.TUESDAY: return MessageType.CALENDAR_DAY_TUESDAY.getTranslation().getValue();
			case Calendar.WEDNESDAY: return MessageType.CALENDAR_DAY_WEDNESDAY.getTranslation().getValue();
			case Calendar.THURSDAY: return MessageType.CALENDAR_DAY_THURSDAY.getTranslation().getValue();
			case Calendar.FRIDAY: return MessageType.CALENDAR_DAY_FRIDAY.getTranslation().getValue();
			case Calendar.SATURDAY: return MessageType.CALENDAR_DAY_SATURDAY.getTranslation().getValue();
			default: return null;
		}
	}
	
	private static int getNextDayOfWeekIn(int dayOfWeek)
	{
		int currentDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		
		while(dayOfWeek <= currentDayOfWeek)
			dayOfWeek += 7;
		
		return dayOfWeek - currentDayOfWeek;
	}
	
	public static Integer getNextDayOfYear(int dayOfWeek)
	{
		Calendar calendar = Calendar.getInstance();
		int nextDayOfWeekIn = getNextDayOfWeekIn(dayOfWeek);
		int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		
		return currentDayOfYear + nextDayOfWeekIn;
	}
	
	public LinkedList<ClanChallenge> getDays()
	{
		return days;
	}

	public CWGame getGame()
	{
		return game;
	}
}
