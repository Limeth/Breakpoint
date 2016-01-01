package cz.projectsurvive.me.limeth.breakpoint.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapPalette;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;

public abstract class BPMap
{
	private String name;
	private long lastTimePlayed;
	private int minPlayers, maxPlayers;
	private BufferedImage image;
	private double fallDamageMultiplier;
	private final GameType gameType;
	
	public BPMap(String name, GameType gameType, int minPlayers, int maxPlayers, double fallDamageMultiplier)
	{
		this.name = name;
		this.gameType = gameType;
		this.minPlayers = minPlayers;
		this.maxPlayers = maxPlayers;
		this.fallDamageMultiplier = fallDamageMultiplier;
		image = loadImage();
		image = MapPalette.resizeImage(image);
	}
	
	public BPMap(String name, GameType gameType, int minPlayers, int maxPlayers)
	{
		this(name, gameType, minPlayers, maxPlayers, 1.0);
	}
	
	public BPMap(String name, GameType gameType, double fallDamageMultiplier)
	{
		this(name, gameType, 0, 0, fallDamageMultiplier);
	}
	
	public BPMap(String name, GameType gameType)
	{
		this(name, gameType, 0, 0, 1.0);
	}
	
	public abstract boolean isPlayable();
	protected abstract void saveExtra(YamlConfiguration yml, String path);
	
	public final void save(YamlConfiguration yml, String path)
	{
		String mapPath = path + "." + name;
		
		yml.set(mapPath + ".min", minPlayers);
		yml.set(mapPath + ".max", maxPlayers);
		yml.set(mapPath + ".fallDamageMultiplier", fallDamageMultiplier);
		
		saveExtra(yml, path);
	}
	
	public boolean isPlayableWith(int players)
	{
		return minPlayers <= players && (maxPlayers >= players || maxPlayers <= 0);
	}
	
	public BufferedImage loadImage()
	{
		try
		{
			File file = new File("plugins/Breakpoint/images/maps/" + gameType.name() + "/" + getName() + ".png");
			BufferedImage img = ImageIO.read(file);
			return img;
		}
		catch (FileNotFoundException fnfe)
		{
			Breakpoint.warn("Error when loading an image for map '" + getName() + "' (" + gameType.name() + ").");
			Breakpoint.warn("File 'plugins/Breakpoint/images/maps/" + gameType.name() + "/" + getName() + ".png' not found.");
			return null;
		}
		catch (Throwable e)
		{
			Breakpoint.warn("Error when loading an image for map '" + getName() + "' (" + gameType.name() + ").");
			return null;
		}
	}

	public long getLastTimePlayed()
	{
		return lastTimePlayed;
	}

	public void setLastTimePlayed(long lastTimePlayed)
	{
		this.lastTimePlayed = lastTimePlayed;
	}

	public double getFallDamageMultiplier()
	{
		return fallDamageMultiplier;
	}

	public void setFallDamageMultiplier(double fallDamageMultiplier)
	{
		this.fallDamageMultiplier = fallDamageMultiplier;
	}

	public int getMaximumPlayers()
	{
		return maxPlayers;
	}

	public void setMaximumPlayers(int maxPlayers)
	{
		this.maxPlayers = maxPlayers;
	}

	public int getMinimumPlayers()
	{
		return minPlayers;
	}

	public void setMinimumPlayers(int minPlayers)
	{
		this.minPlayers = minPlayers;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public BufferedImage getImage()
	{
		return image;
	}
	
	public void setImage(BufferedImage image)
	{
		this.image = image;
	}

	public GameType getGameType()
	{
		return gameType;
	}
}
