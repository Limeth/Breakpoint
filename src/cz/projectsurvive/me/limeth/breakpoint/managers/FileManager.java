package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.BookMeta;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.maps.MapManager;

public class FileManager
{
	public static void loadOnlinePlayerHistory()
	{
		File file = getOnlinePlayerHistoryFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		
		if(yamlConfig.contains("playerGraphList"))
		{
			List<Integer> onlinePlayers = yamlConfig.getIntegerList("playerGraphList");
			MapManager.playerGraphRenderer.setOnlinePlayers(onlinePlayers);
		}
	}

	public static void saveOnlinePlayerHistory() throws IOException
	{
		File file = getOnlinePlayerHistoryFile();
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(file);
		
		yamlConfig.set("playerGraphList", MapManager.playerGraphRenderer.getOnlinePlayers());
		yamlConfig.save(file);
	}
	
	public static File getOnlinePlayerHistoryFile()
	{
		return new File("plugins/Breakpoint/onlinePlayerHistory.yml");
	}

	public static BookMeta loadWikiBook(BookMeta im)
	{
		String path = "plugins/Breakpoint/wikiBook/";
		List<String> contents = new ArrayList<String>();
		int page = 0;
		File file = new File(path + page + ".txt");
		while (file.exists())
		{
			String pageContent = getString(file);
			contents.add(pageContent);
			page++;
			file = new File(path + page + ".txt");
		}
		im.setPages(contents);
		
		return im;
	}

	public static void saveWikiBook()
	{
		String path = "plugins/Breakpoint/wikiBook/";
		BookMeta im = (BookMeta) InventoryMenuManager.wikiBook.getItemMeta();
		List<String> contents = im.getPages();
		for (int page = 0; page < contents.size(); page++)
		{
			File file = new File(path + page + ".txt");
			File pathFile = new File(path);
			pathFile.mkdirs();
			try
			{
				if (!file.exists())
					file.createNewFile();
				setString(file, contents.get(page));
			}
			catch (IOException e)
			{
				Breakpoint.warn("Wiki book did not save properly.");
				e.printStackTrace();
			}
		}
	}

	public static void setString(File file, String s) throws IOException
	{
		FileWriter ryt = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(ryt);
		out.write(s);
		out.close();
	}

	public static String getString(File file)
	{
		BufferedReader br;
		
		try
		{
			br = new BufferedReader(new FileReader(file));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ("Error when reading file '" + file.getPath() + "'.");
		}
		
		try
		{
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			
			while (line != null)
			{
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			
			br.close();
			
			return sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return ("Error when reading file '" + file.getPath() + "'.");
		}
	}

	public static void trySaveYaml(YamlConfiguration yaml, File file)
	{
		try
		{
			yaml.save(file);
		}
		catch (Exception e)
		{
			Breakpoint.warn("Error when saving '" + file.getAbsolutePath() + "'.");
			e.printStackTrace();
		}
	}
}
