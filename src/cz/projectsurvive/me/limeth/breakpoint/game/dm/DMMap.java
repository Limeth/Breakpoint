package cz.projectsurvive.me.limeth.breakpoint.game.dm;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.projectsurvive.me.limeth.breakpoint.game.BPMap;
import cz.projectsurvive.me.limeth.breakpoint.game.GameType;

public class DMMap extends BPMap
{
	//{{STATIC
	public static final DMMap load(YamlConfiguration yml, String path, String name)
	{
		String fullPath = path + "." + name;
		
		int minPlayers = yml.getInt(fullPath + ".min");
		int maxPlayers =  yml.getInt(fullPath + ".max");
		double fallDamageMultiplier = yml.getDouble(fullPath + ".fallDamageMultiplier", 1.0);
		LinkedList<Location> spawns = new LinkedList<Location>();
		List<String> rawSpawns = yml.getStringList(fullPath + ".spawns");
		
		for(String rawSpawn : rawSpawns)
		{
			String[] split = rawSpawn.split(",");
			Location loc = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), 1.0F);
			
			spawns.add(loc);
		}
		
		return new DMMap(name, minPlayers, maxPlayers, fallDamageMultiplier, spawns);
	}
	//}}STATIC
	
	private LinkedList<Location> spawns;
	
	public DMMap(String name, int minPlayers, int maxPlayers, double fallDamageMultiplier, LinkedList<Location> spawns)
	{
		super(name, GameType.DM, minPlayers, maxPlayers, fallDamageMultiplier);
		
		this.spawns = spawns;
	}
	
	public DMMap(String name, int minPlayers, int maxPlayers)
	{
		this(name, minPlayers, maxPlayers, 1.0, new LinkedList<Location>());
	}

	@Override
	public boolean isPlayable()
	{
		return spawns.size() >= 10;
	}

	@Override
	protected void saveExtra(YamlConfiguration yml, String path)
	{
		String mapPath = path + "." + getName();
		
		yml.set(mapPath + ".spawns", spawnsToStringList());
	}
	
	public LinkedList<String> spawnsToStringList()
	{
		LinkedList<String> stringList = new LinkedList<String>();
		
		for(Location loc : spawns)
		{
			String value = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw();
			
			stringList.add(value);
		}
		
		return stringList;
	}
	
	public void addSpawn(Location loc)
	{
		spawns.add(loc);
	}
	
	public void removeSpawn(Location loc)
	{
		spawns.remove(loc);
	}
	
	public LinkedList<Location> getSpawns()
	{
		return spawns;
	}

	public void setSpawns(LinkedList<Location> spawns)
	{
		this.spawns = spawns;
	}
}
