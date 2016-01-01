package cz.projectsurvive.me.limeth.breakpoint.language;


public enum MessageType implements Translateable
{
	//{{ENUMS	
		//{{FREQUENTLY USED
			OTHER_REGENMATTER_NAME("&cRegeneration matter"),
			OTHER_REGENMATTER_DESC("&7Heals 4 hearts."),
		//}}
		//{{EQUIPMENT
			EQUIPMENT_MINUTESLEFT("&7Minutes remaining: &f{1}"),
			EQUIPMENT_SKULLOWNER("&7Name: {1}"),
			EQUIPMENT_PLAYERSKULLNAME("{1}&f's skull"),
			EQUIPMENT_SKULLNAME("{1} &fskull"),
			EQUIPMENT_BLOCKNAME("{1} &fmask"),
		//}}
		//{{MENU
			//{{LOBBY
				MENU_ENCYCLOPEDIA_NAME("&bEncyclopedia"),
				MENU_ENCYCLOPEDIA_AUTHOR("&fCubedVoid.com"),
				MENU_ACHIEVEMENTS_NAME("&6Show achievements"),
				MENU_ACHIEVEMENTS_DESC("&7Unlocked achievements:\\n&7&f{1} &7/ {2}"),
				MENU_ACHIEVEMENTS_NEXTPAGE("Next page"),
				MENU_ACHIEVEMENTS_PREVIOUSPAGE("Previous page"),
				MENU_EMERALDS_NAME("&a&l{1}"),
				MENU_EMERALDS_DESC("&7Emeralds are the currency\\n&7you can use to buy the equipment\\n&7in the shop."),
				MENU_WELCOME_NAME("&4&lWelcome to Breakpoint!"),
				MENU_WELCOME_DESC("&7A CTF Mega-game! ;)"),
				MENU_GRAPH_NAME("&aActivity graph"),
				MENU_GRAPH_DESC("&7Shows the amount of\\n&7players on the server."),
				MENU_TRASH_NAME("&4Trash"),
				MENU_TRASH_DESC("&7Throw the items you\\n&7don't need in here."),
				MENU_TRASH_USE("&7&oYou threw the item '&r{1}&7&o' out."),
				MENU_VIPSLOT_NAME("&bVIP &7Space"),
				MENU_VIPSLOT_DESC("&7Purchase a &bVIP &7account\\n&7to gain the ability to\\n&7use this space."),
			//}}
			//{{IN-GAME
			MENU_LOBBY_NAME("&3Lobby"),
			MENU_LOBBY_DESC("&7Leave the arena\\n&7and join the lobby."),
			MENU_LOBBY_USE("&7You will spawn in the lobby after your next death."),
			MENU_STORE_NAME("&3Store"),
			MENU_STORE_DESC("&7Leave the arena\\n&7and enter the shop."),
			MENU_STORE_USE("&7You will spawn in the store after your next death."),
			MENU_SUICIDE_NAME("&cSuicide"),
			MENU_SUICIDE_DESC("&7Instantly kills you."),
			MENU_SCORE_NAME("&eScore"),
			MENU_SCORE_DESC("&2Kills: &a{1}\\n&7&4Deaths: &c{2}\\n&7&6K/D: &e{3}"),
			MENU_VIPINFO_NAME("&b&lVIP Information"),
			MENU_VIPINFO_DESC("&7Leave the arena and enter\\n&7the VIP information area."),
			MENU_VIPINFO_USE("&7You will spawn in the vip information area after your next death."),
			MENU_EMERALDS_USE("&7You will spawn in the money information area after your next death."),
			MENU_EGG_DESC("&7Click to choose this character.\\n&7You will spawn with it after your next death."),
			MENU_EGG_VIPDESC("&7This is a &bVIP &7character."),
			MENU_COMPASS_NAME("&dCrystal finder"),
			MENU_COMPASS_DESC("&7Points towards the crystal\\n&7of the opposite team."),
			MENU_QUICKCHAT_NAME("&6Quick chat"),
			MENU_QUICKCHAT_DESC("&7Sends a quick message\\n&7viewable by your team only."),
			MENU_CRYSTAL_NAME("{1}&lCRYSTAL"),
			MENU_CRYSTAL_DESC("&7You're holding the crystal\\n&7of the opponent team. Bring\\n&7it to the crystal of\\n&7your team and capture it!"),
			//}}
			//{{SETTINGS
			MENU_SETTINGS_NAME("&lSettings"),
			MENU_SETTINGS_DESC("&7Gameplay configuration"),
			MENU_SETTINGS_DEATHMESSAGES_TURNON("&aEnable &edeath messages"),
			MENU_SETTINGS_DEATHMESSAGES_TURNOFF("&cDisable &edeath messages"),
			MENU_SETTINGS_DEATHMESSAGES_DESC("&7When disabled, only kills\\n&7related to you will be\\n&7shown in the chat."),
			MENU_SETTINGS_DEATHMESSAGES_ENABLE("&aDeath messages have been enabled."),
			MENU_SETTINGS_DEATHMESSAGES_DISABLE("&cDeath messages have been disabled."),
			MENU_SETTINGS_EXTRASOUNDS_TURNON("&aEnable &eextra sounds"),
			MENU_SETTINGS_EXTRASOUNDS_TURNOFF("&cDisable &eextra sounds"),
			MENU_SETTINGS_EXTRASOUNDS_ENABLE("&aExtra sounds have been enabled."),
			MENU_SETTINGS_EXTRASOUNDS_DISABLE("&cExtra sounds have been disabled."),
			MENU_SETTINGS_EXTRASOUNDS_DESC("&7When enabled, extra sounds\\n&7from the Breakpoint\\n&7resourcepack won't play."),
			MENU_SETTINGS_SHOWENCHANTMENTS_TURNON("&aEnable &eshowing enchantments"),
			MENU_SETTINGS_SHOWENCHANTMENTS_TURNOFF("&cDisable &eshowing enchantments"),
			MENU_SETTINGS_SHOWENCHANTMENTS_DESC("&7When disabled, enchantments are\\nnot going to be visible client-side.\\nThis may help increase FPS."),
			MENU_SETTINGS_SHOWENCHANTMENTS_ENABLE("&aClient-side enchantments have been enabled."),
			MENU_SETTINGS_SHOWENCHANTMENTS_DISABLE("&cClient-side enchantments have been disabled."),
			//}}
			//{{PERKS
				MENU_PERKS_NAME("&dPerks"),
				MENU_PERKS_DESC("&7Customize your selected perks here."),
				MENU_PERKS_EMPTY("&cYou don't have any perks. You can buy perks in the shop."),
				MENU_PERKS_LIVESLEFT("&fLives left: &e{1}"),
				MENU_PERKS_ENABLE("&aEnable &r{1}"),
				MENU_PERKS_DISABLE("&cDisable &r{1}"),
				MENU_PERKS_FULL_VIP("&cYou can have maximum of {1} perks equipped at a time."),
				MENU_PERKS_FULL_NONVIP("&cYou can have maximum of {1} perks equipped at a time. Become a &8[&bVIP&8] &cplayer to get more perks!."),
			//}}PERKS
		//}}
		//{{PVP
			PVP_PAYBACK("&6&l�> PAYBACK <�"),
			PVP_HEADSHOT("&6You headshotted &r{1}&6!"),
			PVP_SPAWNKILLING("&cDo not kill your enemies at their spawn!"),
			PVP_KILLINFO_ASSIST("&8You assisted in killing {1}&8."),
			PVP_KILLINFO_KILLEDBY("&8You were killed by {1}&8."),
			PVP_KILLINFO_YOUKILLED("&8You killed {1}&8."),
			PVP_KILLINFO_DEATH("&8You died."),
			PVP_KILLINFO_DIED("{1} &8died."),
			PVP_KILLINFO_KILLED("{1} &8was killed by &r{2}&8."),
		//}}
		//{{MAP
			MAP_CHANGE("&eThe map has been changed to &a{1}&e!"),
			MAP_VOTING_HEADER("&6Map Voting"),
		//}}
		//{{SCOREBOARD
			SCOREBOARD_LOBBY_HEADER("&d&l&oBreakpoint"),
			SCOREBOARD_PROGRESS_CTF_HEADER("&dCrystals &e{1}"),
			SCOREBOARD_PROGRESS_CTF_TEAM_RED("&4Red team"),
			SCOREBOARD_PROGRESS_CTF_TEAM_BLUE("&1Blue team"),
			SCOREBOARD_PROGRESS_DM_HEADER("&bPoints &e{1}"),
		//}}
		//{{SHOP
			//{{PURCHASE
				SHOP_PURCHASE_ARMOR_SUCCESS("&aYou've successfully bought &r{1}&a for &e{2}&a emeralds."),
				SHOP_PURCHASE_PERK_SUCCESS("&aYou've successfully bought perk &r{1}&a for &e{2}&a emeralds."),
				SHOP_PURCHASE_ARMOR_QUESTION("&6Would you like to purchase &r{1}&6?\\n"
						+ "&7Price: &r{2}\\n"
						+ "&7Duration: &r{3} hours\\n"
						+ "&6Click this sign once again to confirm the purchase."),
				SHOP_PURCHASE_PERK_QUESTION("&6Would you like to purchase perk &r{1}&6?\\n"
						+ "&7Price: &r{2}\\n"
						+ "&7Duration: &r{3} lives\\n"
						+ "&6Click this sign once again to confirm the purchase."),
				SHOP_PURCHASE_NOTENOUGHEMERALDS("&cYou don't have enough emeralds to afford this item."),
				SHOP_PURCHASE_NOINVENTORYSPACE("&cYou don't have any free slot left in your inventory."),
				SHOP_PURCHASE_VIPSONLY("&cThis merchandise is for &8[&bVIP&8] &cusers only."),
			//}}
			//{{ITEMS
				SHOP_ITEM_PERK_LABEL("Perk"),
				SHOP_ITEM_SKULL_LABEL("Purchase skull"),
				SHOP_ITEM_ARMOR_HELMET("Helmet"),
				SHOP_ITEM_ARMOR_CHESTPLATE("Chestplate"),
				SHOP_ITEM_ARMOR_LEGGINGS("Leggings"),
				SHOP_ITEM_ARMOR_BOOTS("Boots"),
				SHOP_ITEM_ARMOR_NOCOLOR("&7No color"),
			//}}
		//}}
		//{{CHARACTERS
			CHARACTER_SELECT("[SELECT]"),
			CHARACTER_SWORDSMAN("Swordsman"),
			CHARACTER_KNIGHT("Knight"),
			CHARACTER_ARCHER("Archer"),
			CHARACTER_CHEMIST("Chemist"),
			CHARACTER_CULTIST("Cultist"),
			CHARACTER_PYRO("Pyro"),
			CHARACTER_NINJA("Ninja"),
			CHARACTER_HEAVY("Heavy"),
		//}}
		//{{FLAGS
			FLAG_INFO("This is the crystal of your team.\\n&7Do not let enemies steal it!\\n&7&6Get the crystal of the opposite\\n&7&6team and bring it to this one!"),
			FLAG_STEAL("&c&lYou stole the crystal of your enemies, bring it to the crystal of your team!"),
			FLAG_TAKE_RED("&c&lRed &ecrystal &ltaken &eby {1}&e!"),
			FLAG_TAKE_BLUE("&9&lBlue &ecrystal &ltaken &eby {1}&e!"),
			FLAG_DROP_RED("&c&lRed &ecrystal &ldropped&e!"),
			FLAG_DROP_BLUE("&9&lBlue &ecrystal &ldropped&e!"),
			FLAG_RETURN_RED("&c&lRed &ecrystal &lreturned &eby {1}&e!"),
			FLAG_RETURN_BLUE("&9&lBlue &ecrystal &lreturned &eby {1}&e!"),
			FLAG_SHATTER_RED("&c&lRed &ecrystal &lshattered&e!"),
			FLAG_SHATTER_BLUE("&9&lBlue &ecrystal &lshattered&e!"),
			FLAG_CAPTURE_RED("&c&lRed &ecrystal &lcaptured &eby {1}&e!\\n&7&eThe &9&lBlue &eteam &lscores&e!"),
			FLAG_CAPTURE_BLUE("&9&lBlue &ecrystal &lcaptured &eby {1}&e!\\n&7&eThe &c&lRed &eteam &lscores&e!"),
			FLAG_WARN_NEARSPAWN("&c&lDo not stand near your team's spawn while holding a crystal!"),
		//}}
		//{{RESULT
			RESULT_CTF_DRAW("&f&lThis round ended in a draw."),
			RESULT_CTF_WIN_RED("&c&lThe Red team wins!"),
			RESULT_CTF_WIN_BLUE("&9&lThe Blue team wins!"),
			RESULT_DM_WIN("&6&lPlayer &r{1}&6&l has won with &o{2}&6&l points!"),
			RESULT_DM_POSITION("&f&lYou've finished in &e&l{1}&f&l. place out of &e&l{2}&f&l!"),
			RESULT_CW_DRAW("&f&lThis round ended in a draw."),
			RESULT_CW_WIN("&f&lThe &7{1}&f&l clan wins!"),
			RESULT_CW_MATCH_SCORES("&f&lCurrent wins: &f[&e&l{1}&f x &e&l{2}&f]"),
			RESULT_CW_MATCH_DRAW("&f&lThe match ended in a draw."),
			RESULT_CW_MATCH_WIN("&f&lThe &7{1}&f&l clan won the match, congratulations!"),
		//}}
		//{{QUICKCHAT
			QUICKCHAT_1("Affirmative!"),
			QUICKCHAT_2("Negative!"),
			QUICKCHAT_3("Get their crystal!"),
			QUICKCHAT_4("Defend our crystal!"),
			QUICKCHAT_5("I have their crystal, cover me!"),
			QUICKCHAT_6("They have our crystal, bring it back!"),
			QUICKCHAT_7("Storm the front!"),
			QUICKCHAT_8("Team, fall back!"),
		//}}
		//{{VOTING
			VOTING_VOTE("&7You voted for the map &6{1}&7."),
			VOTING_END("&eVoting ended!\\n&eThe next map will be &a&l{1} &e({2} votes, {3}%).\\n&eThe map will be changed in 10 seconds."),
		//}}
		//{{LOBBY
			//{{GAME
				LOBBY_GAME_NOTREADY("&cThe game '&e{1}&c' is not ready yet."),
				LOBBY_GAME_JOIN("&7You've joined game '&e&l{1}&7'."),
				LOBBY_GAME_CW_NOTALLOWED("&cYou have to challenge a clan first!"),
				LOBBY_GAME_CW_TEAMFULL("&cYour team is full, there are {1} players already!"),
			//}}
			//{{CHARACTER
				LOBBY_CHARACTER_NOTFOUND("&cThe character '&e{1}&c' doesn't exist."),
				LOBBY_CHARACTER_ALREADYSELECTED("&cYou have already selected a character. ({1})"),
				LOBBY_CHARACTER_SELECTED("&7Character &e{1} &7selected!"),
				LOBBY_CHARACTER_VIPSONLY("&cCharacter &e{1}&c is available to VIP players only.\\n&cFor more information go to the lobby."),
			//}}
			//{{TEAM
				LOBBY_TEAM_WARN("&cSelect a team first."),
				LOBBY_TEAM_SELECTVIPSONLY("&cOnly &8[&bVIP&8] players can select a team, step on the pressure plate below to get assigned to a random team."),
				LOBBY_TEAM_BALANCEJOINRED("&cThe teams are not balanced, join the &lRED &cteam, please."),
				LOBBY_TEAM_BALANCEJOINBLUE("&cThe teams are not balanced, join the &9&lBLUE &cteam, please."),
			//}}
		//}}
		//{{CW
			CW_BROADCAST_PREFIX("&8[&a{1}&8] [&7{2} &fVS &7{3}&8]"),
			CW_BROADCAST_MATCHSTART("The match has just started!"),
		//}}CW
		//{{CLAN
			CLAN_JOIN("&7You joined the clan &r{1}&7."),
			CLAN_KICK("&c&lYou were kicked from the clan &r{1}&c&l."),
			CLAN_LEAVE("&7You have left the clan &r{1}&7."),
			CLAN_BREAKUP("&7You broke up your clan."),
			CLAN_OTHERJOIN("&7Player &r{1} &7joined your clan."),
			CLAN_OTHERKICK("&7Player &r{1} &7was kicked from your clan."),
			CLAN_OTHERLEAVE("&7Player &r{1} &7has left your clan."),
			CLAN_OTHERBREAKUP("&c&lYour clan broke up, because the leader has left it."),
		//}}
		//{{RANK
			RANK_PLAYER_NOTFOUND("&cPlayer &e{1} &cnot found!"),
			RANK_PLAYER_DESC("&6Statistics of player &e&l{1} &6[&e{2}.&6]:"),
			RANK_PLAYER_KILLS("&7Kills: &e{1}"),
			RANK_PLAYER_DEATHS("&7Deaths: &e{1}"),
			RANK_PLAYER_KDR("&7K/D: &e{1}"),
			RANK_PLAYER_ACHIEVEMENTS("&7Achievements: &e{1}"),
			RANK_PLAYER_MERCHANDISEPURCHASED("&7Merchandise purchased: &e{1}"),
			RANK_PLAYER_CRYSTALSSTOLEN("&7Crystals stolen: &e{1}"),
			RANK_PLAYER_CRYSTALSCAPTURED("&7Crystals captured: &e{1}"),
		//{{TOP
		RANK_TOP_UPDATING("&7The ranks are updating, please wait."),
		RANK_TOP_CLANEMPTYPAGE("&7No clans found on this page."),
		RANK_TOP_EMPTYPAGE("&7No players found on this page."),
		RANK_TOP_FORMAT("&8[&f{1}&8] &9&l{2} &2K: &a{3} &4D: &c{4} &6K/D: &e{5}"),
		RANK_TOP_CLANFORMAT("&8[&f{1}&8] &7{2} &f&l{3}"),
		//}}
		//}}
		//{{TEAM BALANCE
			BALANCE_MOVERED("&f&lYou were moved to the &c&lred &f&lteam, because the teams were not balanced."),
			BALANCE_MOVEBLUE("&f&lYou were moved to the &9&lblue &f&lteam, because the teams were not balanced."),
		//}}
		//{{COMMANDS
			//{{HELPOP
			COMMAND_HELPOP_USAGE("&7Usage: /{1} [Question]"),
			COMMAND_HELPOP_SUCCESS("&7The helpers were informed, they should respond soon."),
			COMMAND_HELPOP_FAILURE("&7No helpers are online at the moment."),
			//}}
			//{{CLAN
				//{{INFO
					COMMAND_CLAN_INFO_CMD("info"),
					COMMAND_CLAN_INFO_PATH("clan info [Name]"),
					COMMAND_CLAN_INFO_DESC("Shows information about a clan"),
					COMMAND_CLAN_INFO_EXE_NOTFOUND("&cClan &7{1} &cdoes not exist."),
					COMMAND_CLAN_INFO_FORMAT_PENDINGCHALLENGE("&7{1} &7(&f{2}&7, &f{3} hr�cu&7)"),
					COMMAND_CLAN_INFO_FORMAT_MATCHRESULT("&7{1} {2}{3}:{4}"),
					COMMAND_CLAN_INFO_EXE_SUCCESS("&6Information about clan &7{1}&6 [&e{6}&6]:\\n&f&l{15} &8[&a{12}&8�&f{13}&8�&c{14}&8]\\n&7Leader: &f{2}\\n&7Moderators: &f{3}\\n&7Members: &f{4}\\n&7Invitations: &f{5}\\n&7Challenges: &f{10}\\n&7Match results: &f{11}"),
				//}}INFO
				//{{JOIN
					COMMAND_CLAN_JOIN_CMD("join"),
					COMMAND_CLAN_JOIN_PATH("clan join [Name]"),
					COMMAND_CLAN_JOIN_DESC("Joins a clan, if invited"),
					COMMAND_CLAN_JOIN_EXE_ALREADYJOINED("&cYou are already a member of clan &7{1}&c.\\n&cTo leave the clan, type '&o/clan leave&c'."),
					COMMAND_CLAN_JOIN_EXE_NOTFOUND("&cClan &7{1} &cdoes not exist."),
					COMMAND_CLAN_JOIN_EXE_NOTINVITED("&cYou are not invited to the clan &7{1}&c."),
				//}}JOIN
				//{{LEAVE
					COMMAND_CLAN_LEAVE_CMD("leave"),
					COMMAND_CLAN_LEAVE_PATH("clan leave"),
					COMMAND_CLAN_LEAVE_DESC("Leaves a clan"),
					COMMAND_CLAN_LEAVE_EXE_NOTMEMBER("&cYou are not a member of any clan."),
				//}}LEAVE
				//{{CREATE
					COMMAND_CLAN_CREATE_CMD("create"),
					COMMAND_CLAN_CREATE_PATH("clan create [Name]"),
					COMMAND_CLAN_CREATE_DESC("Creates a clan with specified name"),
					COMMAND_CLAN_CREATE_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can create clans."),
					COMMAND_CLAN_CREATE_EXE_ALREADYJOINED("&cYou are already a member of clan &7{1}&c.\\n&cTo leave the clan, type '&o/clan leave&c'."),
					COMMAND_CLAN_CREATE_EXE_SUCCESS("&aA clan with name &7{1} &asuccessfully created!"),
				//}}CREATE
				//{{RENAME
					COMMAND_CLAN_RENAME_CMD("rename"),
					COMMAND_CLAN_RENAME_PATH("clan rename [Name]"),
					COMMAND_CLAN_RENAME_DESC("Renames a clan with specified name"),
					COMMAND_CLAN_RENAME_COLORHINT("&7You can use color codes to color the name of your clan.\\n&11&22&33&44&55&66&77&88&99&00&aa&bb&cc&dd&ee&ff"),
					COMMAND_CLAN_RENAME_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can rename clans."),
					COMMAND_CLAN_RENAME_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_RENAME_EXE_NOTLEADER("&cYou are not the leader of this clan (&7{1}&c)!"),
					COMMAND_CLAN_RENAME_EXE_INCORRECTLENGTH("&cIncorrect length of the name, the name has to be minimum {1} and maximum {2} characters long."),
					COMMAND_CLAN_RENAME_EXE_BANNEDCHARACTERS("&cThe name contains banned characters."),
					COMMAND_CLAN_RENAME_EXE_ALREADYEXISTS("&cA clan with name &7{1} &calready exists, choose a different name, please."),
					COMMAND_CLAN_RENAME_EXE_SUCCESS("&7Your clan has been renamed to &7{1}&7!"),
				//}}RENAME
				//{{INVITE
					COMMAND_CLAN_INVITE_CMD("invite"),
					COMMAND_CLAN_INVITE_PATH("clan invite [Player]"),
					COMMAND_CLAN_INVITE_DESC("Invites a player to your clan"),
					COMMAND_CLAN_INVITE_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can invite others to join their clan."),
					COMMAND_CLAN_INVITE_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_INVITE_EXE_NOTMODERATOR("&cYou are not a moderator or leader of this clan (&7{1}&c)!"),
					COMMAND_CLAN_INVITE_EXE_INVITEDYOURSELF("&cNice try, but you can't invite yourself to your clan."),
					COMMAND_CLAN_INVITE_EXE_ALREADYMEMBER("&cPlayer &e{1} &cis already a member of your clan."),
					COMMAND_CLAN_INVITE_EXE_SUCCESS_TAKENDOWN_SENDER("&cYou have taken down the invitation for &e{1} &cto your clan."),
					COMMAND_CLAN_INVITE_EXE_SUCCESS_TAKENDOWN_TARGET("&7The invitation to the clan &7{1} &7was taken down."),
					COMMAND_CLAN_INVITE_EXE_SUCCESS_CREATE_SENDER("&aYou have invited &e{1} &ato your clan."),
					COMMAND_CLAN_INVITE_EXE_SUCCESS_CREATE_TARGET("&7You were invited to the clan &7{1}&7.\\n&7To join the clan, type '&o/clan join {2}&7'."),
				//}}INVITE
				//{{KICK
					COMMAND_CLAN_KICK_CMD("kick"),
					COMMAND_CLAN_KICK_PATH("clan kick [Player]"),
					COMMAND_CLAN_KICK_DESC("Kicks out a member of your clan"),
					COMMAND_CLAN_KICK_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can kick others from their clan."),
					COMMAND_CLAN_KICK_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_KICK_EXE_NOTMODERATOR("&cYou are not a moderator or leader of this clan."),
					COMMAND_CLAN_KICK_EXE_TARGETNOTMEMBER("&cThe player &e{1} &cis not a member of your clan."),
				//}}KICK
				//{{MODERATOR
					COMMAND_CLAN_MODERATOR_CMD("mod"),
					COMMAND_CLAN_MODERATOR_PATH("clan mod [Player]"),
					COMMAND_CLAN_MODERATOR_DESC("Gives the player the ability to invite and kick players."),
					COMMAND_CLAN_MODERATOR_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can choose moderators in their clan."),
					COMMAND_CLAN_MODERATOR_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_MODERATOR_EXE_NOTLEADER("&cOnly the leader can select moderators for their clan."),
					COMMAND_CLAN_MODERATOR_EXE_TARGETNOTMEMBER("&cThe player &e{1} &cis not a member of your clan."),
					COMMAND_CLAN_MODERATOR_EXE_UPGRADED_SENDER("&aPlayer &e{1} &ais now a moderator of your clan."),
					COMMAND_CLAN_MODERATOR_EXE_UPGRADED_TARGET("&7You are now a moderator in clan {1}&7."),
					COMMAND_CLAN_MODERATOR_EXE_DOWNGRADED_SENDER("&aPlayer &e{1} &ais no longer a moderator of your clan."),
					COMMAND_CLAN_MODERATOR_EXE_DOWNGRADED_TARGET("&cYou are no longer a moderator in clan &7{1}&c."),
				//}}MODERATOR
				//{{CHALLENGE
					COMMAND_CLAN_CHALLENGE_CMD("challenge"),
					COMMAND_CLAN_CHALLENGE_PATH("clan challenge [Clan] [Day] [MaximumPlayers]"),
					COMMAND_CLAN_CHALLENGE_DESC("Challenges a clan to a fight on the next specified day."),
					COMMAND_CLAN_CHALLENGE_EXE_NOGAME("&cNo default game for challenges set."),
					COMMAND_CLAN_CHALLENGE_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can challenge other clans."),
					COMMAND_CLAN_CHALLENGE_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_CHALLENGE_EXE_NOTLEADER("&cYou are not the leader of this clan."),
					COMMAND_CLAN_CHALLENGE_EXE_NOTFOUND("&cClan &7{1} &cdoes not exist."),
					COMMAND_CLAN_CHALLENGE_EXE_CHALLENGEDITSELF("&cYour clan cannot challenge itself."),
					COMMAND_CLAN_CHALLENGE_EXE_UNKNOWNDAY("&cUnknown day '&e{1}&c'."),
					COMMAND_CLAN_CHALLENGE_EXE_DAYTAKEN("&cThis day is already taken by &7{1} &cand &7{2}."),
					COMMAND_CLAN_CHALLENGE_EXE_INCORRECTMAXPLAYERS("&cThe maximum player amount has to be a whole number."),
					COMMAND_CLAN_CHALLENGE_EXE_TOOFEWMAXPLAYERS("&cThe maximum player amount has to be larger than 0."),
					COMMAND_CLAN_CHALLENGE_EXE_MAXPLAYERSFEWPLAYERS_OWN("&cYour clan does not have that much players, choose lower maximum player amount."),
					COMMAND_CLAN_CHALLENGE_EXE_MAXPLAYERSFEWPLAYERS_THEIR("&cThe challenged clan does not have enough players, choose lower maximum player amount."),
					COMMAND_CLAN_CHALLENGE_EXE_ALREADYCHALLENGED("&cYou have already challenged this clan (&e{1}&c), wait for them to respond."),
					COMMAND_CLAN_CHALLENGE_EXE_CHALLENGEPENDING("&cYou are already challenged by this clan (&e{1}&c), respond them first."),
					COMMAND_CLAN_CHALLENGE_EXE_SUCCESS("&aYou have challenged clan &7{1} &afor a match on {2} with {3} maximum players."),
					COMMAND_CLAN_CHALLENGE_EXE_SUCCESSOTHER("&7You were challenged by clan {1} &7for a match on {2} with {3} maximum players."),
				//}}CHALLENGE
				//{{ACCEPT
					COMMAND_CLAN_ACCEPT_CMD("accept"),
					COMMAND_CLAN_ACCEPT_PATH("clan accept [Clan]"),
					COMMAND_CLAN_ACCEPT_DESC("Accepts a pending challenge from a clan."),
					COMMAND_CLAN_ACCEPT_EXE_NOGAME("&cNo default game for challenges set."),
					COMMAND_CLAN_ACCEPT_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can accept pending challenges from other clans."),
					COMMAND_CLAN_ACCEPT_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_ACCEPT_EXE_NOTLEADER("&cYou are not the leader of this clan."),
					COMMAND_CLAN_ACCEPT_EXE_NOTFOUND("&cClan &7{1} &cdoes not exist."),
					COMMAND_CLAN_ACCEPT_EXE_NOCHALLENGE("&cYou haven't been challenged by clan &7{1}&c."),
					COMMAND_CLAN_ACCEPT_EXE_SUCCESS("&aYou have accepted the challenge from clan &7{1}&a, the match will be on {2} at 6 PM."),
					COMMAND_CLAN_ACCEPT_EXE_SUCCESSOTHER("&7Clan &7{1} has accepted your challenge, the match will be on {2} at 6 PM."),
				//}}ACCEPT
				//{{ACCEPT
					COMMAND_CLAN_REJECT_CMD("reject"),
					COMMAND_CLAN_REJECT_PATH("clan reject [Clan]"),
					COMMAND_CLAN_REJECT_DESC("Rejects a pending challenge from a clan."),
					COMMAND_CLAN_REJECT_EXE_NOGAME("&cNo default game for challenges set."),
					COMMAND_CLAN_REJECT_EXE_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can reject pending challenges from other clans."),
					COMMAND_CLAN_REJECT_EXE_NOTMEMBER("&cYou are not a member of any clan."),
					COMMAND_CLAN_REJECT_EXE_NOTLEADER("&cYou are not the leader of this clan."),
					COMMAND_CLAN_REJECT_EXE_NOTFOUND("&cClan &7{1} &cdoes not exist."),
					COMMAND_CLAN_REJECT_EXE_NOCHALLENGE("&cYou haven't been challenged by clan &7{1}&c."),
					COMMAND_CLAN_REJECT_EXE_SUCCESS("&aYou have rejected the challenge from clan &7{1}&a."),
					COMMAND_CLAN_REJECT_EXE_SUCCESSOTHER("&7Clan &7{1} has rejected your challenge."),
				//}}ACCEPT
			//}}
			//{{CW
				COMMAND_CW_FORMAT_MESSAGE("&c&lClan Wars schedule:\\n{1}\\n&c&l---"),
				COMMAND_CW_FORMAT_DAY("&7{1} &fVS &7{2} &8(&7{3}&8)"),
				COMMAND_CW_DAYNOTTAKEN("&7Not taken"),
				COMMAND_CW_EXE_NOGAME("&cNo default game for challenges set."),
			//}}CW
			//{{TOP
				COMMAND_TOP_EXE_INCORRECTPAGE("&cThe page number has to be a whole number."),
				COMMAND_TOP_EXE_NEGATIVEORZERO("&cThe page number has to be larger than 0."),
			//}}
			//{{RANK
				COMMAND_RANK_PATH("rank [Player]"),
				COMMAND_RANK_DESC("Shows statistics of a player"),
			//}}
			//{{ACHIEVEMENTS
				COMMAND_ACHIEVEMENTS_PATH("achievements (Player)"),
				COMMAND_ACHIEVEMENTS_DESC("Shows achievements of a player"),
				COMMAND_ACHIEVEMENTS_EXE_PLAYEROFFLINE("&cPlayer &e{1} &cdoes not exist or is offline."),
			//}}
			//{{SKULL
				COMMAND_SKULL_PATH("skull [Player]"),
				COMMAND_SKULL_DESC("Changes the look of the skull you're wearing to the player's skin"),
				COMMAND_SKULL_WARNING("&7You need to be wearing the '&fCHAMELEON&7' skull from the store, when using this command."),
				COMMAND_SKULL_EXE_NOTINLOBBY("&cYou need to be in the lobby to use this command."),
				COMMAND_SKULL_EXE_NOTRENAMEABLE("&cYou need to be wearing the '&eCHAMELEON&c' skull to use this command."),
				COMMAND_SKULL_EXE_NAMEBANNED("&cName '&e{1}&c' is banned, please use a different name."),
				COMMAND_SKULL_EXE_SUCCESS("&7The name of your '&fCHAMELEON&7' skull has been set to '&e{1}&7'."),
			//}}
			//{{FLY
				COMMAND_FLY_VIPSONLY("&cOnly &8[&bVIP&8] &cplayers can fly in the lobby."),
				COMMAND_FLY_NOTLOBBY("&cYou are not in the lobby, leave the current game."),
				COMMAND_FLY_TOOFAR("&cYou traveled too far from spawn."),
				COMMAND_FLY_ENABLED("&aFlying enabled."),
				COMMAND_FLY_DISABLED("&aFlying disabled."),
			//}}
		//}}
		//{{OTHER
			OTHER_WARNPEARL("&cYou can't throw enderpearls, when you're holding a crystal."),
			OTHER_CHARACTERRESPAWNINFO("&7You will spawn with this character after your next death."),
			OTHER_AFKKICK("&7You were kicked due to 3 minutes of inactivity."),
			OTHER_NOPERMISSION("&cYou don't have permission."),
			OTHER_EMERALDS_INCREASE("&7You have gained {1} emeralds. &a&l{2}"),
			OTHER_EMERALDS_DECREASE("&cYou have lost {1} emeralds."),
			OTHER_TEAMJOIN_RED("&7You have joined the &c&lRED &7team."),
			OTHER_TEAMJOIN_BLUE("&7You have joined the &9&lBLUE &7team."),
			OTHER_ERROR("&cAn error occurred, please inform the admins."),
			OTHER_VIPFEATURE("&3Don't you have a &8[&bVIP&8] &3account yet?\\n&3Get &b{1}&3!\\n&8[&bVIP&8] &3costs only 4.99$!"),
			OTHER_RESTART("&7The server has been reloaded."),
		//}}
		//{{CALENDAR
			CALENDAR_TODAY("Today"),
			CALENDAR_DAY_MONDAY("Monday"),
			CALENDAR_DAY_TUESDAY("Tuesday"),
			CALENDAR_DAY_WEDNESDAY("Wednesday"),
			CALENDAR_DAY_THURSDAY("Thursday"),
			CALENDAR_DAY_FRIDAY("Friday"),
			CALENDAR_DAY_SATURDAY("Saturday"),
			CALENDAR_DAY_SUNDAY("Sunday"),
		//}}
		//{{ACHIEVEMENTS
			ACHIEVEMENT_GET("&5Congratulations, you've earned the achievement &d&l{1}&5!"),
		//}}
		//{{CHAT
			CHAT_BREAKPOINT("&8[&d&lBreakpoint&8]&e"),
			CHAT_PREFIX_ADMIN("&8[&4Admin&8]&r"),
			CHAT_PREFIX_MODERATOR("&8[&1Moderator&8]&r"),
			CHAT_PREFIX_HELPER("&8[&7Helper&8]&r"),
			CHAT_PREFIX_VIP("&8[&bVIP&8]&r"),
			CHAT_PREFIX_YOUTUBE("&8[&eYouTube&8]&r"),
			CHAT_PREFIX_SPONSOR("&8[&aSponsor&8]&r"),
			CHAT_TAGPREFIX_VIP("&6VIP"),
			CHAT_TAGPREFIX_YOUTUBE("&eYT"),
			CHAT_TAGPREFIX_SPONSOR("&aSPR"),
		//}}
		//{{GAMETYPES
			GAMETYPE_CTF("Capture the Flag"),
			GAMETYPE_DM("Deathmatch"),
			GAMETYPE_CW("Clan Wars"),
		//}}
		//{{PERKS
			PERK_NOTICE_BROKEN("&c&lPerk &f&l{1} &c&lhas just broken! &cPurchase more perks in the shop."),
			PERK_AGILITY_NAME("Agility"),
			PERK_AGILITY_DESC("&7Increases speed by 10%."),
			PERK_STABILITY_NAME("Stability"),
			PERK_STABILITY_DESC("&7Increases knockback stability by 10%."),
			PERK_STRENGTH_NAME("Strength"),
			PERK_STRENGTH_DESC("&7Increases melee damage by 10%."),
			PERK_VITALITY_NAME("Vitality"),
			PERK_VITALITY_DESC("&7Increases health by 10%."),
			PERK_FIRESPREADER_NAME("Fire Spreading"),
			PERK_FIRESPREADER_DESC("&7Provides a 10% chance of\\n&7lighting an enemy on fire."),
			PERK_POWER_NAME("Power"),
			PERK_POWER_DESC("&7Increases projectile damage by 10%."),
			PERK_SPLITTER_NAME("Splitter"),
			PERK_SPLITTER_DESC("&7Increases damage dealt by\\n&7critical melee hit by 15%."),
			PERK_AIRBORN_NAME("Airborn"),
			PERK_AIRBORN_DESC("&7Increases jump height by 50%."),
			PERK_SUICIDE_NAME("Suicide"),
			PERK_SUICIDE_DESC("&7Increases both given and\\n&7taken melee damage by 25%."),
		//}}
		//{{EVENTS
			//{{ADVENT
				EVENT_ADVENT_BLOCKNAME("&cA&fd&cv&fe&cn&ft &7{2}-{1} &e{3}"),
				EVENT_ADVENT_MAP_NAME("&cA&fd&cv&fe&cn&ft&7n� kalend�r"),
				EVENT_ADVENT_MAP_HEADER(">ADVENT<"),
				EVENT_ADVENT_MAP_DESCRIPTION("Right click and enjoy!"),
				EVENT_ADVENT_EARN("&aYou've just earned todays gift."),
				EVENT_ADVENT_ALREADYEARNED("&cYou have already earned today's gift!"),
				EVENT_ADVENT_NOSPACE("&cYou don't have any space in your inventory for the gift!"),
			//}}ADVENT
		//}}
	//}}
	
