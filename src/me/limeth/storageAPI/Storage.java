package me.limeth.storageAPI;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;

import com.fijistudios.jordan.FruitSQL;

public class Storage extends HashMap<String, Object>
{
	private static final long serialVersionUID = 5566285288844660397L;
	public static final String UNIQUE_KEY = "entryOwner";
	private static final char CHAR_BACKSLASH = '\\', CHAR_DIVIDER = '|', CHAR_BRACKET_OPEN = '[', CHAR_BRACKET_CLOSE = ']',
			CHAR_NULL = '×';
	private static final char[] escapedChars = {CHAR_BACKSLASH, CHAR_DIVIDER, CHAR_BRACKET_OPEN, CHAR_BRACKET_CLOSE, CHAR_NULL};
	private String name;
	
	public Storage(@Nonnull String name)
	{
		Validate.notNull(name, "Name cannot be null!");
		
		this.name = name;
	}
	
	@Override
	public Object put(String key, Object value)
	{
		Validate.notNull(key, "The key cannot be null!");
		Validate.notEmpty(key, "The key cannot be empty!");
		
		if(key.equals(UNIQUE_KEY))
			throw new IllegalArgumentException("The key cannot be '" + UNIQUE_KEY + "'!");
		
		return super.put(key, value);
	}
	
	public <T, D extends T> T get(Class<T> clazz, Object key, D defaultValue) throws Exception
	{
		Object value = super.get(key);
		
		return value != null ? cast(value, clazz) : defaultValue;
	}
	
	public <T, D extends T> T get(Class<T> clazz, Object key) throws Exception
	{
		return get(clazz, key, null);
	}
	
