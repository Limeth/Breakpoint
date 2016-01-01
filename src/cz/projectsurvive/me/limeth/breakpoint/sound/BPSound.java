package cz.projectsurvive.me.limeth.breakpoint.sound;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.managers.SoundManager;

public enum BPSound
{
	//COMBO
	
	MULTI_KILL(SoundManager.COMBO_PATH + ".multikill"),
	MEGA_KILL(SoundManager.COMBO_PATH + ".megakill"),
	ULTRA_KILL(SoundManager.COMBO_PATH + ".ultrakill"),
	MONSTER_KILL(SoundManager.COMBO_PATH + ".monsterkill"),
	LUDACRISS_KILL(SoundManager.COMBO_PATH + ".ludacrisskill"),
	
	KILLING_SPREE(SoundManager.COMBO_PATH + ".killingspree"),
	RAMPAGE(SoundManager.COMBO_PATH + ".rampage"),
	DOMINATING(SoundManager.COMBO_PATH + ".dominating"),
	UNSTOPPABLE(SoundManager.COMBO_PATH + ".unstoppable"),
	GODLIKE(SoundManager.COMBO_PATH + ".godlike"),
	MASSACRE(SoundManager.COMBO_PATH + ".massacre"),
	
	//MISC
	
	FIRST_BLOOD(SoundManager.MISC_PATH + ".firstblood"),
	FLAWLESS_VICTORY(SoundManager.MISC_PATH + ".flawlessvictory"),
	HUMILIATING_DEFEAT(SoundManager.MISC_PATH + ".humiliatingdefeat"),
	
	//COUNTDOWN
	
	MINUTES(SoundManager.COUNTDOWN_PATH + ".minutes", 0.8285714285714286),
	SECONDS(SoundManager.COUNTDOWN_PATH + ".seconds", 0.8732879818594105),
	REMAINING(SoundManager.COUNTDOWN_PATH + ".remaining", 1.0490702947845805),
	SIXTY(SoundManager.COUNTDOWN_PATH + ".sixty", 0.9913832199546485),
	FIFTY(SoundManager.COUNTDOWN_PATH + ".fifty", 0.9979138321995464),
	FOURTY(SoundManager.COUNTDOWN_PATH + ".fourty", 0.9027664399092971),
	THIRTY(SoundManager.COUNTDOWN_PATH + ".thirty", 0.8673015873015874),
	TWENTY(SoundManager.COUNTDOWN_PATH + ".twenty", 0.8224943310657596),
	TEN(SoundManager.COUNTDOWN_PATH + ".ten", 0.5822222222222222),
	NINE(SoundManager.COUNTDOWN_PATH + ".nine", 0.8317460317460318),
	EIGHT(SoundManager.COUNTDOWN_PATH + ".eight", 0.5796825396825397),
	SEVEN(SoundManager.COUNTDOWN_PATH + ".seven", 0.7435827664399093),
	SIX(SoundManager.COUNTDOWN_PATH + ".six", 0.8317460317460318),
	FIVE(SoundManager.COUNTDOWN_PATH + ".five", 0.8789115646258503),
	FOUR(SoundManager.COUNTDOWN_PATH + ".four", 0.7764172335600907),
	THREE(SoundManager.COUNTDOWN_PATH + ".three", 0.7763265306122449),
	TWO(SoundManager.COUNTDOWN_PATH + ".two", 0.6738321995464852),
	ONE(SoundManager.COUNTDOWN_PATH + ".one", 0.7031292517006803);
	
	private final String path;
	private final Double lengthInSeconds;
	
	private BPSound(String path)
	{
		this(path, null);
	}
	
	private BPSound(String path, Double lengthInSeconds)
	{
		this.path = path;
		this.lengthInSeconds = lengthInSeconds;
	}
	
	@SuppressWarnings("deprecation")
	public void play(Player player, Location loc, float volume, float pitch)
	{
		player.playSound(loc, path, volume, pitch);
	}
	
	public void play(Player player, float volume, float pitch)
	{
		play(player, player.getLocation(), volume, pitch);
	}
	
	public void play(Player player, float volume)
	{
		play(player, volume, 1F);
	}
	
	public void play(Player player)
	{
		play(player, 1F);
	}

	public String getPath()
	{
		return path;
	}
	
	public boolean hasLength()
	{
		return lengthInSeconds != null;
	}

	public double getLengthInSeconds()
	{
		return hasLength() ? lengthInSeconds : 0;
	}
	
	public static BPSound parse(int number)
	{
		switch(number)
		{
			default: return null;
			
			case 1: return ONE;
			case 2: return TWO;
			case 3: return THREE;
			case 4: return FOUR;
			case 5: return FIVE;
			case 6: return SIX;
			case 7: return SEVEN;
			case 8: return EIGHT;
			case 9: return NINE;
			case 10: return TEN;
			case 20: return TWENTY;
			case 30: return THIRTY;
			case 40: return FOURTY;
			case 50: return FIFTY;
			case 60: return SIXTY;
		}
	}
}
