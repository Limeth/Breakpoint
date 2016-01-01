package cz.projectsurvive.me.limeth.breakpoint.language;

import java.util.Arrays;
import java.util.List;

public class Translation
{
	private final Translateable key;
	private final String message;
	
	protected Translation(Translateable key)
	{
		this.key = key;
		message = null;
	}
	
	public Translation(Translateable key, String message)
	{
		this.key = key;
		this.message = message.replace("\\n", "\n");
	}
	
	public Translateable getKey()
	{
		return key;
	}
	
	public String getValue(Object... values)
	{
		String filled = getMessage();
		
		for(int i = 0; i < values.length; i++)
			filled = filled.replace("{" + (i + 1) + "}", values[i].toString());
		
		return filled;
	}
	
	public List<String> getValues(Object... values)
	{
		String filled = getValue(values);
		return Arrays.asList(filled.split("\n"));
	}
	
	public void addValuesToList(List<String> pre, Object... values)
	{
		pre.addAll(getValues(values));
	}

	private String getMessage()
	{
		return message;
	}
}
