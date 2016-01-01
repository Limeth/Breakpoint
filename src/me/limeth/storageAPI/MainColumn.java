package me.limeth.storageAPI;

public class MainColumn extends Column
{
	public static final String NAME = "entryOwner";
	
	public MainColumn(ColumnType type, Integer size, String[] values)
	{
		super(NAME, type, size, values);
	}
	
	@Override
	public String toString()
	{
		return super.toString() + " UNIQUE NOT NULL";
	}
}