	public void saveToYAML(File directory) throws IOException
	{
		File file = new File(directory, name + ".yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(directory);
		
		for(Entry<String, Object> entry : entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			
			yml.set(key, value);
		}
		
		yml.save(file);
	}
	
	public static Storage loadFromYAML(File directory, String name)
	{
		File file = new File(directory, name + ".yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
		Storage storage = new Storage(name);
		
		for(Entry<String, Object> entry : yml.getValues(true).entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();
			
			storage.put(key, value);
		}
		
		return storage;
	}
	
	public void saveToMySQL(FruitSQL mySQL, String table)
	{
		mySQL.insertInto(table, getKeys(), getValues());
	}
	
	public static Storage loadFromMySQL(FruitSQL mySQL, String name, String table) throws SQLException
	{
		Storage storage = new Storage(name);
		ResultSet rs = mySQL.executeQuery(getMySQLCommandSelect(table, name));
		
		if(!rs.next())
			return storage;
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int size = rsmd.getColumnCount();
		
		for(int i = 1; i <= size; i++)
		{
			String key = rsmd.getColumnName(i);
			
			if(key.equals(UNIQUE_KEY))
				continue;
			
			Object value = rs.getObject(i);
			
			if(value instanceof String)
			{
				String stringValue = (String) value;
				
				if(isSerializedMySQLList(stringValue))
				{
					ArrayList<Object> list = null;
					
					try
					{
						list = deserializeMySQLList(stringValue, Object.class);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					
					storage.put(key, list);
					continue;
				}
				else
					value = stringValue = unescapeMySQLString(stringValue);
			}
			
			storage.put(key, value);
		}
		
		FruitSQL.closeQuietly(rs);
		
		return storage;
	}
	
	public String[] getKeys()
	{
		Set<String> keySet = keySet();
		String[] keys = new String[keySet.size() + 1];
		keys[0] = UNIQUE_KEY;
		Iterator<String> iterator = keySet.iterator();
		int i = 1;
		
		while(iterator.hasNext())
			keys[i++] = iterator.next();
		
		return keys;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getValues()
	{
		Collection<Object> valueColl = values();
		Object[] values = new Object[valueColl.size() + 1];
		values[0] = name;
		Iterator<Object> iterator = valueColl.iterator();
		int i = 0;
		
		while(iterator.hasNext())
		{
			Object value = iterator.next();
			
			if(value instanceof List)
				values[++i] = serializeMySQLList((List<Object>) value);
			else if(value instanceof String)
				values[++i] = escapeMySQLString((String) value);
			else
				values[++i] = value;
		}
		
		return values;
	}
	
	public static <E> List<E> queryColumn(FruitSQL mySQL, String tableName, String columnName, Class<E> clazz)
	{
		ResultSet resultSet = mySQL.executeQuery("SELECT `" + columnName + "` FROM `" + tableName + "`;");
		List<E> list = new LinkedList<E>();
		
		try
		{
			while(resultSet.next())
			{
				Object object = resultSet.getObject(1);
				
				try
				{
					E cast = cast(object, clazz);
					
					list.add(cast);
				}
				catch(Exception e) {}
			}
			
			FruitSQL.closeQuietly(resultSet);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
		return list;
	}
	
	public static <E> List<E> queryKeyColumn(FruitSQL mySQL, String tableName, Class<E> clazz)
	{
		return queryColumn(mySQL, tableName, UNIQUE_KEY, clazz);
	}
	
	public static void createTable(FruitSQL mySQL, String tableName, String uniqueKeyType, Column... columns)
	{
		String[] args = new String[columns.length + 1];
		args[0] = "`" + UNIQUE_KEY + "` " + uniqueKeyType + " UNIQUE NOT NULL";
		
		for(int i = 1; i <= columns.length; i++)
			args[i] = columns[i - 1].toString();
		
		mySQL.createTable(tableName, args, "CHARACTER SET utf8 COLLATE utf8_bin;");
	}
	
	private static String getMySQLCommandSelect(String table, String name)
	{
		StringBuilder sb = new StringBuilder("SELECT * FROM " + table);
		
		if(name != null)
			sb.append(" WHERE BINARY ").append(UNIQUE_KEY).append(" = '").append(name).append('\'');
		
		return sb.append(';').toString();
	}
	
	public void save(StorageType type, File directory, FruitSQL mySQL, String table) throws IOException, SQLException
	{
		switch(type)
		{
			case YAML: saveToYAML(directory); return;
			case MYSQL: saveToMySQL(mySQL, table); return;
			default: throw new NotImplementedException("StorageType '" + type + "' is not yet implemented.");
		}
	}
	
	public static Storage load(StorageType type, String name, File directory, FruitSQL mySQL, String table) throws IOException, SQLException
	{
		switch(type)
		{
			case YAML: return loadFromYAML(directory, name);
			case MYSQL: return loadFromMySQL(mySQL, name, table);
			default: throw new NotImplementedException("StorageType '" + type + "' is not yet implemented.");
		}
	}
	
	public static String escapeMySQLString(String string)
	{
		StringBuilder sb = new StringBuilder(string);
		int i = 0;
		
		while(i < sb.length())
		{
			char c = sb.charAt(i);
			
			if(shouldBeEscaped(c))
			{
				sb.insert(i, CHAR_BACKSLASH);
				i++;
			}
			
			i++;
		}
		
		return escapeBackslashes(sb.toString());
	}
	
	private static String escapeBackslashes(String string)
	{
		StringBuilder sb = new StringBuilder(string);
		int i = 0;
		
		while(i < sb.length())
		{
			char c = sb.charAt(i);
			
			if(c == CHAR_BACKSLASH)
			{
				sb.insert(i, CHAR_BACKSLASH);
				i++;
			}
			
			i++;
		}
		
		return sb.toString();
	}
	
	
	public static String unescapeMySQLString(String string)
	{
		StringBuilder sb = new StringBuilder(string);
		int i = 0;
		
		while(i < sb.length())
		{
			char slash = sb.charAt(i);
			
			if(slash == CHAR_BACKSLASH && i + 1 < sb.length())
			{
				char c = sb.charAt(i + 1);
				
				if(shouldBeEscaped(c))
					sb.deleteCharAt(i);
			}
			
			i++;
		}
		
		return sb.toString();
	}
	
	public static String serializeMySQLList(List<Object> list)
	{
		Iterator<Object> iterator = list.iterator();
		StringBuilder sb = new StringBuilder();
		int i = 0;
		
		sb.append(CHAR_BRACKET_OPEN);
		
		while(iterator.hasNext())
		{
			if(i > 0)
				sb.append(CHAR_DIVIDER);
			
			Object value = iterator.next();
			String string;
			
			if(value != null)
				string = escapeMySQLString(value.toString());
			else
				string = Character.toString(CHAR_NULL);
			
			sb.append(string);
			i++;
		}
		
		return sb.append(CHAR_BRACKET_CLOSE).toString();
	}
	
	public static <T> ArrayList<T> deserializeMySQLList(String serialized, Class<T> valueClazz) throws Exception
	{
		if(!isSerializedMySQLList(serialized))
			throw new IllegalArgumentException("Entered string is not a serialized list!");
		
		ArrayList<T> list = new ArrayList<T>();
		//List<T> list = new ArrayList<T>();
		serialized = serialized.substring(1, serialized.length() - 1);
		String[] split = splitSerializedMySQLList(serialized);
		
		for(String s : split)
		{
			T parsed;
			
			if(s.length() == 1 && s.charAt(0) == CHAR_NULL)
				parsed = null;
			else
			{
				String unescaped = unescapeMySQLString(s);
				parsed = cast(unescaped, valueClazz);
			}
			
			list.add(parsed);
		}
		
		return list;
	}
	
	public <T, L extends List<T>> List<T> getList(String key, Class<T> valueClazz, L def) throws Exception
	{
		Object rawList = get(key);
		
		if(rawList == null)
			return def;
		else if(!(rawList instanceof List))
			throw new ClassCastException("The key '" + key + "' doesn't point at a list.");
		
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) rawList;
		List<T> newList = new ArrayList<T>();
		
		for(Object object : list)
		{
			T parsed = cast(object.toString(), valueClazz);
			
			newList.add(parsed);
		}
		
		return newList;
	}
	
	public <T, L extends List<T>> List<T> getList(String key, Class<T> valueClazz) throws Exception
	{
		return getList(key, valueClazz, new ArrayList<T>());
	}
	
	public <T, L extends List<T>> List<T> tryGetList(String key, Class<T> valueClazz, L def)
	{
		Object rawList = get(key);
		
		if(rawList == null)
			return def;
		else if(!(rawList instanceof List))
			throw new ClassCastException("The key '" + key + "' doesn't point at a list.");
		
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) rawList;
		List<T> newList = new ArrayList<T>();
		
		for(Object object : list)
		{
			T parsed = null;
			
			try
			{
				parsed = cast(object.toString(), valueClazz);
			}
			catch(Exception e)
			{
				continue;
			}
			
			newList.add(parsed);
		}
		
		return newList;
	}
	
	public <T, L extends List<T>> List<T> tryGetList(String key, Class<T> valueClazz)
	{
		return tryGetList(key, valueClazz, new ArrayList<T>());
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] getArray(String key, Class<T> valueClazz, T[] def, Integer size) throws Exception
	{
		Object rawList = get(key);
		
		if(rawList == null)
			return def;
		else if(!(rawList instanceof List))
			throw new ClassCastException("The key '" + key + "' doesn't point at a list.");
		
		List<Object> list = (List<Object>) rawList;
		Iterator<Object> iterator = list.iterator();
		T[] newList = (T[]) Array.newInstance(valueClazz, size != null ? size : list.size());
		int length = newList.length;
		int i = 0;
		
		while(iterator.hasNext() && i < length)
		{
			Object object = iterator.next();
			newList[i] = cast(object.toString(), valueClazz);
			i++;
		}
		
		return newList;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] getArray(String key, Class<T> valueClazz, int size) throws Exception
	{
		return getArray(key, valueClazz, (T[]) Array.newInstance(valueClazz, size), size);
	}
	
	public <T> T[] getArray(String key, Class<T> valueClazz) throws Exception
	{
		return getArray(key, valueClazz, null, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] tryGetArray(String key, Class<T> valueClazz, T[] def, Integer size)
	{
		Object rawList = get(key);
		
		if(rawList == null)
			return def;
		else if(!(rawList instanceof List))
			return def;
		
		List<Object> list = (List<Object>) rawList;
		Iterator<Object> iterator = list.iterator();
		T[] newList = (T[]) Array.newInstance(valueClazz, size != null ? size : list.size());
		int length = newList.length;
		int i = 0;
		
		while(iterator.hasNext() && i < length)
		{
			Object object = iterator.next();
			
			try
			{
				newList[i] = cast(object.toString(), valueClazz);
			}
			catch(Exception e) { continue; }
			
			i++;
		}
		
		return newList;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] tryGetArray(String key, Class<T> valueClazz, int size)
	{
		return tryGetArray(key, valueClazz, (T[]) Array.newInstance(valueClazz, size), size);
	}
	
	public <T> T[] tryGetArray(String key, Class<T> valueClazz)
	{
		return tryGetArray(key, valueClazz, null, null);
	}
	
	public static boolean isSerializedMySQLList(String string)
	{
		return string.charAt(0) == CHAR_BRACKET_OPEN && string.charAt(string.length() - 1) == CHAR_BRACKET_CLOSE && !isMySQLEscaped(string, string.length() - 1);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o, Class<T> clazz) throws Exception
	{
		if(clazz.isInstance(o))
			return (T) o;
		else
		{
			String s = o.toString();
			
			if(clazz == String.class)
				return (T) s;
			else if(clazz == Byte.class)
				return (T) (Byte) Byte.parseByte(s);
			else if(clazz == Short.class)
				return (T) (Short) Short.parseShort(s);
			else if(clazz == Integer.class)
				return (T) (Integer) Integer.parseInt(s);
			else if(clazz == Long.class)
				return (T) (Long) Long.parseLong(s);
			else if(clazz == Float.class)
				return (T) (Float) Float.parseFloat(s);
			else if(clazz == Double.class)
				return (T) (Double) Double.parseDouble(s);
			else if(clazz == Character.class)
				return (T) (Character) (s.length() > 0 ? s.charAt(0) : 0);
			else if(clazz == Boolean.class)
				return (T) (Boolean) Boolean.parseBoolean(s);
		}
		
		return (T) o;
	}
	
	public static String[] splitSerializedMySQLList(String serialized)
	{
		List<String> split = new ArrayList<String>();
		int begin = 0, i = 0;
		
		while(i < serialized.length())
		{
			char c = serialized.charAt(i);
			
			if((c == CHAR_DIVIDER && !isMySQLEscaped(serialized, i)))
			{
				String s = serialized.substring(begin, i);
				begin = i + 1;
				
				split.add(s);
			}
			else if(i + 1 >= serialized.length())
			{
				String s = serialized.substring(begin, i + 1);
				
				split.add(s);
				break;
			}
			
			i++;
		}
		
		return split.toArray(new String[split.size()]);
	}
	
	public static boolean isMySQLEscaped(String string, int index)
	{
		int amount = 0;
		
		while(index > 0)
			if(string.charAt(--index) == CHAR_BACKSLASH)
				amount++;
			else
				break;
		
		return amount % 2 == 1;
	}
	
	public static boolean shouldBeEscaped(char c)
	{
		for(char c2 : escapedChars)
			if(c == c2)
				return true;
		
		return false;
	}

	public String getName()
	{
		return name;
	}

	public void setName(@Nonnull String name)
	{
		Validate.notNull(name, "Name cannot be null!");
		
		this.name = name;
	}
}
