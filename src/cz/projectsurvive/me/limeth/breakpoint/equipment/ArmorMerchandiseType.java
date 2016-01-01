package cz.projectsurvive.me.limeth.breakpoint.equipment;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;

public enum ArmorMerchandiseType
{
	BOOTS(MessageType.SHOP_ITEM_ARMOR_BOOTS, 4),
	LEGGINGS(MessageType.SHOP_ITEM_ARMOR_LEGGINGS, 7),
	CHESTPLATE(MessageType.SHOP_ITEM_ARMOR_CHESTPLATE, 8),
	HELMET(MessageType.SHOP_ITEM_ARMOR_HELMET, 5);
	
	private final MessageType messageType;
	private final int materialAmount;
	
	private ArmorMerchandiseType(MessageType messageType, int materialAmount)
	{
		this.messageType = messageType;
		this.materialAmount = materialAmount;
	}
	
	public static ArmorMerchandiseType parse(String string)
	{
		for(ArmorMerchandiseType amt : values())
			if(amt.getTranslated().equalsIgnoreCase(string))
				return amt;
		
		return null;
	}
	
	public String getTranslated()
	{
		return getMessageType().getTranslation().getValue();
	}

	public MessageType getMessageType()
	{
		return messageType;
	}

	public int getMaterialAmount()
	{
		return materialAmount;
	}
}
