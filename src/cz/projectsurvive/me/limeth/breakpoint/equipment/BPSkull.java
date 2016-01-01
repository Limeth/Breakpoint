package cz.projectsurvive.me.limeth.breakpoint.equipment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;

public class BPSkull extends BPEquipment
{
	public BPSkull(String name, int minutesLeft)
	{
		super(name, minutesLeft);
	}

	@Override
	public String getEquipmentLabel()
	{
		return "skull";
	}

	@Override
	protected ItemStack getItemStackRaw()
	{
		String name = getName();
		ItemStack is = new ItemStack(Material.SKULL_ITEM);
		SkullType st = SkullType.parse(name);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		if (st == null)
		{
			is.setDurability((short) 3);
			sm.setOwner(name);
			sm.setDisplayName(MessageType.EQUIPMENT_PLAYERSKULLNAME.getTranslation().getValue(name));
		}
		else
		{
			name = st.getFormattedName();
			
			is.setDurability(st.getData());
			
			if(st.hasSkullTag())
				sm.setOwner(st.getSkullTag());
			
			sm.setDisplayName(MessageType.EQUIPMENT_SKULLNAME.getTranslation().getValue(name));
		}
		List<String> lore = new ArrayList<String>();
		lore.add(MessageType.EQUIPMENT_SKULLOWNER.getTranslation().getValue(name));
		sm.setLore(lore);
		is.setItemMeta(sm);
		return is;
	}

	public static boolean canBeRenamedTo(String name)
	{
		return SkullType.parse(name) == null;
	}

	public boolean canBeRenamed()
	{
		return SkullType.parse(getName()) == null;
	}
	
	@Override
	public BPSkull clone()
	{
		return new BPSkull(getName(), getMinutesLeft());
	}

	@Override
	protected String serializeRaw()
	{
		return getName() + "," + getMinutesLeft();
	}

	public static enum SkullType
	{
		//{{Non-VIP Skulls
		WITHER(1, 145),
		GHAST("MHF_Ghast", 140),
		LAVA_SLIME("MHF_LavaSlime", 135, "MAGMA CUBE"),
		BLAZE("MHF_Blaze", 130),
		ENDERMAN("MHF_Enderman", 125),
		MUSHROOM_COW("MHF_MushroomCow", 120, "MOOSHROOM"),
		GOLEM("MHF_Golem", 115),
		ZOMBIE_PIGMAN("MHF_PigZombie", 110, "PIGMAN"),
		SLIME("MHF_Slime", 105),
		CAVE_SPIDER("MHF_CaveSpider", 100, "CAVE SPIDER"),
		SPIDER("MHF_Spider", 95),
		CREEPER(4, 90),
		SKELETON(0, 85),
		ZOMBIE(2, 80),
		VILLAGER("MHF_Villager", 75),
		OCELOT("MHF_Ocelot", 70),
		SQUID("MHF_Squid", 65),
		SHEEP("MHF_Sheep", 60),
		COW("MHF_Cow", 55),
		PIG("MHF_Pig", 50),
		CHICKEN("MHF_Chicken", 45),
		//}}
		
		//{{VIP Skulls
		CAKE("MHF_Cake", 250, true),
		CACTUS("MHF_Cactus", 80, true),
		MELON("MHF_Melon", 100, true),
		PUMPKIN("MHF_Pumpkin", 100, true),
		OAK_LOG("MHF_OakLog", 60, "OAK LOG", true),
		TNT("MHF_TNT", 200, true),
		TNT2("MHF_TNT2", 200, true),
		CHEST("MHF_Chest", 120, true);
		//}}
		
		private final boolean vip;
		private final int data;
		private final int cost;
		private final String skullTag;
		private final String alias;
		private static final int playerCost = 150;

		private SkullType(int data, int cost, String alias, boolean vip)
		{
			this.data = data;
			this.alias = alias;
			this.cost = cost;
			skullTag = null;
			this.vip = vip;
		}
		
		private SkullType(String skullTag, int cost, String alias, boolean vip)
		{
			data = 3;
			this.alias = alias;
			this.cost = cost;
			this.skullTag = skullTag;
			this.vip = vip;
		}

		private SkullType(int data, int cost)
		{
			this(data, cost, null, false);
		}
		
		private SkullType(String skullTag, int cost)
		{
			this(skullTag, cost, null, false);
		}

		private SkullType(int data, int cost, boolean vip)
		{
			this(data, cost, null, vip);
		}
		
		private SkullType(String skullTag, int cost, boolean vip)
		{
			this(skullTag, cost, null, vip);
		}

		private SkullType(int data, int cost, String alias)
		{
			this(data, cost, alias, false);
		}
		
		private SkullType(String skullTag, int cost, String alias)
		{
			this(skullTag, cost, alias, false);
		}
		
		public static SkullType parse(String string)
		{
			for (SkullType st : values())
				if (st.name().equals(string) || (string.equals(st.getAlias())) || (st.hasSkullTag() && st.getSkullTag().equals(string)))
					return st;
			return null;
		}
		
		public String getFormattedName()
		{
			return hasAlias() ? getAlias() : name();
		}

		public static int getCost(SkullType st)
		{
			if (st == null)
				return playerCost;
			else
				return st.getCost();
		}

		public short getData()
		{
			return (short) data;
		}
		
		public boolean hasAlias()
		{
			return alias != null;
		}

		public String getAlias()
		{
			return alias;
		}
		
		private int getCost()
		{
			return cost;
		}
		
		public boolean hasSkullTag()
		{
			return skullTag != null;
		}

		public String getSkullTag()
		{
			return skullTag;
		}

		public boolean isVip()
		{
			return vip;
		}
	}
}