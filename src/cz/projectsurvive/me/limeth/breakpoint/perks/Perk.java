package cz.projectsurvive.me.limeth.breakpoint.perks;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import com.google.common.base.Preconditions;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public class Perk
{	
	private PerkType type;
	private int livesLeft;
	private boolean enabled;
	
	public Perk(@Nonnull PerkType type, int livesLeft, boolean enabled)
	{
		Validate.notNull(type, "Type cannot be null!");
		
		this.type = type;
		this.livesLeft = livesLeft;
		this.enabled = enabled;
	}
	
	public static void onSpawn(BPPlayer player)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onSpawn(player);
	}
	
	public static void onDamageDealtByEntity(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageDealtByEntity(event);
	}
	
	public static void onDamageDealtByProjectile(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageDealtByProjectile(event);
	}
	
	public static void onDamageDealtByPlayer(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageDealtByPlayer(event);
	}
	
	public static void onDamageTakenFromEntity(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageTakenFromEntity(event);
	}
	
	public static void onDamageTakenFromProjectile(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageTakenFromProjectile(event);
	}
	
	public static void onDamageTakenFromPlayer(BPPlayer player, EntityDamageByEntityEvent event)
	{
		for(Perk perk : player.getPerks())
			if(perk.isEnabled())
				perk.getType().onDamageTakenFromPlayer(event);
	}
	
	public String serialize()
	{
		return type.name() + "," + livesLeft + "," + enabled;
	}
	
	public static Perk deserialize(String serialized)
	{
		String[] array = serialized.split(",");
		PerkType perk = PerkType.valueOf(array[0]);
		int livesLeft = Integer.parseInt(array[1]);
		boolean equipped = Boolean.parseBoolean(array[2]);
		
		return new Perk(perk, livesLeft, equipped);
	}
	
	public ItemStack buildItemStack()
	{
		MaterialData md = type.getMaterialData();
		@SuppressWarnings("deprecation")
		ItemStack is = new ItemStack(md.getItemType(), 1, md.getData());
		
		ItemMeta im = is.getItemMeta();
		List<String> lore = new LinkedList<String>();
		MessageType actionMT = enabled ? MessageType.MENU_PERKS_DISABLE : MessageType.MENU_PERKS_ENABLE;
		String name = actionMT.getTranslation().getValue(type.getName());
		
		lore.add(MessageType.MENU_PERKS_LIVESLEFT.getTranslation().getValue(livesLeft + ""));
		lore.addAll(type.getDescription());
		im.setLore(lore);
		im.setDisplayName(name);
		
		is.setItemMeta(im);
		
		return is;
	}
	
	public boolean hasExpired()
	{
		return livesLeft <= 0;
	}
	
	public int decreaseLivesLeft(int amount)
	{
		return livesLeft -= amount;
	}
	
	public int increaseLivesLeft(int amount)
	{
		return livesLeft += amount;
	}
	
	public int decreaseLivesLeft()
	{
		return decreaseLivesLeft(1);
	}
	
	public int increaseLivesLeft()
	{
		return increaseLivesLeft(1);
	}

	public PerkType getType()
	{
		return type;
	}

	public void setType(@Nonnull PerkType type)
	{
		Preconditions.checkNotNull(type, "Type cannot be null!");
		this.type = type;
	}

	public int getLivesLeft()
	{
		return livesLeft;
	}

	public void setLivesLeft(int livesLeft)
	{
		this.livesLeft = livesLeft;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
	
	//{{STATIC
	
	public static List<Perk> loadPlayerPerks(Storage storage) throws Exception
	{
		LinkedList<Perk> perks = new LinkedList<Perk>();
		List<String> list = storage.getList("perks", String.class, new LinkedList<String>());
		
		if(list == null)
			return perks;
		
		for(String serialized : list)
			try
			{
				Perk perk = Perk.deserialize(serialized);
				
				perks.add(perk);
			}
			catch(Exception e)
			{
				Breakpoint.warn("Error when deserializing a perk: " + e.getMessage() + " - " + serialized);
			}
		
		return perks;
	}

	public static void savePlayerPerks(Storage storage, List<Perk> perks)
	{
		LinkedList<String> list = new LinkedList<String>();
		
		for(Perk perk : perks)
		{
			if(perk == null || perk.hasExpired())
				continue;
			
			String serialized = perk.serialize();
			
			list.add(serialized);
		}
		
		storage.put("perks", list);
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		return Arrays.asList(new Column("perks", ColumnType.VARCHAR, 256));
	}
	
	//{{INVENTORY
	
	public static final String MENU_TITLE = "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "BREAKPOINT " + ChatColor.RESET + "PERKY";
	
	public static void onMenuClick(InventoryClickEvent event, BPPlayer bpPlayer)
	{
		event.setCancelled(true);
		
		Player player = bpPlayer.getPlayer();
		List<Perk> perks = bpPlayer.getPerks();
		int slotId = event.getRawSlot();
		Perk perk = getPerkAt(slotId, perks);
		
		if(perk != null)
			if(perk.isEnabled())
			{
				Inventory inv = event.getInventory();
				
				perk.setEnabled(false);
				equipMenu(bpPlayer, inv);
			}
			else
			{
				int enabled = bpPlayer.getEnabledPerks().size();
				int max = bpPlayer.getMaxEquippedPerks();
				
				if(enabled < max)
				{
					Inventory inv = event.getInventory();
					
					perk.setEnabled(true);
					equipMenu(bpPlayer, inv);
				}
				else
					player.sendMessage(MessageType.MENU_PERKS_FULL_VIP.getTranslation().getValue(max));
				/*else if(player.hasPermission("Breakpoint.vip"))
					player.sendMessage(MessageType.MENU_PERKS_FULL_VIP.getTranslation().getValue(max));
				else
					player.sendMessage(MessageType.MENU_PERKS_FULL_NONVIP.getTranslation().getValue(max));*/
			}
		
		InventoryMenuManager.updateInventoryDelayed(player);
	}
	
	public static InventoryView showPerkMenu(BPPlayer bpPlayer)
	{
		Player player = bpPlayer.getPlayer();
		List<Perk> perks = bpPlayer.getPerks();
		
		if(perks.size() <= 0)
		{
			player.sendMessage(MessageType.MENU_PERKS_EMPTY.getTranslation().getValue());
			return null;
		}
		
		int rows = bpPlayer.getPerkInventoryRows();
		Inventory inv = Bukkit.getServer().createInventory(player, 9 * rows, MENU_TITLE);
		
		equipMenu(bpPlayer, inv);
		player.closeInventory();
		
		return player.openInventory(inv);
	}

	public static void equipMenu(BPPlayer bpTarget, Inventory inv)
	{
		List<Perk> perks = bpTarget.getPerks();
		int size = inv.getSize();
		int borderStart = (int) Math.ceil(bpTarget.getDisabledPerks().size() / 9.0) * 9;
		
		inv.clear();
		
		for(int i = 0; i < 9; i++)
			inv.setItem(borderStart + i, InventoryMenuManager.getBorder());
		
		for(int i = 0; i < size; i++)
		{
			Perk perk = getPerkAt(i, perks);
			
			if(perk == null)
				continue;
			
			ItemStack is = perk.buildItemStack();
			
			inv.setItem(i, is);
		}
	}
	
	public static Perk getPerkAt(int slotId, List<Perk> perks)
	{
		if(slotId < 0)
			return null;
		
		LinkedList<Perk> enabledPerks = new LinkedList<Perk>();
		LinkedList<Perk> disabledPerks = new LinkedList<Perk>();
		
		for(Perk perk : perks)
			if(perk.isEnabled())
				enabledPerks.add(perk);
			else
				disabledPerks.add(perk);
		
		int disabled = disabledPerks.size();
		int enabled = enabledPerks.size();
		int disabledRows = (int) Math.ceil(disabled / 9.0);
		int enabledStart = 9 * (disabledRows + 1);
		
		if(slotId < disabled)
			return disabledPerks.get(slotId);
		
		if(slotId >= enabledStart && slotId < enabledStart + enabled)
			return enabledPerks.get(slotId - enabledStart);
		
		return null;
	}
	
	//}}INVENTORY
	
	//}}STATIC
}
