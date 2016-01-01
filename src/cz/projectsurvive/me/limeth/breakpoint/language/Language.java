package cz.projectsurvive.me.limeth.breakpoint.language;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementTranslation;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementTranslation.TranslationPart;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;

public class Language
{
	public static String languageFileName;
	
	public static void loadLanguage(String pluginName, String languageFileName)
	{
		Language.languageFileName = languageFileName;
		File file = new File("plugins/" + pluginName + "/lang/" + languageFileName + ".yml");
		
		if(file.isDirectory())
			file.delete();
		
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		try
		{
			Language.load(file);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void load(File file) throws IOException
	{
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		
		addMissingValues(file, yml);
		
		for(MessageType type : MessageType.values())
		{
			String value = yml.getString(type.getYamlPath());
			value = ChatColor.translateAlternateColorCodes('&', value);
			Translation translation = new Translation(type, value);
			
			type.setTranslation(translation);
		}
		
		for(AchievementType at : AchievementType.values())
		{
			String path = at.getYamlPath();
			String name = yml.getString(path + TranslationPart.NAME.getSuffix());
			String desc = yml.getString(path + TranslationPart.DESCRIPTION.getSuffix());
			name = ChatColor.translateAlternateColorCodes('&', name);
			desc = ChatColor.translateAlternateColorCodes('&', desc);
			AchievementTranslation translation = new AchievementTranslation(at, name, desc);
			
			at.setTranslation(translation);
		}
	}
	
	private static void addMissingValues(File file, YamlConfiguration yml) throws IOException
	{
		List<Translateable> missing = new ArrayList<Translateable>();
		List<AchievementType> missingAC = new ArrayList<AchievementType>();
		boolean save = false;
		
		for(MessageType type : MessageType.values())
		{
			String path = type.getYamlPath();
			
			if(!yml.contains(path))
			{
				if(!save)
					save = true;
				
				missing.add(type);
			}
		}
		
		for(AchievementType ac : AchievementType.values())
		{
			String path = ac.getYamlPath();
			
			if(!yml.contains(path))
			{
				if(!save)
					save = true;
				
				missingAC.add(ac);
			}
		}
		
		if(!save)
			return;
		
		for(Translateable type : missing)
			yml.set(type.getYamlPath(), type.getDefaultTranslation());
		
		for(AchievementType at : missingAC)
			for(TranslationPart tp : TranslationPart.values())
				yml.set(at.getYamlPath() + tp.getSuffix(), at.getDefaultTranslation(tp));
		
		yml.save(file);
	}
	
/*	//{{STATIC
	public static String languageFileName;
	public static Language language;
	
	public static final void loadLanguage(String pluginName, String languageFileName)
	{
		Language.languageFileName = languageFileName;
		File file = new File("plugins/" + pluginName + "/lang/" + languageFileName + ".yml");
		
		if(file.isDirectory())
			file.delete();
		
		if(!file.exists())
		{
			file.getParentFile().mkdirs();
			
			try
			{
				file.createNewFile();
			}
			catch(IOException e)
			{
				e.printStackTrace();
				return;
			}
		}
		
		try
		{
			Language language = Language.load(file);
			Language.language = language;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	//}}
	
	private Translation[] translations;
	
	public Language(Translation... translations)
	{
		setTranslations(translations);
	}
	
	public Language(List<Translation> translations)
	{
		this(translations.toArray(new Translation[translations.size()]));
	}
	
	public static Language load(File file) throws IOException
	{
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		ArrayList<Translation> translations = new ArrayList<Translation>();
		
		addMissingValues(file, yml);
		
		for(MessageType type : MessageType.values())
		{
			String value = yml.getString(type.getYamlPath());
			value = ChatColor.translateAlternateColorCodes('&', value);
			Translation translation = new Translation(type, value);
			
			translations.add(translation);
		}
		
		for(AchievementType at : AchievementType.values())
		{
			String path = at.getYamlPath();
			String name = yml.getString(path + TranslationPart.NAME.getSuffix());
			String desc = yml.getString(path + TranslationPart.DESCRIPTION.getSuffix());
			name = ChatColor.translateAlternateColorCodes('&', name);
			desc = ChatColor.translateAlternateColorCodes('&', desc);
			AchievementTranslation translation = new AchievementTranslation(at, name, desc);
			
			translations.add(translation);
		}
		
		return new Language(translations);
	}
	
	private static final void addMissingValues(File file, YamlConfiguration yml) throws IOException
	{
		List<Translateable> missing = new ArrayList<Translateable>();
		List<AchievementType> missingAC = new ArrayList<AchievementType>();
		boolean save = false;
		
		for(MessageType type : MessageType.values())
		{
			String path = type.getYamlPath();
			
			if(!yml.contains(path))
			{
				if(!save)
					save = true;
				
				missing.add(type);
			}
		}
		
		for(AchievementType ac : AchievementType.values())
		{
			String path = ac.getYamlPath();
			
			if(!yml.contains(path))
			{
				if(!save)
					save = true;
				
				missingAC.add(ac);
			}
		}
		
		if(!save)
			return;
		
		for(Translateable type : missing)
			yml.set(type.getYamlPath(), type.getDefaultTranslation());
		
		for(AchievementType at : missingAC)
			for(TranslationPart tp : TranslationPart.values())
				yml.set(at.getYamlPath() + tp.getSuffix(), at.getDefaultTranslation(tp));
		
		yml.save(file);
	}
	
	public String translate(MessageType type, Object... values)
	{
		return getTranslation(type).getValue(values);
	}
	
	public List<String> translateToList(MessageType type, Object... values)
	{
		return getTranslation(type).getValues(values);
	}
	
	public void translateToList(MessageType type, List<String> list, Object... values)
	{
		getTranslation(type).addValuesToList(list, values);
	}
	
	public AchievementTranslation getTranslation(AchievementType at)
	{
		return (AchievementTranslation) getTranslation((Translateable) at);
	}
	
	public Translation getTranslation(Translateable key)
	{
		for(Translation message : translations)
			if(message.getKey() == key)
				return message;
		
		return null;
	}
	
	public Translation getTranslation(String value)
	{
		for(Translation message : translations)
			if(message.getValue().equals(value))
				return message;
		
		return null;
	}

	public Translation[] getTranslations()
	{
		return translations;
	}

	public void setTranslations(Translation[] translations)
	{
		this.translations = translations;
	}
	*/
}
