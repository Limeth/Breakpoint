package cz.projectsurvive.me.limeth.breakpoint.managers;

import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;

public class ChatManager
{
	public static String prefixAdmin;
	public static String prefixModerator;
	public static String prefixHelper;
	public static String prefixVIP;
	public static String prefixYT;
	public static String prefixSponsor;
	public static String tagPrefixVIP;
	public static String tagPrefixYT;
	public static String tagPrefixSponsor;
	
	public static void loadStrings()
	{
		prefixAdmin = MessageType.CHAT_PREFIX_ADMIN.getTranslation().getValue();
		prefixModerator = MessageType.CHAT_PREFIX_MODERATOR.getTranslation().getValue();
		prefixHelper = MessageType.CHAT_PREFIX_HELPER.getTranslation().getValue();
		prefixVIP = MessageType.CHAT_PREFIX_VIP.getTranslation().getValue();
		prefixYT = MessageType.CHAT_PREFIX_YOUTUBE.getTranslation().getValue();
		prefixSponsor = MessageType.CHAT_PREFIX_SPONSOR.getTranslation().getValue();
		tagPrefixVIP = MessageType.CHAT_TAGPREFIX_VIP.getTranslation().getValue();
		tagPrefixYT = MessageType.CHAT_TAGPREFIX_YOUTUBE.getTranslation().getValue();
		tagPrefixSponsor = MessageType.CHAT_TAGPREFIX_SPONSOR.getTranslation().getValue();
	}
}
