package cz.projectsurvive.me.limeth.breakpoint.achievements;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementTranslation.TranslationPart;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.language.Language;
import cz.projectsurvive.me.limeth.breakpoint.language.Translateable;
import cz.projectsurvive.me.limeth.breakpoint.language.Translation;

public enum AchievementType implements Translateable
{
	//{{ENUMS
	//{{PVP
	//{{Kills
	//{{Global
	KILLS_1("First kill!", new ItemStack(Material.IRON_SWORD), "You've killed totally 1 enemy overall.", 1),
	KILLS_50("Teachable", new ItemStack(Material.IRON_SWORD), "You've killed totally 50 enemies overall.", 50),
	KILLS_250("Advanced", new ItemStack(Material.IRON_SWORD), "You've killed totally 250 enemies overall.", 250),
	KILLS_1000("Hero", new ItemStack(Material.IRON_SWORD), "You've killed totally 1000 enemies overall.", 1000),
	KILLS_5000("Veteran", new ItemStack(Material.IRON_SWORD), "You've killed totally 5000 enemies overall.", 5000),
	KILLS_25000("Master", new ItemStack(Material.IRON_SWORD), "You've killed totally 25000 enemies overall.", 25000),
	//}}
	//{{Characters
	CHARACTER_KILLS_10("Good {1}", "You've killed totally 10 enemies overall using {1}.", 10),
	CHARACTER_KILLS_25("Better {1}", "You've killed totally 25 enemies overall using {1}.", 25),
	CHARACTER_KILLS_50("Experienced {1}", "You've killed totally 50 enemies overall using {1}.", 50),
	CHARACTER_KILLS_100("Excellent {1}", "You've killed totally 100 enemies overall using {1}.", 100),
	CHARACTER_KILLS_250("Recognizable {1}", "You've killed totally 250 enemies overall using {1}.", 250),
	CHARACTER_KILLS_500("Admirable {1}", "You've killed totally 500 enemies overall using {1}.", 500),
	CHARACTER_KILLS_1000("Miraculous {1}", "You've killed totally 1000 enemies overall using {1}.", 1000),
	CHARACTER_KILLS_2500("Extreme {1}", "You've killed totally 2500 enemies overall using {1}.", 2500),
	CHARACTER_KILLS_5000("Breathtaking {1}", "You've killed totally 5000 enemies overall using {1}.", 5000),
	CHARACTER_KILLS_10000("Legendary {1}", "You've killed totally 10000 enemies overall using {1}.", 10000),
	//}}
	//}}
	//{{Others
	FIRST_BLOOD("First blood!", new ItemStack(Material.RAW_BEEF), "You were the first person, who killed a player, when the server was at least half full."),
	LAST_BLOOD("Last blood!", new ItemStack(Material.COOKED_BEEF), "You were the last person, who killed a player, when the server was at least half full."),
	//}}
	//{{CTF
	//{{TAKE
	FLAG_TAKE_1("My crystal", new ItemStack(Material.COMPASS), "You've stolen 1 enemy crystal overall.", 1),
	FLAG_TAKE_25("Getting Hang of It", new ItemStack(Material.COMPASS), "You've stolen 25 enemy crystals overall.", 25),
	FLAG_TAKE_100("Undercover", new ItemStack(Material.COMPASS), "You've stolen 100 enemy crystals overall.", 100),
	FLAG_TAKE_250("Let me carry that for you", new ItemStack(Material.COMPASS), "You've stolen 250 enemy crystals overall.", 250),
	FLAG_TAKE_500("Heavy Lifter", new ItemStack(Material.COMPASS), "You've stolen 500 enemy crystals overall.", 500),
	FLAG_TAKE_1000("Standard-Bearer", new ItemStack(Material.COMPASS), "You've stolen 1000 enemy crystals overall.", 1000),
	FLAG_TAKE_5000("Twink", new ItemStack(Material.COMPASS), "You've stolen 5000 enemy crystals overall.", 5000),
	FLAG_TAKE_10000("The Warsong Experience", new ItemStack(Material.COMPASS), "You've stolen 10000 enemy crystals overall.", 10000),
	//}}
	//{{CAPTURE
	FLAG_CAPTURE_1("Objective Complete", new ItemStack(Material.EYE_OF_ENDER), "You've captured 1 enemy crystal overall.", 1),
	FLAG_CAPTURE_25("Back and Fourth", new ItemStack(Material.EYE_OF_ENDER), "You've captured 25 enemy crystals overall.", 25),
	FLAG_CAPTURE_100("Only 9900 to go", new ItemStack(Material.EYE_OF_ENDER), "You've captured 100 enemy crystals overall.", 100),
	FLAG_CAPTURE_250("Seen that, done that", new ItemStack(Material.EYE_OF_ENDER), "You've captured 250 enemy crystals overall.", 250),
	FLAG_CAPTURE_500("Let the Crystal Drop", new ItemStack(Material.EYE_OF_ENDER), "You've captured 500 enemy crystals overall.", 500),
	FLAG_CAPTURE_1000("Seasoned Player", new ItemStack(Material.EYE_OF_ENDER), "You've captured 1000 enemy crystals overall.", 1000),
	FLAG_CAPTURE_5000("Dependable Teammate", new ItemStack(Material.EYE_OF_ENDER), "You've captured 5000 enemy crystals overall.", 5000),
	FLAG_CAPTURE_10000("All your crystals belong to us!", new ItemStack(Material.EYE_OF_ENDER), "You've captured 10000 enemy crystals overall.", 10000),
	//}}
	//}}
	//{{Combos
	DOUBLEKILL("Multikill", new ItemStack(Material.SKULL_ITEM),"You've killed 2 enemies with a short delay.", 2),
	MULTIKILL("Megakill", new ItemStack(Material.SKULL_ITEM), "You've killed 3 enemies with a short delay.", 3),
	MEGAKILL("Ultrakill", new ItemStack(Material.SKULL_ITEM), "You've killed 4 enemies with a short delay.", 4),
	ULTRAKILL("Monsterkill", new ItemStack(Material.SKULL_ITEM), "You've killed 5 enemies with a short delay.", 5),
	MONSTERKILL("Ludacriss-kill!", new ItemStack(Material.SKULL_ITEM), "You've killed 6 enemies with a short delay.", 6),
	KILLING_SPREE("Killing spree", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 3 enemies overall without dying.", 3),
	RAMPAGE("Rampage", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 6 enemies overall without dying.", 6),
	DOMINATING("Dominating", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 9 enemies overall without dying.", 9),
	UNSTOPPABLE("Unstoppable", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 12 enemies overall without dying.", 12),
	GODLIKE("Godlike", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 15 enemies overall without dying.", 15),
	MASSACRE("Massacre!", new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "You've killed 18 enemies overall without dying.", 18),
	//}}
	//}}
	//{{Shop
	BOUGHT_1("First purchase", new ItemStack(Material.EMERALD), "You've purchased 1 item in the shop.", 1),
	BOUGHT_5("Fancy colors", new ItemStack(Material.EMERALD), "You've purchased 5 items in the shop.", 5),
	BOUGHT_25("Team favor", new ItemStack(Material.EMERALD), "You've purchased 25 items in the shop.", 25),
	BOUGHT_125("Fashionable", new ItemStack(Material.EMERALD), "You've purchased 125 items in the shop.", 125),
	BOUGHT_500("Stylist", new ItemStack(Material.EMERALD), "You've purchased 500 items in the shop.", 500),
	BOUGHT_2500("Big spender", new ItemStack(Material.EMERALD), "You've purchased 2500 items in the shop.", 2500),
	//}}
	//{{Misc
	ALPHA_TESTER("Alpha tester", new ItemStack(Material.RECORD_11), "You've played Breakpoint in Alpha version."),
	BETA_TESTER("Beta tester", new ItemStack(Material.GOLD_RECORD), "You've played Breakpoint in Beta version."),
	BUG_FINDER("Bug finder", new ItemStack(Material.RAW_FISH), "You have found and reported a bug to an admin."),
	BUG_HUNTER("Bug hunter", new ItemStack(Material.COOKED_FISH), "You have found and reported more bugs to an admin.");
	//}}
	//}}
	
	private final String defaultName, defaultDescription;
	private final ItemStack icon;
	private final int requiredAmount;
	private AchievementTranslation translation;

	private AchievementType(String name, String description, int requiredAmount)
	{
		this(name, null, description, requiredAmount);
	}

	private AchievementType(String name, ItemStack icon, String description)
	{
		this(name, icon, description, -1);
	}

	private AchievementType(String name, ItemStack icon, String description, int requiredAmount)
	{
		defaultName = name;
		defaultDescription = description;
		this.icon = icon;
		this.requiredAmount = requiredAmount;
	}
	
	public String getDescription(CharacterType ct)
	{
		String ctName = ct.getProperName();
		
		return translation.getDesc(ctName);
	}
	
	public String getDescription(Language lang)
	{
		return translation.getDesc();
	}

	public String getDefaultDescription()
	{
		return defaultDescription;
	}

	public int getAmountRequired()
	{
		return requiredAmount;
	}

	public String getName(CharacterType ct)
	{
		String ctName = ct.getProperName();
		
		return translation.getName(ctName);
	}
	
	public String getName()
	{
		return translation.getName();
	}

	public String getDefaultName()
	{
		return defaultName;
	}

	public ItemStack getIcon(CharacterType ct)
	{
		String name = name();
		if (name.contains("CHARACTER_KILLS"))
			switch (ct)
			{
				case SWORDSMAN:
					return new ItemStack(Material.DIAMOND_SWORD);
				case ARCHER:
					return new ItemStack(Material.BOW);
				case KNIGHT:
					return new ItemStack(Material.IRON_HELMET);
				case CHEMIST:
					return new ItemStack(Material.GLASS_BOTTLE);
				case NINJA:
					return new ItemStack(Material.GOLD_SWORD);
				case PYRO:
					return new ItemStack(Material.STONE_AXE);
				case CULTIST:
					return new ItemStack(Material.COAL);
				case HEAVY:
					return new ItemStack(Material.DIAMOND_HELMET);
			}
		return null;
	}

	public ItemStack getIcon()
	{
		return icon.clone();
	}
	
	@Override
	public String getYamlPath()
	{
		String name = name();
		String[] parts = name.split("_");
		StringBuilder pathBuilder = new StringBuilder(toCamelCase(parts[0]));
		
		for(int i = 1; i < parts.length; i++)
			pathBuilder.append('.').append(toCamelCase(parts[i]));
		
		return "achievement." + pathBuilder.toString();
	}
	
	private static String toCamelCase(String string)
	{
		return toCamelCase(string, " ");
	}
	
	private static String toCamelCase(String string, String splitter)
	{
		String[] parts = string.split(splitter);
		StringBuilder builder = new StringBuilder();
		builder.append(parts[0].toLowerCase());
		
		for(int i = 1; i < parts.length; i++)
		{
			String part = parts[i];
			String camelCase = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
			builder.append(camelCase);
		}
		
		return builder.toString();
	}
	
	public String getDefaultTranslation(TranslationPart part)
	{
		if(part == TranslationPart.NAME)
			return getDefaultName();
		else
			return getDefaultDescription();
	}
	
	@Override
	public String getDefaultTranslation()
	{
		return null;
	}

	@Override
	public void setTranslation(Translation translation)
	{
		if(!(translation instanceof AchievementTranslation))
			throw new IllegalArgumentException("!(translation instanceof AchievementTranslation)");
		
		this.translation = (AchievementTranslation) translation;
	}

	@Override
	public AchievementTranslation getTranslation()
	{
		return translation;
	}
}
