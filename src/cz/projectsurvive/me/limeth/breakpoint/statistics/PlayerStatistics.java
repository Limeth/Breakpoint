package cz.projectsurvive.me.limeth.breakpoint.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;
import me.limeth.storageAPI.StorageType;

import com.fijistudios.jordan.FruitSQL;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class PlayerStatistics extends Statistics
{
	private HashMap<CharacterType, Integer> ctKills;
	
	public PlayerStatistics(String name, int kills, int assists, int deaths, int money, int bought, int flagTakes, int flagCaptures, HashMap<CharacterType, Integer> ctKills)
	{
		super(name, kills, assists, deaths, money, bought, flagTakes, flagCaptures);
		
		this.ctKills = ctKills;
	}
	
	public static PlayerStatistics loadPlayerStatistics(String playerName)
	{
		try
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			StorageType type = config.getStorageType();
			File dir = BPPlayer.getFolder();
			String table = config.getMySQLTablePlayers();
			FruitSQL mySQL = Breakpoint.getMySQL();
			Storage storage = Storage.load(type, playerName, dir, mySQL, table);
			
			return loadPlayerStatistics(storage);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static PlayerStatistics loadPlayerStatistics(Storage storage) throws Exception
	{
		int kills = storage.get(Integer.class, "kills", 0);
		int assists = storage.get(Integer.class, "assists", 0);
		int deaths = storage.get(Integer.class, "deaths", 0);
		int money = storage.get(Integer.class, "money", 0);
		int bought = storage.get(Integer.class, "bought", 0);
		int flagTakes = storage.get(Integer.class, "flagTakes", 0);
		int flagCaptures = storage.get(Integer.class, "flagCaptures", 0);
		
		HashMap<CharacterType, Integer> ctKills = new HashMap<CharacterType, Integer>();
		
		for (CharacterType ct : CharacterType.values())
		{
			String name = ct.name();
			int curKills = storage.get(Integer.class, "ctKills." + name, 0);
			ctKills.put(ct, curKills);
		}
		
		return new PlayerStatistics(storage.getName(), kills, assists, deaths, money, bought, flagTakes, flagCaptures, ctKills);
	}
	
	public void savePlayerStatistics(Storage storage)
	{
		storage.put("kills", getKills());
		storage.put("assists", getAssists());
		storage.put("deaths", getDeaths());
		storage.put("money", getMoney());
		storage.put("bought", getBought());
		storage.put("flagTakes", getFlagTakes());
		storage.put("flagCaptures", getFlagCaptures());
		
		for (Entry<CharacterType, Integer> entry : ctKills.entrySet())
		{
			CharacterType ct = entry.getKey();
			int curKills = entry.getValue();
			String name = ct.name();
			
			storage.put("ctKills." + name, curKills);
		}
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		List<Column> list = new ArrayList<Column>();
		
		list.add(new Column("kills", ColumnType.INT));
		list.add(new Column("assists", ColumnType.INT));
		list.add(new Column("deaths", ColumnType.INT));
		list.add(new Column("money", ColumnType.INT));
		list.add(new Column("bought", ColumnType.INT));
		list.add(new Column("flagTakes", ColumnType.INT));
		list.add(new Column("flagCaptures", ColumnType.INT));
		
		for (CharacterType ct : CharacterType.values())
		{
			String name = ct.name();
			
			list.add(new Column("ctKills." + name, ColumnType.INT));
		}
		
		return list;
	}
	
	public boolean areDefault()
	{
		if(getKills() != 0 || getAssists() != 0 || getDeaths() != 0 || getMoney() != 0 || getBought() != 0 || getFlagTakes() != 0 || getFlagCaptures() != 0)
			return false;
		
		for(Integer kills : ctKills.values())
			if(kills == null)
				continue;
			else if(kills != 0)
				return false;
		
		return true;
	}
	
	public void increaseKills(int by, CharacterType ct)
	{
		ctKills.put(ct, ctKills.get(ct) + by);
	}
	
	public void increaseKills(CharacterType ct)
	{
		increaseKills(1, ct);
	}
	
	public int getKills(CharacterType ct)
	{
		return ctKills.get(ct);
	}

	public HashMap<CharacterType, Integer> getCharacterKills()
	{
		return ctKills;
	}

	public void setCharacterKills(HashMap<CharacterType, Integer> ctKills)
	{
		this.ctKills = ctKills;
	}
}
