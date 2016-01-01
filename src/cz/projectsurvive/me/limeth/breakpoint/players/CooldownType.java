package cz.projectsurvive.me.limeth.breakpoint.players;

public enum CooldownType
{
	HEAL("Breakpoint.heal"), POTION_RAW("Breakpoint.chemik.potion.", true), BOW_PYRO("Breakpoint.pyroman.bow"), BOW_SNIPER("Breakpoint.odstrelovac.bow"), BLAZE_ROD_MAGE("Breakpoint.cernokneznik.blaze_rod"), STICK_MAGE("Breakpoint.cernokneznik.stick"), FEATHER_MAGE("Breakpoint.cernokneznik.feather");
	private final String path;
	private final boolean raw;

	private CooldownType(String path)
	{
		this(path, false);
	}

	private CooldownType(String path, boolean raw)
	{
		this.path = path;
		this.raw = raw;
	}

	public String getPath()
	{
		return path;
	}

	public boolean isRaw()
	{
		return raw;
	}
}
