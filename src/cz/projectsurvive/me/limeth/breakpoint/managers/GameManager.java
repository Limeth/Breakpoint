package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.projectsurvive.me.limeth.breakpoint.game.Game;

public class GameManager
{
	private static LinkedList<Game> games;
	
	public static void loadGames()
	{
		File file = getFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		LinkedList<Game> games = new LinkedList<Game>();
		
		for (String gameName : yamlConfig.getKeys(false))
		{
			Game game = Game.loadGame(yamlConfig, gameName);
			
			games.add(game);
		}
		
		setGames(games);
	}
	
	public static void saveGames() throws IOException
	{
		File file = getFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		
		for(Game game : GameManager.getGames())
			game.save(yamlConfig);
		
		yamlConfig.save(file);
	}
	
	public static File getFile()
	{
		return new File("plugins/Breakpoint/games.yml");
	}
	
	public static void startPlayableGames()
	{
		for(Game game : games)
			if(game.isPlayable(true))
				game.start();
	}

	public static LinkedList<Game> getGames()
	{
		return games;
	}

	public static void setGames(LinkedList<Game> games)
	{
		GameManager.games = games;
	}
	
	public static void addGame(Game game)
	{
		games.add(game);
	}
	
	public static void removeGame(Game game)
	{
		games.remove(game);
	}
	
	public static Game getGame(String name)
	{
		for(Game game : games)
			if(game.getName().equals(name))
				return game;
		
		return null;
	}
	
	public static Game getGame(Location signLoc)
	{
		for(Game game : games)
			if(game.getSignLocation().equals(signLoc))
				return game;
		
		return null;
	}
}
