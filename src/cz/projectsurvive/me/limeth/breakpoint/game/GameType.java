package cz.projectsurvive.me.limeth.breakpoint.game;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFGame;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFListener;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFMap;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWGame;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWListener;
import cz.projectsurvive.me.limeth.breakpoint.game.dm.DMGame;
import cz.projectsurvive.me.limeth.breakpoint.game.dm.DMListener;
import cz.projectsurvive.me.limeth.breakpoint.game.dm.DMMap;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;

public enum GameType
{
	CTF(MessageType.GAMETYPE_CTF, CTFListener.class)
	{
		@Override
		public CTFGame newGame(String name, Location signLoc)
		{
			return new CTFGame(name, signLoc);
		}
		
		@Override
		public CTFGame loadGame(YamlConfiguration yml, String name, Location signLoc)
		{
			String[] rawTeamSelLoc = yml.getString(name + ".teamSelLoc", "world,0,0,0,0,0").split(",");
			Location teamSelLoc = new Location(Bukkit.getWorld(rawTeamSelLoc[0]), Double.parseDouble(rawTeamSelLoc[1]), Double.parseDouble(rawTeamSelLoc[2]), Double.parseDouble(rawTeamSelLoc[3]), Float.parseFloat(rawTeamSelLoc[4]), Float.parseFloat(rawTeamSelLoc[5]));

			String[] rawCharSelLoc = yml.getString(name + ".charSelLoc", "world,0,0,0,0,0").split(",");
			Location charSelLoc = new Location(Bukkit.getWorld(rawCharSelLoc[0]), Double.parseDouble(rawCharSelLoc[1]), Double.parseDouble(rawCharSelLoc[2]), Double.parseDouble(rawCharSelLoc[3]), Float.parseFloat(rawCharSelLoc[4]), Float.parseFloat(rawCharSelLoc[5]));
			
			String mapsPath = name + ".maps";
			ConfigurationSection mapsSection = yml.getConfigurationSection(mapsPath);
			Set<String> mapNames = mapsSection != null ? mapsSection.getKeys(false) : new HashSet<String>();
			LinkedList<CTFMap> maps = new LinkedList<CTFMap>();
			
			for(String mapName : mapNames)
			{
				CTFMap map = CTFMap.load(yml, mapsPath, mapName);
				
				maps.add(map);
			}
			
			return new CTFGame(name, signLoc, teamSelLoc, charSelLoc, maps);
		}
	},
	
	DM(MessageType.GAMETYPE_DM, DMListener.class)
	{
		@Override
		public DMGame newGame(String name, Location signLoc)
		{
			return new DMGame(name, signLoc);
		}

		@Override
		public DMGame loadGame(YamlConfiguration yml, String name, Location signLoc)
		{
			String[] rawCharSelLoc = yml.getString(name + ".charSelLoc", "world,0,0,0,0,0").split(",");
			Location charSelLoc = new Location(Bukkit.getWorld(rawCharSelLoc[0]), Double.parseDouble(rawCharSelLoc[1]), Double.parseDouble(rawCharSelLoc[2]), Double.parseDouble(rawCharSelLoc[3]), Float.parseFloat(rawCharSelLoc[4]), Float.parseFloat(rawCharSelLoc[5]));
			
			String mapsPath = name + ".maps";
			ConfigurationSection mapsSection = yml.getConfigurationSection(mapsPath);
			Set<String> mapNames = mapsSection != null ? mapsSection.getKeys(false) : new HashSet<String>();
			LinkedList<DMMap> maps = new LinkedList<DMMap>();
			
			for(String mapName : mapNames)
			{
				DMMap map = DMMap.load(yml, mapsPath, mapName);
				
				maps.add(map);
			}
			
			return new DMGame(name, signLoc, charSelLoc, maps);
		}
	},
	
	CW(MessageType.GAMETYPE_CW, CWListener.class)
	{
		@Override
		public CWGame newGame(String name, Location signLoc)
		{
			return new CWGame(name, signLoc);
		}
		
		@Override
		public CWGame loadGame(YamlConfiguration yml, String name, Location signLoc)
		{
			String[] rawCharSelLoc = yml.getString(name + ".charSelLoc", "world,0,0,0,0,0").split(",");
			Location charSelLoc = new Location(Bukkit.getWorld(rawCharSelLoc[0]), Double.parseDouble(rawCharSelLoc[1]), Double.parseDouble(rawCharSelLoc[2]), Double.parseDouble(rawCharSelLoc[3]), Float.parseFloat(rawCharSelLoc[4]), Float.parseFloat(rawCharSelLoc[5]));
			
			String mapsPath = name + ".maps";
			ConfigurationSection mapsSection = yml.getConfigurationSection(mapsPath);
			Set<String> mapNames = mapsSection != null ? mapsSection.getKeys(false) : new HashSet<String>();
			LinkedList<CTFMap> maps = new LinkedList<CTFMap>();
			
			for(String mapName : mapNames)
			{
				CTFMap map = CTFMap.load(yml, mapsPath, mapName);
				
				maps.add(map);
			}
			
			List<String> rawDays = yml.getStringList(name + ".schedule");
			LinkedList<ClanChallenge> days = new LinkedList<ClanChallenge>();
			
			if(rawDays != null)
				for(String rawDay : rawDays)
					try
					{
						ClanChallenge day = ClanChallenge.unserialize(rawDay);
						
						days.add(day);
					}
					catch(Exception e)
					{
						Breakpoint.warn("Error when parsing a day (" + rawDay + "): " + e.getMessage());
					}
			
			return new CWGame(name, signLoc, charSelLoc, maps, days);
		}
	};
	
	private final MessageType messageType;
	private final Class<? extends GameListener> listenerClass;
	
	private GameType(MessageType messageType, Class<? extends GameListener> listenerClass)
	{
		this.messageType = messageType;
		this.listenerClass = listenerClass;
	}
	
	public abstract Game newGame(String name, Location signLoc);
	public abstract Game loadGame(YamlConfiguration yml, String name, Location signLoc);
	
	public MessageType getMessageType()
	{
		return messageType;
	}

	public Class<? extends GameListener> getListenerClass()
	{
		return listenerClass;
	}
}
