package cz.projectsurvive.me.limeth.breakpoint.players;

import java.util.Arrays;
import java.util.List;

import me.limeth.storageAPI.Column;
import me.limeth.storageAPI.ColumnType;
import me.limeth.storageAPI.Storage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;

public class Settings
{
	private boolean deathMessages;
	private boolean extraSounds;
	private boolean showEnchantments;
	
	public Settings()
	{
		deathMessages = true;
		extraSounds = true;
		showEnchantments = true;
	}
	
	public Settings(boolean deathMessages, boolean extraSounds, boolean showEnchantments)
	{
		this.deathMessages = deathMessages;
		this.extraSounds = extraSounds;
		this.showEnchantments = showEnchantments;
	}
	
	public static final Settings load(Storage storage) throws Exception
	{
		boolean deathMessages = storage.get(Boolean.class, "deathMessages", true);
		boolean extraSounds = storage.get(Boolean.class, "extraSounds", true);
		boolean showEnchantments = storage.get(Boolean.class, "showEnchantments", true);
		
		return new Settings(deathMessages, extraSounds, showEnchantments);
	}
	
	public void save(Storage storage)
	{
		storage.put("deathMessages", deathMessages);
		storage.put("extraSounds", extraSounds);
		storage.put("showEnchantments", showEnchantments);
	}
	
	public static List<Column> getRequiredMySQLColumns()
	{
		return Arrays.asList(
				new Column("deathMessages", ColumnType.BOOLEAN),
				new Column("extraSounds", ColumnType.BOOLEAN),
				new Column("showEnchantments", ColumnType.BOOLEAN)
				);
	}
	
	public boolean areDefault()
	{
		return deathMessages == true && extraSounds == true && showEnchantments == true;
	}
	
	public boolean toggleExtraSounds()
	{
		extraSounds = !extraSounds;
		return extraSounds;
	}

	public boolean hasExtraSounds()
	{
		return extraSounds;
	}

	public void setExtraSounds(boolean extraSounds)
	{
		this.extraSounds = extraSounds;
	}
	
	public boolean toggleDeathMessages()
	{
		deathMessages = !deathMessages;
		return deathMessages;
	}
	
	public boolean hasDeathMessages()
	{
		return deathMessages;
	}

	public void setDeathMessages(boolean deathMessages)
	{
		this.deathMessages = deathMessages;
	}

	public boolean hasShowEnchantments()
	{
		return showEnchantments;
	}

	public void setShowEnchantments(boolean showEnchantments)
	{
		this.showEnchantments = showEnchantments;
	}
	
	// INVENTORY
	
	public static final String MENU_TITLE = "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "BREAKPOINT " + ChatColor.RESET + "NASTAVENI";
	private static final SettingsButton[] SETTINGS_BUTTONS =
		{
			new SettingsButton(
				MessageType.MENU_SETTINGS_DEATHMESSAGES_DESC,
				MessageType.MENU_SETTINGS_DEATHMESSAGES_TURNON,
				MessageType.MENU_SETTINGS_DEATHMESSAGES_TURNOFF,
				MessageType.MENU_SETTINGS_DEATHMESSAGES_ENABLE,
				MessageType.MENU_SETTINGS_DEATHMESSAGES_DISABLE,
				3
			) {
				@Override
				public boolean isEnabled(Settings settings)
				{
					return settings.deathMessages;
				}

				@Override
				public void setEnabled(Settings settings, boolean enabled)
				{
					settings.deathMessages = enabled;
				}
			},
			new SettingsButton(
				MessageType.MENU_SETTINGS_EXTRASOUNDS_DESC,
				MessageType.MENU_SETTINGS_EXTRASOUNDS_TURNON,
				MessageType.MENU_SETTINGS_EXTRASOUNDS_TURNOFF,
				MessageType.MENU_SETTINGS_EXTRASOUNDS_ENABLE,
				MessageType.MENU_SETTINGS_EXTRASOUNDS_DISABLE,
				4
			) {
				@Override
				public boolean isEnabled(Settings settings)
				{
					return settings.extraSounds;
				}

				@Override
				public void setEnabled(Settings settings, boolean enabled)
				{
					settings.extraSounds = enabled;
				}
			},
			new SettingsButton(
				MessageType.MENU_SETTINGS_SHOWENCHANTMENTS_DESC,
				MessageType.MENU_SETTINGS_SHOWENCHANTMENTS_TURNON,
				MessageType.MENU_SETTINGS_SHOWENCHANTMENTS_TURNOFF,
				MessageType.MENU_SETTINGS_SHOWENCHANTMENTS_ENABLE,
				MessageType.MENU_SETTINGS_SHOWENCHANTMENTS_DISABLE,
				5
			) {
				@Override
				public boolean isEnabled(Settings settings)
				{
					return settings.showEnchantments;
				}

				@Override
				public void setEnabled(Settings settings, boolean enabled)
				{
					settings.showEnchantments = enabled;
				}
			},
		};
	
