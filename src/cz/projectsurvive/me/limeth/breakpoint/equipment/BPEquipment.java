package cz.projectsurvive.me.limeth.breakpoint.equipment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;

public abstract class BPEquipment
{
	private String name;
	private int minutesLeft;

	public BPEquipment(String name, int minutesLeft)
	{
		setName(name);
		setMinutesLeft(minutesLeft);
	}

	protected abstract ItemStack getItemStackRaw();
	protected abstract String serializeRaw();
	public abstract String getEquipmentLabel();
	
	public ItemStack getItemStack()
	{
		ItemStack is = getItemStackRaw();
		ItemMeta im = is.getItemMeta();
		List<String> lore = im.getLore();
		
		if(lore == null)
			lore = new ArrayList<String>();
		
		lore.add(MessageType.EQUIPMENT_MINUTESLEFT.getTranslation().getValue(getMinutesLeft()));
		im.setLore(lore);
		is.setItemMeta(im);
		
		return is;
	}
	
	public String serialize()
	{
		return getEquipmentLabel() + "," + serializeRaw();
	}
	
	@SuppressWarnings("deprecation")
	public static BPEquipment parse(ItemStack is)
	{
		try
		{
			ItemMeta im = is.getItemMeta();
			if (im instanceof LeatherArmorMeta)
			{
				LeatherArmorMeta lam = (LeatherArmorMeta) im;
				int type = BPArmor.getTypeId(is.getType());
				int rgb = lam.getColor().asRGB();
				String name = lam.getDisplayName();
				List<String> lore = lam.getLore();
				
				if(lore == null || lore.size() < 1)
				{
					Breakpoint.warn("Error when parsing LeatherArmor: " + (lam.hasDisplayName() ? lam.getDisplayName() : Material.getMaterial(type)));
					return null;
				}
				
				int minutesLeft = Integer.parseInt(lore.get(0).substring(MessageType.EQUIPMENT_MINUTESLEFT.getTranslation().getValue().length() - 3));
				return new BPArmor(type, rgb, name, minutesLeft);
			}
			else if (im instanceof SkullMeta)
			{
				SkullMeta sm = (SkullMeta) im;
				List<String> lore = sm.getLore();
				
				if(lore == null || lore.size() < 2)
				{
					Breakpoint.warn("Error when parsing Skull: " + (sm.hasDisplayName() ? sm.getDisplayName() : ""));
					return null;
				}
				
				int minutesLeft = Integer.parseInt(lore.get(1).substring(MessageType.EQUIPMENT_MINUTESLEFT.getTranslation().getValue().length() - 3));
				String name = lore.get(0).substring(MessageType.EQUIPMENT_SKULLOWNER.getTranslation().getValue().length() - 3);
				return new BPSkull(name, minutesLeft);
			}
			else if(im != null)
				try
				{
					List<String> lore = im.getLore();
					
					if(lore == null || lore.size() < 1)
					{
						Breakpoint.warn("Error when parsing Equipment: " + (im.hasDisplayName() ? im.getDisplayName() : ""));
						return null;
					}
					
					int minutesLeft = Integer.parseInt(lore.get(0).substring(MessageType.EQUIPMENT_MINUTESLEFT.getTranslation().getValue().length() - 3));
					return new BPBlock(im.getDisplayName(), minutesLeft, is.getTypeId(), (byte) is.getDurability());
				}
				catch(Exception e)
				{
					Breakpoint.warn("Error when parsing equipment: " + (im.hasDisplayName() ? im.getDisplayName() : ""));
					return null;
				}
			else
				return null;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static BPEquipment deserialize(String[] raw)
	{
		try
		{
			if (raw[0].equals("armor"))
			{
				int type = Integer.parseInt(raw[1]);
				int rgb = Integer.parseInt(raw[2]);
				String name = raw[3];
				int minutesLeft = Integer.parseInt(raw[4]);
				return new BPArmor(type, rgb, name, minutesLeft);
			}
			else if (raw[0].equals("skull"))
			{
				String name = raw[1];
				int minutesLeft = Integer.parseInt(raw[2]);
				return new BPSkull(name, minutesLeft);
			}
			else if (raw[0].equals("block"))
			{
				String name = raw[1];
				int minutesLeft = Integer.parseInt(raw[2]);
				int id = Integer.parseInt(raw[3]);
				byte data = Byte.parseByte(raw[4]);
				return new BPBlock(name, minutesLeft, id, data);
			}
			else
				return null;
		}
		catch (Exception e)
		{
			Breakpoint.warn("Error when parsing BPArmor from '" + raw.toString() + "'!");
			return null;
		}
	}
	
	@Override
	public BPEquipment clone()
	{
		return null;
	}

	@Override
	public String toString()
	{
		return serialize();
	}
	
	public boolean hasExpired()
	{
		return getMinutesLeft() <= 0;
	}

	public void decreaseMinutesLeft(int by)
	{
		setMinutesLeft(getMinutesLeft() - by);
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public int getMinutesLeft()
	{
		return minutesLeft;
	}

	public void setMinutesLeft(int minutesLeft)
	{
		this.minutesLeft = minutesLeft;
	}
}
