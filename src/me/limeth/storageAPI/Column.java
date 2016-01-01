package me.limeth.storageAPI;

import javax.annotation.Nonnull;

import org.apache.commons.lang.Validate;

public class Column
{
	private String name;
	private ColumnType type;
	private Integer size;
	private String[] values;
	
	public Column(@Nonnull String name, @Nonnull ColumnType type, Integer size, String... values)
	{
		Validate.notNull(name, "The name cannot be null!");
		Validate.notNull(type, "The type cannot be null!");
		
		if(type == ColumnType.ENUM || type == ColumnType.SET)
			if(values.length <= 0)
				throw new IllegalArgumentException("Values cannot be empty in a column of type " + type.name().toLowerCase() + "!");
		
		this.name = name;
		this.type = type;
		this.size = size;
		this.values = values;
	}
	
	public Column(String name, ColumnType type, String... values)
	{
		this(name, type, null, values);
	}
	
	@Override
	public String toString()
	{
		if(type == ColumnType.ENUM || type == ColumnType.SET)
		{
			StringBuilder sb = new StringBuilder().append('\'').append(values[0]).append('\'');
			
			for(int i = 1; i < values.length; i++)
				sb.append(", '").append(values[i]).append('\'');
			
			return '`' + name + "` " + type.name() + '(' + sb.toString() + ')';
		}
		else
			return '`' + name + "` " + type.name() + (size != null ? ("(" + size + ")") : (""));
	}

	public String getName()
	{
		return name;
	}

	public void setName(@Nonnull String name)
	{
		Validate.notNull(name, "The name cannot be null!");
		this.name = name;
	}

	public ColumnType getType()
	{
		return type;
	}

	public void setType(@Nonnull ColumnType type)
	{
		Validate.notNull(type, "The type cannot be null!");
		this.type = type;
	}

	public Integer getSize()
	{
		return size;
	}

	public void setSize(Integer size)
	{
		this.size = size;
	}

	public String[] getValues()
	{
		return values;
	}

	public void setValues(String[] values)
	{
		this.values = values;
	}
}