	public static InventoryView showSettingsMenu(BPPlayer bpPlayer)
	{
		Settings settings = bpPlayer.getSettings();
		Player player = bpPlayer.getPlayer();
		Inventory inv = Bukkit.getServer().createInventory(player, 9, MENU_TITLE);
		
		equipMenu(inv, settings);
		player.closeInventory();
		
		return player.openInventory(inv);
	}

	public static void equipMenu(Inventory inv, Settings settings)
	{
		for(SettingsButton button : SETTINGS_BUTTONS)
			inv.setItem(button.slotId, button.getButton(settings));
	}
	
	/*
		else if(mat == Material.DIODE_BLOCK_OFF || mat == Material.DIODE_BLOCK_ON)
		{
			boolean state = bpPlayer.getSettings().toggleDeathMessages();
			if(state)
				player.sendMessage(MessageType.MENU_SETTINGS_DEATHMESSAGES_ENABLE.getTranslation().getValue());
			else
				player.sendMessage(MessageType.MENU_SETTINGS_DEATHMESSAGES_DISABLE.getTranslation().getValue());
			InventoryMenuManager.showDeathMessageDiode(bpPlayer);
		}
		else if(mat == Material.REDSTONE_COMPARATOR_OFF || mat == Material.REDSTONE_COMPARATOR_ON)
		{
			boolean state = bpPlayer.getSettings().toggleExtraSounds();
			if(state)
				player.sendMessage(MessageType.MENU_SETTINGS_EXTRASOUNDS_ENABLE.getTranslation().getValue());
			else
				player.sendMessage(MessageType.MENU_SETTINGS_EXTRASOUNDS_DISABLE.getTranslation().getValue());
			InventoryMenuManager.showSoundDiode(bpPlayer);
		}
		
		*/

	public static void onMenuClick(InventoryClickEvent event, BPPlayer bpPlayer)
	{
		event.setCancelled(true);
		int slotId = event.getRawSlot();
		
		for(SettingsButton button : SETTINGS_BUTTONS)
			if(button.getSlotId() == slotId)
			{
				Player player = bpPlayer.getPlayer();
				Settings settings = bpPlayer.getSettings();
				Inventory inv = event.getInventory();
				boolean newState = !button.isEnabled(settings);
				
				if(newState)
					player.sendMessage(button.getEnableMessage().getTranslation().getValue());
				else
					player.sendMessage(button.getDisableMessage().getTranslation().getValue());
				
				button.setEnabled(settings, newState);
				inv.setItem(slotId, button.getButton(newState));
				InventoryMenuManager.updateInventoryDelayed(player);
				break;
			}
	}
	
	public abstract static class SettingsButton
	{
		private MessageType desc, turnOn, turnOff, enableMessage, disableMessage;
		private int slotId;
		
		public SettingsButton(MessageType desc, MessageType turnOn, MessageType turnOff, MessageType enableMessage, MessageType disableMessage, int slotId)
		{
			this.desc = desc;
			this.turnOn = turnOn;
			this.turnOff = turnOff;
			this.enableMessage = enableMessage;
			this.disableMessage = disableMessage;
			setSlotId(slotId);
		}
		
		public abstract boolean isEnabled(Settings settings);
		public abstract void setEnabled(Settings settings, boolean enabled);
		
		public ItemStack getButton(boolean enabled)
		{
			return InventoryMenuManager.getToggleableButton(turnOn, turnOff, desc, enabled);
		}
		
		public ItemStack getButton(Settings settings)
		{
			return getButton(isEnabled(settings));
		}

		public MessageType getDesc()
		{
			return desc;
		}

		public void setDesc(MessageType desc)
		{
			this.desc = desc;
		}

		public MessageType getTurnOn()
		{
			return turnOn;
		}

		public void setTurnOn(MessageType turnOn)
		{
			this.turnOn = turnOn;
		}

		public MessageType getTurnOff()
		{
			return turnOff;
		}

		public void setTurnOff(MessageType turnOff)
		{
			this.turnOff = turnOff;
		}

		public int getSlotId()
		{
			return slotId;
		}

		public void setSlotId(int slotId)
		{
			this.slotId = slotId;
		}

		public MessageType getEnableMessage()
		{
			return enableMessage;
		}

		public void setEnableMessage(MessageType enableMessage)
		{
			this.enableMessage = enableMessage;
		}

		public MessageType getDisableMessage()
		{
			return disableMessage;
		}

		public void setDisableMessage(MessageType disableMessage)
		{
			this.disableMessage = disableMessage;
		}
	}
}
