package cz.projectsurvive.me.limeth.breakpoint.language;


public interface Translateable
{
	public String getDefaultTranslation();
	public String getYamlPath();
	public void setTranslation(Translation translation);
	public Translation getTranslation();
}
