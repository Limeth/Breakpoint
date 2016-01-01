package cz.projectsurvive.me.limeth.breakpoint.perks;

import java.util.List;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.comphenix.example.Attributes;
import com.comphenix.example.Attributes.Attribute;
import com.comphenix.example.Attributes.AttributeType;
import com.comphenix.example.Attributes.Operation;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public enum PerkType
{
	//{{STATIC
	@SuppressWarnings("deprecation")
	AGILITY(MessageType.PERK_AGILITY_NAME, MessageType.PERK_AGILITY_DESC, new MaterialData(Material.POTION, (byte) 8194), new Attribute[]
		{
			getAttribute(AttributeType.GENERIC_MOVEMENT_SPEED, Operation.MULTIPLY_PERCENTAGE, 0.1)
		}),
	STABILITY(MessageType.PERK_STABILITY_NAME, MessageType.PERK_STABILITY_DESC, new MaterialData(Material.CHAINMAIL_CHESTPLATE), new Attribute[]
		{
			getAttribute(AttributeType.GENERIC_KNOCKBACK_RESISTANCE, Operation.MULTIPLY_PERCENTAGE, 0.1)
		}),
	STRENGTH(MessageType.PERK_STRENGTH_NAME, MessageType.PERK_STRENGTH_DESC, new MaterialData(Material.IRON_SWORD), new Attribute[]
		{
			getAttribute(AttributeType.GENERIC_ATTACK_DAMAGE, Operation.MULTIPLY_PERCENTAGE, 0.1)
		}),
	@Deprecated
	VITALITY(MessageType.PERK_VITALITY_NAME, MessageType.PERK_VITALITY_DESC, new MaterialData(Material.BOW), new Attribute[]
		{
			getAttribute(AttributeType.GENERIC_MAX_HEALTH, Operation.ADD_NUMBER, 20)
		}),
	POWER(MessageType.PERK_POWER_NAME, MessageType.PERK_POWER_DESC, new MaterialData(Material.BOW))
	{
		public final double MULTIPLIER = 1.1;
		
		@Override
		public void onDamageDealtByProjectile(EntityDamageByEntityEvent event)
		{
			event.setDamage(event.getDamage() * MULTIPLIER);
		}
	},
	FIRESPREADER(MessageType.PERK_FIRESPREADER_NAME, MessageType.PERK_FIRESPREADER_DESC, new MaterialData(Material.BLAZE_POWDER))
	{
		public final double CHANCE = 0.1, DURATION = 5;
		
		@Override
		public void onDamageDealtByEntity(EntityDamageByEntityEvent event)
		{
			Random rnd = new Random();
			
			if(rnd.nextDouble() < CHANCE)
				event.getEntity().setFireTicks((int) (20 * DURATION));
		}
	},
	SPLITTER(MessageType.PERK_SPLITTER_NAME, MessageType.PERK_SPLITTER_DESC, new MaterialData(Material.FIREBALL))
	{
		public final double MULTIPLIER = 1.15;
		
		@Override
		public void onDamageDealtByPlayer(EntityDamageByEntityEvent event)
		{
			Entity damager = event.getDamager();
			
			if(damager.isOnGround())
				return;
			
			Vector velocity = damager.getVelocity();
			double y = velocity.getY();
			
			if(y >= 0)
				return;
			
			event.setDamage(event.getDamage() * MULTIPLIER);
		}
	},
	AIRBORN(MessageType.PERK_AIRBORN_NAME, MessageType.PERK_AIRBORN_DESC, new MaterialData(Material.FEATHER))
	{
		@Override
		public void onSpawn(BPPlayer bpPlayer)
		{
			Player player = bpPlayer.getPlayer();
			
			player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, true), true);
		}
	},
	SUICIDE(MessageType.PERK_SUICIDE_NAME, MessageType.PERK_SUICIDE_DESC, new MaterialData(Material.SKULL_ITEM))
	{
		public final double MULTIPLIER = 1.25;
		
		@Override
		public void onDamageDealtByPlayer(EntityDamageByEntityEvent event)
		{
			event.setDamage(event.getDamage() * MULTIPLIER);
		}
		
		@Override
		public void onDamageTakenFromPlayer(EntityDamageByEntityEvent event)
		{
			event.setDamage(event.getDamage() * MULTIPLIER);
		}
	}
	;
	
	private static Attribute getAttribute(AttributeType type, Operation operation, double amount)
	{
		return Attribute.newBuilder().type(type).operation(operation).amount(amount)
				.name("Breakpoint Perk Attribute").build();
	}
	
	public static PerkType parse(String translatedName, boolean ignoreCase)
	{
		if(translatedName == null)
			return null;
		
		if(ignoreCase)
		{
			for(PerkType perk : values())
				if(translatedName.equalsIgnoreCase(perk.getName()))
					return perk;
		}
		else
			for(PerkType perk : values())
				if(translatedName.equals(perk.getName()))
					return perk;
		
		return null;
	}
	
	public static PerkType parse(String name)
	{
		return parse(name, false);
	}
	//}}STATIC
	
	private final MessageType name, description;
	private final MaterialData materialData;
	private final Attribute[] attributes;
	
	private PerkType(MessageType name, MessageType description, MaterialData materialData, Attribute[] attributes)
	{
		this.name = name;
		this.description = description;
		this.materialData = materialData;
		this.attributes = attributes;
	}
	
	private PerkType(MessageType name, MessageType description, MaterialData materialData)
	{
		this(name, description, materialData, null);
	}
	
	public void onSpawn(BPPlayer bpPlayer) {}
	public void onDamageDealtByEntity(EntityDamageByEntityEvent event) {}
	public void onDamageDealtByProjectile(EntityDamageByEntityEvent event) {}
	public void onDamageDealtByPlayer(EntityDamageByEntityEvent event) {}
	public void onDamageTakenFromEntity(EntityDamageByEntityEvent event) {}
	public void onDamageTakenFromProjectile(EntityDamageByEntityEvent event) {}
	public void onDamageTakenFromPlayer(EntityDamageByEntityEvent event) {}
	
	public ItemStack applyToClonedItemStack(ItemStack is)
	{
		ItemStack is2 = is.clone();
		
		applyToItemStack(is2);
		
		return is2;
	}
	
	public ItemStack applyToItemStack(ItemStack is)
	{
		if(attributes == null)
			return is;
		
		Attributes attributes = new Attributes(is);
		
		for(Attribute attribute : this.attributes)
			attributes.add(attribute);
		
		return attributes.getStack();
	}
	
	public String getName()
	{
		return name.getTranslation().getValue();
	}

	public List<String> getDescription()
	{
		return description.getTranslation().getValues();
	}
	
	public MessageType getNameMessageType()
	{
		return name;
	}

	public MessageType getDescriptionMessageType()
	{
		return description;
	}

	public Attribute[] getAttributes()
	{
		return attributes;
	}

	public MaterialData getMaterialData()
	{
		return materialData;
	}
}
