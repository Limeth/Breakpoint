package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWGame;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWScheduler;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;

public class CWCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Configuration config = Breakpoint.getBreakpointConfig();
		Game rawGame = GameManager.getGame(config.getCWChallengeGame());
		
		if(rawGame == null || !(rawGame instanceof CWGame))
		{
			sender.sendMessage(MessageType.COMMAND_CW_EXE_NOGAME.getTranslation().getValue());
			return false;
		}
		
		CWGame game = (CWGame) rawGame;
		CWScheduler sc = game.getScheduler();
		StringBuilder sb = new StringBuilder();
		Calendar calendar = Calendar.getInstance();
		int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		ClanChallenge today = sc.getDay(dayOfYear);
		
		sb.append(ChatColor.YELLOW.toString()).append(ChatColor.BOLD.toString())
		.append(MessageType.CALENDAR_TODAY.getTranslation().getValue()).append(": ")
		.append(today == null ? MessageType.COMMAND_CW_DAYNOTTAKEN.getTranslation().getValue() : MessageType.COMMAND_CW_FORMAT_DAY.getTranslation().getValue(
					today.getChallengingClan() != null ? today.getChallengingClan().getColoredName() : "?",
					today.getChallengedClan() != null ? today.getChallengedClan().getColoredName() : "?",
					today.getMaximumPlayers()
				));
		
		for(int i = 1; i <= 7; i++)
		{
			ClanChallenge day = sc.getDay(++dayOfYear);
			
			if(++dayOfWeek > 7)
				dayOfWeek -= 7;
			
			sb.append('\n').append(ChatColor.YELLOW.toString())
			.append(CWScheduler.getDayName(dayOfWeek)).append(": ")
			.append(day == null ? MessageType.COMMAND_CW_DAYNOTTAKEN.getTranslation().getValue() : MessageType.COMMAND_CW_FORMAT_DAY.getTranslation().getValue(
						day.getChallengingClan() != null ? day.getChallengingClan().getColoredName() : "?",
						day.getChallengedClan() != null ? day.getChallengedClan().getColoredName() : "?",
						day.getMaximumPlayers()
					));
		}
		
		sender.sendMessage(MessageType.COMMAND_CW_FORMAT_MESSAGE.getTranslation().getValue(sb.toString()));
		
		return false;
	}
}
