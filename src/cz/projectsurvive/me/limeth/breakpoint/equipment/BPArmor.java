package cz.projectsurvive.me.limeth.breakpoint.equipment;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class BPArmor extends BPEquipment
{
	private int typeId;
	private int rgb;

	public BPArmor(int typeId, int rgb, String name, int minutesLeft)
	{
		super(name, minutesLeft);
		setTypeId(typeId);
		setRGB(rgb);
	}

	@Override
	public BPArmor clone()
	{
		return new BPArmor(typeId, rgb, getName(), getMinutesLeft());
	}

	@Override
	public String getEquipmentLabel()
	{
		return "armor";
	}

	@Override
	protected ItemStack getItemStackRaw()
	{
		Material mat = getMaterial(typeId);
		ItemStack is = new ItemStack(mat);
		LeatherArmorMeta im = (LeatherArmorMeta) is.getItemMeta();
		im.setDisplayName(getName());
		is.setItemMeta(im);
		colorArmor(is, rgb);
		return is;
	}

	public void colorArmor(ItemStack armor)
	{
		colorArmor(armor, rgb);
	}

	public static void colorArmor(ItemStack armor, int rgb)
	{
		Color color = Color.fromRGB(rgb);
		colorArmor(armor, color);
	}

	public static void colorArmor(ItemStack armor, Color color)
	{
		LeatherArmorMeta im = (LeatherArmorMeta) armor.getItemMeta();
		im.setColor(color);
		armor.setItemMeta(im);
	}

	@Override
	protected String serializeRaw()
	{
		return typeId + "," + rgb + "," + getName() + "," + getMinutesLeft();
	}
	
	public static Material getMaterial(int typeId)
	{
		switch (typeId)
		{
			case 3:
				return Material.LEATHER_HELMET;
			case 2:
				return Material.LEATHER_CHESTPLATE;
			case 1:
				return Material.LEATHER_LEGGINGS;
			case 0:
				return Material.LEATHER_BOOTS;
			default:
				return null;
		}
	}

	public static int getTypeId(Material mat)
	{
		switch (mat)
		{
			case LEATHER_HELMET:
				return 3;
			case LEATHER_CHESTPLATE:
				return 2;
			case LEATHER_LEGGINGS:
				return 1;
			case LEATHER_BOOTS:
				return 0;
			default:
				return -1;
		}
	}

	public static boolean isConvertable(ItemStack is)
	{
		if (is.hasItemMeta())
		{
			ItemMeta im = is.getItemMeta();
			if (im instanceof LeatherArmorMeta)
				try
				{
					Integer.parseInt(im.getLore().get(0).substring(15));
					return true;
				}
				catch (Exception e)
				{
				}
		}
		return false;
	}

	public int getTypeId()
	{
		return typeId;
	}

	public void setTypeId(int typeId)
	{
		this.typeId = typeId;
	}

	public int getRGB()
	{
		return rgb;
	}

	public void setRGB(int rgb)
	{
		this.rgb = rgb;
	}
}
