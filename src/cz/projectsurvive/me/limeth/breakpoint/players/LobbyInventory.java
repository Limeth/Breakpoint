package cz.projectsurvive.me.limeth.breakpoint.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;


public class LobbyInventory
{
	private static int SLOT_AMOUNT = 28;
	private static int MAX_SERIALIZED_SIZE = 128;
	private BPEquipment[] contents;
	
	public LobbyInventory(BPEquipment... contents)
	{
		this.contents = contents;
	}
	
	@Deprecated
	public static final LobbyInventory oldLoad(Storage storage) throws Exception
	{
		BPEquipment[] contents = new BPEquipment[28];
		
		for (int i = 0; i < 28; i++)
		{
			String[] raw = storage.get(String.class, "lobbyInventory." + i, "").split(",");
			contents[i] = BPEquipment.deserialize(raw);
		}
		
		return new LobbyInventory(contents);
	}
	
	public static final LobbyInventory load(Storage storage) throws Exception
	{
		String[] rawContents = storage.tryGetArray("lobbyInventory", String.class, SLOT_AMOUNT);
		BPEquipment[] contents = new BPEquipment[SLOT_AMOUNT];
		
		for (int i = 0; i < SLOT_AMOUNT; i++)
			if(rawContents[i] != null)
			{
				String[] raw = rawContents[i].split(",");
				contents[i] = BPEquipment.deserialize(raw);
			}
			else
				contents[i] = null;
		
		return new LobbyInventory(contents);
	}
	
	@Deprecated
	public void oldSave(Storage storage)
	{
		for (int i = 0; i < SLOT_AMOUNT; i++)
		{
			BPEquipment bpEquipment = contents[i];
			
			if (bpEquipment != null)
			{
				String value = bpEquipment.serialize();
				storage.put("lobbyInventory." + i, value);
				continue;
			}
			storage.put("lobbyInventory." + i, null);
		}
	}
	
	public void save(Storage storage)
	{
		List<String> serialized = new ArrayList<String>();
		
		for (int i = 0; i < SLOT_AMOUNT; i++)
		{
			BPEquipment bpEquipment = contents[i];
			
			if(bpEquipment != null)
			{
				String value = bpEquipment.serialize();
				serialized.add(value);
				continue;
			}
			else
				serialized.add(null);
		}
		
		storage.put("lobbyInventory", serialized);
	}
	
	@Deprecated
	public static List<Column> oldGetRequiredMySQLColumns()
	{
		Column[] array = new Column[SLOT_AMOUNT];
		
		for(int i = 0; i < array.length; i++)
			array[i] = new Column("lobbyInventory." + i, ColumnType.VARCHAR, 128);
		
		return Arrays.asList(array);
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{//																		(size + divider) * slots + bracket
		return Arrays.asList(new Column("lobbyInventory", ColumnType.VARCHAR, (MAX_SERIALIZED_SIZE + 1) * SLOT_AMOUNT + 1));
	}
	
	public boolean isEmpty()
	{
		for(BPEquipment content : contents)
			if(content != null)
				return false;
		
		return true;
	}

	public BPEquipment[] getContents()
	{
		return contents;
	}

	public void setContents(BPEquipment[] contents)
	{
		this.contents = contents;
	}
}