	;
	
	private final String defaultTranslation;
	private Translation translation;
	
	private MessageType(String defaultTranslation)
	{
		this.defaultTranslation = defaultTranslation;
	}
	
	@Override
	public String getDefaultTranslation()
	{
		return defaultTranslation;
	}
	
	@Override
	public String getYamlPath()
	{
		String name = name();
		String[] parts = name.split("_");
		StringBuilder pathBuilder = new StringBuilder(toCamelCase(parts[0]));
		
		for(int i = 1; i < parts.length; i++)
			pathBuilder.append('.').append(toCamelCase(parts[i]));
		
		return pathBuilder.toString();
	}
	
	private static String toCamelCase(String string)
	{
		return toCamelCase(string, " ");
	}
	
	private static String toCamelCase(String string, String splitter)
	{
		String[] parts = string.split(splitter);
		StringBuilder builder = new StringBuilder();
		builder.append(parts[0].toLowerCase());
		
		for(int i = 1; i < parts.length; i++)
		{
			String part = parts[i];
			String camelCase = Character.toUpperCase(part.charAt(0)) + part.substring(1).toLowerCase();
			builder.append(camelCase);
		}
		
		return builder.toString();
	}

	@Override
	public void setTranslation(Translation translation)
	{
		this.translation = translation;
	}

	@Override
	public Translation getTranslation()
	{
		return translation;
	}
}
