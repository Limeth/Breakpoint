package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWGame;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWScheduler;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.language.Translation;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.StatisticsManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;
import cz.projectsurvive.me.limeth.breakpoint.statistics.CWMatchResult;

public class ClanCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!(sender instanceof Player))
			return true;
		
		Player player = (Player) sender;
		
		if(args.length <= 0)
			listClanCommands(sender, ChatColor.AQUA);
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_INFO_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan bpClan;
			if(args.length <= 1)
			{
				bpClan = Clan.getByPlayer(playerName);
				
				if(bpClan == null)
				{
					sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_INFO_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_INFO_DESC.getTranslation().getValue(), ChatColor.AQUA));
					return true;
				}
			}
			else
			{
				String clanName = args[1];
				bpClan = Clan.get(clanName);
				
				if(bpClan == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_INFO_EXE_NOTFOUND.getTranslation().getValue(clanName));
					return true;
				}
			}
			
			String clanNameColored = bpClan.getColoredName();
			String leader = bpClan.getLeaderName();
			List<String> moderators = bpClan.getModeratorNames();
			List<String> members = bpClan.getMemberNames();
			List<String> invited = bpClan.getInvitedNames();
			StringBuilder moderatorString = new StringBuilder(),
					memberString = new StringBuilder(),
					invitedString = new StringBuilder(),
					pendingChallengesString = new StringBuilder(),
					matchResultsString = new StringBuilder();
			
			for(String moderator : moderators)
			{
				String modName = getOnlinePlayerName(moderator);
				moderatorString.append(modName).append(", ");
			}
			
			for(String member : members)
			{
				String memName = getOnlinePlayerName(member);
				memberString.append(memName).append(", ");
			}
			
			for(String inv : invited)
			{
				String invName = getOnlinePlayerName(inv);
				invitedString.append(invName).append(", ");
			}
			
			Integer rawRank = StatisticsManager.getRank(bpClan);
			String rank = rawRank != null ? Integer.toString(rawRank) : "?";
			Translation ctrans = MessageType.COMMAND_CLAN_INFO_FORMAT_PENDINGCHALLENGE.getTranslation();
			
			for(ClanChallenge challenge : bpClan.getPendingChallenges())
			{
				if(!challenge.isWaiting())
					continue;
				
				Clan challengingClan = challenge.getChallengingClan();
				String challengingClanName = challengingClan != null ? challengingClan.getColoredName() : "?";
				String dayName = CWScheduler.getDayName(challenge.getDayOfWeek());
				int maxPlayers = challenge.getMaximumPlayers();
				pendingChallengesString.append(ctrans.getValue(challengingClanName, dayName, maxPlayers)).append(", ");
			}
			
			Translation mrtrans = MessageType.COMMAND_CLAN_INFO_FORMAT_MATCHRESULT.getTranslation();
			
			for(CWMatchResult result : bpClan.getMatchResults())
			{
				int[] points = result.getPoints();
				ChatColor color;
				
				if(result.hasWon())
					color = ChatColor.GREEN;
				else if(result.hasLost())
					color = ChatColor.RED;
				else
					color = ChatColor.WHITE;
				
				matchResultsString.append("\n  ").append(mrtrans.getValue(result.getOpponent(), color, points[0], points[1]));
			}
			
			sender.sendMessage(MessageType.COMMAND_CLAN_INFO_EXE_SUCCESS.getTranslation().getValue(clanNameColored, getOnlinePlayerName(leader), moderatorString, memberString, invitedString, rank, pendingChallengesString, matchResultsString, bpClan.getWins(), bpClan.getDraws(), bpClan.getLoses(), bpClan.getPoints()));
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_JOIN_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			if(curClan != null)
			{
				String clanName = curClan.getColoredName();
				player.sendMessage(MessageType.COMMAND_CLAN_JOIN_EXE_ALREADYJOINED.getTranslation().getValue(clanName));
				return true;
			}
			if(args.length <= 1)
				sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_JOIN_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_JOIN_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				String clanName = args[1];
				Clan bpClan = Clan.get(clanName);
				if(bpClan != null)
				{
					String clanNameColored = bpClan.getColoredName();
					if(bpClan.isInvited(playerName))
					{
						BPPlayer bpPlayer = BPPlayer.get(player);
						
						bpClan.join(bpPlayer);
					}
					else
						player.sendMessage(MessageType.COMMAND_CLAN_JOIN_EXE_NOTINVITED.getTranslation().getValue(clanNameColored));
				}
				else
					player.sendMessage(MessageType.COMMAND_CLAN_JOIN_EXE_NOTFOUND.getTranslation().getValue(clanName));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_LEAVE_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			if(curClan != null)
			{
				boolean isLeader = curClan.getLeaderName().equals(playerName);
				if(isLeader)
					curClan.breakup(player);
				else
					curClan.leave(player);
			}
			else
				player.sendMessage(MessageType.COMMAND_CLAN_LEAVE_EXE_NOTMEMBER.getTranslation().getValue());
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_CREATE_CMD.getTranslation().getValue()))
		{
			if(!player.hasPermission("Breakpoint.vip"))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_CREATE_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			if(curClan != null)
			{
				String clanName = curClan.getColoredName();
				player.sendMessage(MessageType.COMMAND_CLAN_CREATE_EXE_ALREADYJOINED.getTranslation().getValue(clanName));
				return true;
			}
			if(args.length <= 1)
			{
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_CREATE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_CREATE_DESC.getTranslation().getValue(), ChatColor.AQUA));
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_COLORHINT.getTranslation().getValue());
			}
			else
			{
				String nameColored = Clan.getColored(args[1]);
				BPPlayer bpPlayer = BPPlayer.get(player);
				Clan bpClan;
				
				try
				{
					bpClan = Clan.create(args[1], playerName);
				}
				catch(IllegalArgumentException e)
				{
					sender.sendMessage(e.getMessage());
					return false;
				}
				
				bpPlayer.setClan(bpClan);
				//plugin.clm.updatePlayerClan(player, bpClan);
				player.sendMessage(MessageType.COMMAND_CLAN_CREATE_EXE_SUCCESS.getTranslation().getValue(nameColored));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_RENAME_CMD.getTranslation().getValue()))
		{
			if(!player.hasPermission("Breakpoint.vip"))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			String clanNameColored = curClan.getColoredName();
			if(!curClan.isLeader(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_EXE_NOTLEADER.getTranslation().getValue(clanNameColored));
				return true;
			}
			if(args.length <= 1)
			{
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_RENAME_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_RENAME_DESC.getTranslation().getValue(), ChatColor.AQUA));
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_COLORHINT.getTranslation().getValue());
			}
			else
			{
				try
				{
					curClan.setColoredNameRaw(args[1]);
				}
				catch(IllegalArgumentException e)
				{
					player.sendMessage(e.getMessage());
					return false;
				}
				
				player.sendMessage(MessageType.COMMAND_CLAN_RENAME_EXE_SUCCESS.getTranslation().getValue(curClan.getColoredName()));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_INVITE_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			else if(!player.hasPermission("Breakpoint.vip") && !curClan.isModerator(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			
			String clanNameColored = curClan.getColoredName();
			if(!curClan.isAtLeastModerator(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_NOTMODERATOR.getTranslation().getValue(clanNameColored));
				return true;
			}
			if(args.length <= 1)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_INVITE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_INVITE_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				String targetName = args[1];
				if(targetName.equals(playerName))
				{
					player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_INVITEDYOURSELF.getTranslation().getValue());
					return true;
				}
				if(curClan.isAtLeastMember(targetName))
				{
					player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_ALREADYMEMBER.getTranslation().getValue(targetName));
					return true;
				}
				Player target = Bukkit.getPlayerExact(targetName);
				if(curClan.isInvited(targetName))
				{
					curClan.removeInvited(targetName);
					player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_SUCCESS_TAKENDOWN_SENDER.getTranslation().getValue(targetName));
					
					if(target != null)
						target.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_SUCCESS_TAKENDOWN_TARGET.getTranslation().getValue(clanNameColored));
				}
				else
				{
					String clanName = ChatColor.stripColor(clanNameColored);
					curClan.addInvited(targetName);
					player.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_SUCCESS_CREATE_SENDER.getTranslation().getValue(targetName));
					
					if(target != null)
						target.sendMessage(MessageType.COMMAND_CLAN_INVITE_EXE_SUCCESS_CREATE_TARGET.getTranslation().getValue(clanNameColored, clanName));
				}
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_KICK_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_KICK_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			else if(!player.hasPermission("Breakpoint.vip") && !curClan.isModerator(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_KICK_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			else if(!curClan.isAtLeastModerator(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_KICK_EXE_NOTMODERATOR.getTranslation().getValue());
				return true;
			}
			if(args.length <= 1)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_KICK_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_KICK_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				String targetName = args[1];
				if(curClan.isAtLeastMember(targetName) && !curClan.isLeader(targetName))
					curClan.kick(targetName);
				else
					player.sendMessage(MessageType.COMMAND_CLAN_KICK_EXE_TARGETNOTMEMBER.getTranslation().getValue(targetName));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_MODERATOR_CMD.getTranslation().getValue()))
		{
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			else if(!player.hasPermission("Breakpoint.vip") && !curClan.isModerator(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			else if(!curClan.isLeader(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_NOTLEADER.getTranslation().getValue());
				return true;
			}
			if(args.length <= 1)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_MODERATOR_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_MODERATOR_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				String targetName = args[1];
				
				if(curClan.isAtLeastMember(targetName) && !curClan.isLeader(targetName))
				{
					if(curClan.isMember(targetName))
					{
						curClan.removeMember(targetName);
						curClan.addModerator(targetName);
						
						Player target = Bukkit.getPlayerExact(targetName);
						
						if(target != null)
						{
							String clanNameColored = curClan.getColoredName();
							
							target.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_UPGRADED_TARGET.getTranslation().getValue(clanNameColored));
						}
						
						sender.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_UPGRADED_SENDER.getTranslation().getValue(targetName));
					}
					else
					{
						curClan.removeModerator(targetName);
						curClan.addMember(targetName);
						
						Player target = Bukkit.getPlayerExact(targetName);
						
						if(target != null)
						{
							String clanNameColored = curClan.getColoredName();
							
							target.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_DOWNGRADED_TARGET.getTranslation().getValue(clanNameColored));
						}
						
						sender.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_DOWNGRADED_SENDER.getTranslation().getValue(targetName));
					}
				}
				else
					player.sendMessage(MessageType.COMMAND_CLAN_MODERATOR_EXE_TARGETNOTMEMBER.getTranslation().getValue(targetName));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_CHALLENGE_CMD.getTranslation().getValue()))
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			Game rawGame = GameManager.getGame(config.getCWChallengeGame());
			
			if(rawGame == null || !(rawGame instanceof CWGame))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_NOGAME.getTranslation().getValue());
				return true;
			}
			
			CWGame game = (CWGame) rawGame;
			
			if(!player.hasPermission("Breakpoint.vip"))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			
			if(!curClan.isLeader(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_NOTLEADER.getTranslation().getValue());
				return true;
			}
			
			if(args.length <= 3)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_CHALLENGE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_CHALLENGE_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				Clan bpClan = Clan.get(args[1]);
				
				if(bpClan == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_NOTFOUND.getTranslation().getValue(args[1]));
					return true;
				}
				else if(bpClan.equals(curClan))
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_CHALLENGEDITSELF.getTranslation().getValue());
					return false;
				}
				
				Integer dayOfWeek = CWScheduler.getDayIndex(args[2]);
				
				if(dayOfWeek == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_UNKNOWNDAY.getTranslation().getValue(args[2]));
					return true;
				}
				
				int dayOfYear = CWScheduler.getNextDayOfYear(dayOfWeek);
				ClanChallenge day = game.getScheduler().getDay(dayOfYear);
				
				if(day != null)
				{
					String challengingName = day.getChallengingClan() != null ? day.getChallengingClan().getColoredName() : "?";
					String challengedName = day.getChallengedClan() != null ? day.getChallengedClan().getColoredName() : "?";
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_DAYTAKEN.getTranslation().getValue(challengingName, challengedName));
					return true;
				}
				
				int maxPlayers;
				
				try
				{
					maxPlayers = Integer.parseInt(args[3]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_INCORRECTMAXPLAYERS.getTranslation().getValue());
					return false;
				}
				
				if(maxPlayers < 1)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_TOOFEWMAXPLAYERS.getTranslation().getValue());
					return false;
				}
				
				if(curClan.getPlayerNames().size() < maxPlayers)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_MAXPLAYERSFEWPLAYERS_OWN.getTranslation().getValue());
					return false;
				}
				
				if(bpClan.getPlayerNames().size() < maxPlayers)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_MAXPLAYERSFEWPLAYERS_THEIR.getTranslation().getValue());
					return false;
				}
				
				ClanChallenge theirChallenge = bpClan.getPendingChallengeFrom(curClan);
				
				if(theirChallenge != null)
				{
					String curDay;
					
					if(theirChallenge.isToday())
						curDay = MessageType.CALENDAR_TODAY.getTranslation().getValue();
					else
						curDay = CWScheduler.getDayName(theirChallenge.getDayOfWeek());
					
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_ALREADYCHALLENGED.getTranslation().getValue(curDay));
					return true;
				}
				
				ClanChallenge ourChallenge = curClan.getPendingChallengeFrom(bpClan);
				
				if(ourChallenge != null)
				{
					String curDay;
					
					if(ourChallenge.isToday())
						curDay = MessageType.CALENDAR_TODAY.getTranslation().getValue();
					else
						curDay = CWScheduler.getDayName(ourChallenge.getDayOfWeek());
					
					sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_CHALLENGEPENDING.getTranslation().getValue(curDay));
					return true;
				}
				
				String dayName = CWScheduler.getDayName(dayOfWeek);
				Player leader = bpClan.getLeader();
				
				bpClan.getPendingChallenges().add(new ClanChallenge(dayOfYear, curClan, bpClan, maxPlayers));
				sender.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_SUCCESS.getTranslation().getValue(bpClan.getColoredName(), dayName, maxPlayers));
				
				if(leader != null)
					leader.sendMessage(MessageType.COMMAND_CLAN_CHALLENGE_EXE_SUCCESSOTHER.getTranslation().getValue(curClan.getColoredName(), dayName, maxPlayers));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_ACCEPT_CMD.getTranslation().getValue()))
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			Game rawGame = GameManager.getGame(config.getCWChallengeGame());
			
			if(rawGame == null || !(rawGame instanceof CWGame))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_NOGAME.getTranslation().getValue());
				return true;
			}
			
			CWGame game = (CWGame) rawGame;
			
			if(!player.hasPermission("Breakpoint.vip"))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			
			if(!curClan.isLeader(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_NOTLEADER.getTranslation().getValue());
				return true;
			}
			
			if(args.length <= 1)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_ACCEPT_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_ACCEPT_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				Clan bpClan = Clan.get(args[1]);
				
				if(bpClan == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_NOTFOUND.getTranslation().getValue(args[1]));
					return true;
				}
				
				ClanChallenge ourChallenge = curClan.getPendingChallengeFrom(bpClan);
				
				if(ourChallenge == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_NOCHALLENGE.getTranslation().getValue(bpClan.getColoredName()));
					return true;
				}
				
				int day = ourChallenge.getDayOfWeek();
				String dayName = CWScheduler.getDayName(day);
				Player leader = bpClan.getLeader();
				
				curClan.getPendingChallenges().remove(ourChallenge);
				game.getScheduler().addDay(ourChallenge);
				sender.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_SUCCESS.getTranslation().getValue(bpClan.getColoredName(), dayName));
				
				if(leader != null)
					leader.sendMessage(MessageType.COMMAND_CLAN_ACCEPT_EXE_SUCCESSOTHER.getTranslation().getValue(curClan.getColoredName(), dayName));
			}
		}
		else if(args[0].equalsIgnoreCase(MessageType.COMMAND_CLAN_REJECT_CMD.getTranslation().getValue()))
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			Game rawGame = GameManager.getGame(config.getCWChallengeGame());
			
			if(rawGame == null || !(rawGame instanceof CWGame))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_NOGAME.getTranslation().getValue());
				return true;
			}
			
			if(!player.hasPermission("Breakpoint.vip"))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_VIPSONLY.getTranslation().getValue());
				return true;
			}
			
			String playerName = player.getName();
			Clan curClan = Clan.getByPlayer(playerName);
			
			if(curClan == null)
			{
				player.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_NOTMEMBER.getTranslation().getValue());
				return true;
			}
			
			if(!curClan.isLeader(playerName))
			{
				player.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_NOTLEADER.getTranslation().getValue());
				return true;
			}
			
			if(args.length <= 1)
				player.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_REJECT_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_REJECT_DESC.getTranslation().getValue(), ChatColor.AQUA));
			else
			{
				Clan bpClan = Clan.get(args[1]);
				
				if(bpClan == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_NOTFOUND.getTranslation().getValue(args[1]));
					return true;
				}
				
				ClanChallenge ourChallenge = curClan.getPendingChallengeFrom(bpClan);
				
				if(ourChallenge == null)
				{
					sender.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_NOCHALLENGE.getTranslation().getValue(bpClan.getColoredName()));
					return true;
				}
				
				Player leader = bpClan.getLeader();
				
				curClan.getPendingChallenges().remove(ourChallenge);
				sender.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_SUCCESS.getTranslation().getValue(bpClan.getColoredName()));
				
				if(leader != null)
					leader.sendMessage(MessageType.COMMAND_CLAN_REJECT_EXE_SUCCESSOTHER.getTranslation().getValue(curClan.getColoredName()));
			}
		}
		else
			listClanCommands(sender, ChatColor.RED);
		
		return true;
	}
	
	private static String getCommandInfo(String command, String info, ChatColor color)
	{
		return color + "/" + command + ChatColor.GRAY + " - " + info;
	}
	
	private static void listClanCommands(CommandSender sender, ChatColor color)
	{
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_INFO_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_INFO_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_JOIN_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_JOIN_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_LEAVE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_LEAVE_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_CREATE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_CREATE_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_RENAME_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_RENAME_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_MODERATOR_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_MODERATOR_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_INVITE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_INVITE_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_KICK_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_KICK_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_CHALLENGE_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_CHALLENGE_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_ACCEPT_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_ACCEPT_DESC.getTranslation().getValue(), color));
		sender.sendMessage(getCommandInfo(MessageType.COMMAND_CLAN_REJECT_PATH.getTranslation().getValue(), MessageType.COMMAND_CLAN_REJECT_DESC.getTranslation().getValue(), color));
	}

	private static String getOnlinePlayerName(String playerName)
	{
		Player player = Bukkit.getPlayerExact(playerName);
		
		if(player != null)
			if(player.isOnline())
				playerName = ChatColor.ITALIC + playerName + ChatColor.RESET;
		
		return playerName;
	}
}
