package cz.projectsurvive.me.limeth.breakpoint.managers.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import me.limeth.storageAPI.StorageType;
import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.fijistudios.jordan.FruitSQL;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.Configuration;
import cz.projectsurvive.me.limeth.breakpoint.RandomShop;
import cz.projectsurvive.me.limeth.breakpoint.achievements.AchievementType;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPEquipment;
import cz.projectsurvive.me.limeth.breakpoint.game.CharacterType;
import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.GameType;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWGame;
import cz.projectsurvive.me.limeth.breakpoint.game.cw.CWScheduler;
import cz.projectsurvive.me.limeth.breakpoint.language.Language;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.managers.FileManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.GameManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.InventoryMenuManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.ShopManager;
import cz.projectsurvive.me.limeth.breakpoint.managers.StatisticsManager;
import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.Clan;
import cz.projectsurvive.me.limeth.breakpoint.players.clans.ClanChallenge;
import cz.projectsurvive.me.limeth.breakpoint.statistics.TotalPlayerStatistics;

public class BPCommandExecutor implements CommandExecutor
{
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(!sender.hasPermission("Breakpoint.adminCommands"))
		{
			sender.sendMessage(MessageType.OTHER_NOPERMISSION.getTranslation().getValue());
			return true;
		}
		
		if(args.length <= 0)
		{
			sender.sendMessage("/bp lobbyLoc");
			sender.sendMessage("/bp shopLoc");
			sender.sendMessage("/bp vipInfoLoc");
			sender.sendMessage("/bp moneyInfoLoc");
			sender.sendMessage("/bp setSignLine [i] [text]");
			sender.sendMessage("/bp getArmour [#color]");
			sender.sendMessage("/bp money [set/add] [Player] [Amount]");
			sender.sendMessage("/bp buildShop [side] [#RGB] [cost,cost...] [time,time...] [name]");
			sender.sendMessage("/bp updateStats");
			sender.sendMessage("/bp stats");
			sender.sendMessage("/bp sound");
			sender.sendMessage("/bp setWikiBook");
			sender.sendMessage("/bp saveWikiBook");
			sender.sendMessage("/bp giveAchievement");
			sender.sendMessage("/bp parseEquipment");
			sender.sendMessage("/bp setArmorByHand [armorSlotId]");
			sender.sendMessage("/bp reloadLanguage");
			sender.sendMessage("/bp setRandomShopLoc [Dir]");
			sender.sendMessage("/bp deleteDefault");
			sender.sendMessage("/bp exportPlayers [StorageType From] [StorageType To]");
			sender.sendMessage("/bp lobbyMessages");
			sender.sendMessage("/bp setMatch +days challenging challenged");
			sender.sendMessage("/bp removeMatch +days");
			sender.sendMessage("Games:");
			sender.sendMessage("/bp game create [GameType] [Name]");
			sender.sendMessage("/bp game remove [Game]");
			sender.sendMessage("/bp game signLoc [Name]");
			sender.sendMessage("/bp game [Game] ...");
		}
		else if(args[0].equalsIgnoreCase("lobbyLoc"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for players!");
				return true;
			}
			Player player = (Player) sender;
			Configuration config = Breakpoint.getBreakpointConfig();
			
			config.setLobbyLocation(player.getLocation());
			sender.sendMessage(ChatColor.GREEN + "Lobby location successfully set.");
		}
		else if(args[0].equalsIgnoreCase("shopLoc"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for players!");
				return true;
			}
			Player player = (Player) sender;
			Configuration config = Breakpoint.getBreakpointConfig();
			
			config.setShopLocation(player.getLocation());
			sender.sendMessage(ChatColor.GREEN + "Shop location successfully set.");
		}
		else if(args[0].equalsIgnoreCase("VIPInfoLoc"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for players!");
				return true;
			}
			Player player = (Player) sender;
			Configuration config = Breakpoint.getBreakpointConfig();
			
			config.setVipInfoLocation(player.getLocation());
			sender.sendMessage(ChatColor.GREEN + "Character selection location successfully set.");
		}
		else if(args[0].equalsIgnoreCase("MoneyInfoLoc"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for players!");
				return true;
			}
			Player player = (Player) sender;
			Configuration config = Breakpoint.getBreakpointConfig();
			
			config.setMoneyInfoLocation(player.getLocation());
			sender.sendMessage(ChatColor.GREEN + "Character selection location successfully set.");
		}
		else if(args[0].equalsIgnoreCase("lobbyMessages"))
		{
			if(args.length <= 1)
			{
				sender.sendMessage("/bp lobbyMessages list");
				sender.sendMessage("/bp lobbyMessages add [Message]");
				sender.sendMessage("/bp lobbyMessages remove [Index]");
			}
			else if(args[1].equalsIgnoreCase("list"))
			{
				Configuration config = Breakpoint.getBreakpointConfig();
				List<String> messages = config.getLobbyMessages();
				
				for(int i = 0; i < messages.size(); i++)
					sender.sendMessage("[" + i + "] " + ChatColor.YELLOW + messages.get(i));
			}
			else if(args[1].equalsIgnoreCase("add"))
			{
				String message = args[2];
				
				for(int i = 3; i < args.length; i++)
					message += " " + args[i];
				
				message = ChatColor.translateAlternateColorCodes('&', message);
				Configuration config = Breakpoint.getBreakpointConfig();
				
				config.getLobbyMessages().add(message);
				sender.sendMessage("added");
			}
			else if(args[1].equalsIgnoreCase("remove"))
			{
				int index;
				
				try
				{
					index = Integer.parseInt(args[2]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage("Incorrect index");
					return true;
				}
				
				Configuration config = Breakpoint.getBreakpointConfig();
				List<String> messages = config.getLobbyMessages();
				
				try
				{
					messages.remove(index);
				}
				catch(Exception e)
				{
					sender.sendMessage("Incorrect index");
					return true;
				}
				
				sender.sendMessage("removed");
			}
		}
		else if(args[0].equalsIgnoreCase("game"))
		{
			if(args.length <= 1)
			{
				
			}
			else if(args[1].equalsIgnoreCase("list"))
			{
				StringBuilder sb = new StringBuilder();
				boolean first = true;
				
				for(Game game : GameManager.getGames())
				{
					if(!first)
						sb.append(ChatColor.GRAY + ", ");
					else
						first = false;
					
					String name = game.getName();
					boolean isPlayable = game.isPlayable(true);
					boolean isActive = game.isActive();
					ChatColor color = isActive ? ChatColor.GREEN : (isPlayable ? ChatColor.YELLOW : ChatColor.RED);
					
					sb.append(color + name);
				}
				
				sender.sendMessage(sb.toString());
			}
			else if(args[1].equalsIgnoreCase("create"))
			{
				if(args.length <= 3)
					return true;
				
				Player player = (Player) sender;
				Location loc = player.getLocation();
				Game foundGame = GameManager.getGame(args[3]);
				
				if(foundGame != null)
				{
					sender.sendMessage(ChatColor.RED + "A game with name '" + args[3] + "' already exists!");
					return true;
				}
				
				GameType type;
				
				try
				{
					type = GameType.valueOf(args[2].toUpperCase());
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "GameType not found!");
					return true;
				}
				
				Game game = type.newGame(args[3], loc);
				GameManager.addGame(game);
				
				sender.sendMessage(ChatColor.GREEN + "Game '" + args[3] + "' successfully created.");
			}
			else if(args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete"))
			{
				if(args.length <= 2)
					return true;
				
				String name = args[2];
				
				for(int i = 3; i < args.length; i++)
					name += " " + args[i];
				
				Game game = GameManager.getGame(name);
				
				if(game == null)
					return true;
				
				GameManager.removeGame(game);
				
				sender.sendMessage(ChatColor.GREEN + "Game '" + name + "' successfully removed.");
			}
			else if(args[1].equalsIgnoreCase("signLoc"))
			{
				if(args.length <= 2)
					return true;
				
				Player player = (Player) sender;
				Location loc = player.getLocation().getBlock().getLocation();
				String name = args[2];
				
				for(int i = 3; i < args.length; i++)
					name += " " + args[i];
				
				Game game = GameManager.getGame(name);
				
				if(game == null)
					return true;
				
				game.setSignLocation(loc);
				
				sender.sendMessage(ChatColor.GREEN + "Sign location set.");
			}
			else
			{
				Game game = GameManager.getGame(args[1]);
				
				if(game == null)
				{
					sender.sendMessage("Game / Command not found");
					return true;
				}
				
				game.onCommand(sender, Arrays.copyOfRange(args, 2, args.length));
			}
		}
		else if(args[0].equalsIgnoreCase("getArmour"))
		{
			if(args.length > 1)
			{
				int colorRgb;
				try
				{
					colorRgb = Integer.decode("0x" + args[1]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Not a number");
					return true;
				}
				ItemStack[] armour = new ItemStack[] { new ItemStack(Material.LEATHER_HELMET), new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_BOOTS) };
				for(ItemStack piece : armour)
				{
					LeatherArmorMeta im = (LeatherArmorMeta) piece.getItemMeta();
					Color color = Color.fromRGB(colorRgb);
					im.setColor(color);
					piece.setItemMeta(im);
					((Player) sender).getInventory().addItem(piece);
				}
			}
		}
		else if(args[0].equalsIgnoreCase("setSignLine"))
		{
			if(!(sender instanceof Player))
			{
				sender.sendMessage(ChatColor.RED + "Only for players!");
				return true;
			}
			if(args.length > 2)
			{
				int lineNo;
				try
				{
					lineNo = Integer.parseInt(args[1]);
				}
				catch(NumberFormatException e)
				{
					sender.sendMessage(ChatColor.RED + "Not a number");
					return true;
				}
				if(lineNo >= 0 && lineNo <= 3)
				{
					Player player = (Player) sender;
					Location loc = player.getLocation();
					Block block = loc.getBlock();
					if(block.getState() instanceof Sign)
					{
						Sign sign = (Sign) block.getState();
						String text = "";
						int start = 2;
						for(int i = 0; i < args.length - start; i++)
							text += args[i + start] + " ";
						text = text.substring(0, text.length() - 1);
						sign.setLine(lineNo, text);
						sign.update(true);
					}
				}
			}
		}
		else if(args[0].equalsIgnoreCase("money"))
		{
			if(args.length > 3)
			{
				Player target = Bukkit.getPlayer(args[2]);
				if(target != null)
				{
					int amount;
					
					try
					{
						amount = Integer.parseInt(args[3]);
					}
					catch(NumberFormatException e)
					{
						sender.sendMessage(ChatColor.RED + "Not a number");
						return true;
					}
					
					BPPlayer bpTarget = BPPlayer.get(target);
					
					if(args[1].equalsIgnoreCase("set"))
					{
						bpTarget.setMoney(amount);
						sender.sendMessage(ChatColor.GREEN + "The money of player '" + target.getName() + "' has been succesfully set to " + amount + "!");
					}
					else if(args[1].equalsIgnoreCase("add"))
					{
						int money = bpTarget.addMoney(amount, true, false);
						sender.sendMessage(ChatColor.GREEN + "The money of player '" + target.getName() + "' has been succesfully set to " + money + " by increasing it by " + amount + "!");
					}
					else
						sender.sendMessage(ChatColor.RED + "Use set/add!");
				}
				else
					sender.sendMessage(ChatColor.RED + "Unknown player '" + args[2] + "'!");
			}
		}
		else if(args[0].equalsIgnoreCase("buildShop"))
		{
			if(!(sender instanceof Player))
				return true;
			Player player = (Player) sender;
			if(args.length >= 6)
			{
				Location loc;
				int facing;
				String color;
				int[] cost;
				int[] time;
				String name;
				try
				{
					loc = player.getLocation();
					facing = Integer.parseInt(args[1]);
					color = args[2];
					name = "";
					for(int i = 5; i < args.length; i++)
						name += args[i] + " ";
					name = name.substring(0, name.length() - 1);
					String[] rawCost = args[3].split(",");
					String[] rawTime = args[4].split(",");
					if(rawCost.length != rawTime.length)
					{
						player.sendMessage(ChatColor.RED + "Pocet casu se musi rovnat poctu cen.");
						return true;
					}
					cost = new int[rawCost.length];
					time = new int[rawTime.length];
					for(int i = 0; i < cost.length; i++)
					{
						cost[i] = Integer.parseInt(rawCost[i]);
						time[i] = Integer.parseInt(rawTime[i]);
					}
				}
				catch(Exception e)
				{
					player.sendMessage(ChatColor.RED + "Nespravne argumenty!");
					return true;
				}
				ShopManager.buildArmorShop(loc, facing, color, cost, time, name);
				player.sendMessage(ChatColor.GREEN + "Obchod postaven!");
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Nespravne argumenty!");
				player.sendMessage(ChatColor.GRAY + "/ps buildShop [side] [#RGB] [cost,cost...] [time,time...] [name]");
			}
		}
		else if(args[0].equalsIgnoreCase("updateStatistics") || args[0].equalsIgnoreCase("updateStats"))
		{
			if(StatisticsManager.isUpdating())
			{
				sender.sendMessage(ChatColor.RED + "Statistics are already being updated.");
				return true;
			}
			
			StatisticsManager.asyncUpdate();
			sender.sendMessage(ChatColor.GREEN + "Statistics update started!");
		}
		else if(args[0].equalsIgnoreCase("stats"))
		{
			TotalPlayerStatistics stats = StatisticsManager.getTotalStats();
			
			sender.sendMessage("Players: " + stats.getPlayerAmount());
			
			sender.sendMessage("totalKills: " + stats.getKills());
			sender.sendMessage("averageKills: " + stats.getAverageKills());
			
			sender.sendMessage("totalDeaths: " + stats.getDeaths());
			sender.sendMessage("averageDeaths: " + stats.getAverageDeaths());
			
			sender.sendMessage("totalFlagTakes: " + stats.getFlagTakes());
			sender.sendMessage("averageFlagTakes: " + stats.getAverageFlagTakes());
			
			sender.sendMessage("totalFlagCaptures: " + stats.getFlagCaptures());
			sender.sendMessage("averageFlagCaptures: " + stats.getAverageFlagCaptures());
			
			sender.sendMessage("totalBought: " + stats.getBought());
			sender.sendMessage("averageBought: " + stats.getAverageBought());
			
			sender.sendMessage("totalMoney: " + stats.getMoney());
			sender.sendMessage("averageMoney: " + stats.getAverageMoney());
			
			for(CharacterType ct : CharacterType.values())
			{
				sender.sendMessage("totalKills " + ct.getProperName() + ": " + stats.getKills(ct));
				sender.sendMessage("averageKills " + ct.getProperName() + ": " + stats.getAverageKills(ct));
			}
		}
		else if(args[0].equalsIgnoreCase("sound"))
		{
			if(args.length <= 1)
			{
				String s = "";
				for(Sound sound : Sound.values())
					s += sound.name() + ", ";
				s = s.substring(0, s.length() - 2);
				sender.sendMessage(ChatColor.GRAY + s);
			}
			else
			{
				if(!(sender instanceof Player))
					return true;
				Player player = (Player) sender;
				Location loc = player.getLocation();
				World world = loc.getWorld();
				try
				{
					Sound sound = Sound.valueOf(args[1].toUpperCase());
					world.playSound(loc, sound, 1F, 1F);
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Sound not found.");
				}
			}
		}
		else if(args[0].equalsIgnoreCase("setWikiBook"))
		{
			if(!(sender instanceof Player))
				return true;
			Player player = (Player) sender;
			ItemStack inHand = player.getItemInHand();
			Material mat = inHand.getType();
			if(!(mat == Material.WRITTEN_BOOK || mat == Material.BOOK_AND_QUILL))
				return true;
			BookMeta wikiIM = (BookMeta) InventoryMenuManager.wikiBook.getItemMeta();
			BookMeta curIM = (BookMeta) inHand.getItemMeta();
			wikiIM.setPages(curIM.getPages());
			InventoryMenuManager.wikiBook.setItemMeta(wikiIM);
			sender.sendMessage("Book set!");
		}
		else if(args[0].equalsIgnoreCase("saveWikiBook"))
		{
			FileManager.saveWikiBook();
			sender.sendMessage("Book saved!");
		}
	/*	else if(args[0].equalsIgnoreCase("names"))
		{
			if(!(sender instanceof Player))
				return true;
			Player player = (Player) sender;
			plugin.tapim.toggleNames(player);
		}*/
		else if(args[0].equalsIgnoreCase("giveAchievement"))
		{
			if(args.length < 3)
			{
				sender.sendMessage(ChatColor.RED + "/bp giveAchievement [Player] [Achievement]");
				String list = "";
				boolean first = true;
				for(AchievementType at : AchievementType.values())
				{
					if(!first)
						list += ", ";
					list += at.name();
					first = false;
				}
				sender.sendMessage(ChatColor.GRAY + list);
			}
			else
			{
				Player player = Bukkit.getPlayer(args[1]);
				BPPlayer bpPlayer = BPPlayer.get(player);
				AchievementType at;
				try
				{
					at = AchievementType.valueOf(args[2].toUpperCase());
				}
				catch(Exception e)
				{
					sender.sendMessage(ChatColor.RED + "Neznámý achievement '" + args[2] + "'!");
					return true;
				}
				if(!bpPlayer.hasAchievement(at))
				{
					bpPlayer.giveAchievement(at);
					sender.sendMessage(ChatColor.GREEN + "Gave " + player.getName() + " achievement '" + at.name() + "'.");
				}
				else
					sender.sendMessage(ChatColor.RED + "Player " + player.getName() + " already has achievement '" + at.name() + "'.");
			}
		}
		else if(args[0].equalsIgnoreCase("parseEquipment"))
		{
			if(!(sender instanceof Player))
				return true;
			Player player = (Player) sender;
			BPEquipment equipment = BPEquipment.deserialize(args[1].split(","));
			
			if(equipment == null)
			{
				sender.sendMessage(ChatColor.RED + "Wrong format!");
				return true;
			}
			
			ItemStack is = equipment.getItemStack();
			Location loc = player.getLocation();
			World world = loc.getWorld();
			world.dropItem(loc, is);
			sender.sendMessage(ChatColor.GREEN + "Spawned!");
		}
		else if(args[0].equalsIgnoreCase("setArmorByHand"))
			try
			{
				Player player = (Player) sender;
				PlayerInventory inv = player.getInventory();
				ItemStack is = player.getItemInHand();
				ItemStack[] armor = inv.getArmorContents();
				armor[Integer.parseInt(args[1])] = is;
				inv.setArmorContents(armor);
				sender.sendMessage(ChatColor.GREEN + "Done.");
			}
			catch(Exception e)
			{
				sender.sendMessage(ChatColor.RED + "Wrong format!");
			}
		else if(args[0].equalsIgnoreCase("reloadLanguage"))
		{
			Configuration config = Breakpoint.getBreakpointConfig();
			Language.loadLanguage(Breakpoint.PLUGIN_NAME, config.getLanguageFileName());
			sender.sendMessage(ChatColor.GREEN + "Successfully loaded!");
		}
		else if(args[0].equalsIgnoreCase("setRandomShopLoc"))
		{
			Location loc = ((Player) sender).getLocation();
			int dir = Integer.parseInt(args[1]);
			RandomShop rs = new RandomShop(loc, dir);
			
			Breakpoint.getBreakpointConfig().setRandomShop(rs);
			rs.build();
		}
		else if(args[0].equalsIgnoreCase("setMatch"))
		{
			if(args.length <= 4)
				return false;
			
			int add = Integer.parseInt(args[1]);
			int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + add;
			Clan challenging = Clan.get(args[2]);
			Clan challenged = Clan.get(args[3]);
			int maxPlayers = Integer.parseInt(args[4]);
			Configuration config = Breakpoint.getBreakpointConfig();
			CWGame game = (CWGame) GameManager.getGame(config.getCWChallengeGame());
			CWScheduler sc = game.getScheduler();
			
			ClanChallenge cur = sc.getDay(day);
			sc.getDays().remove(cur);
			sc.addDay(new ClanChallenge(day, challenging, challenged, maxPlayers));
			game.setDay();
			
			sender.sendMessage("Done");
		}
		else if(args[0].equalsIgnoreCase("removeMatch"))
		{
			if(args.length <= 1)
				return false;
			
			int add = Integer.parseInt(args[1]);
			int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + add;
			Configuration config = Breakpoint.getBreakpointConfig();
			CWGame game = (CWGame) GameManager.getGame(config.getCWChallengeGame());
			CWScheduler sc = game.getScheduler();
			
			ClanChallenge cur = sc.getDay(day);
			sc.getDays().remove(cur);
			game.setDay();
			
			sender.sendMessage("Done");
		}
		else if(args[0].equalsIgnoreCase("deleteDefault"))
		{
			File folder = BPPlayer.getFolder();
			File[] files = folder.listFiles();
			
			for(File file : files)
			{
				if(!file.isFile())
					continue;
				
				String name = file.getName();
				
				if(!name.endsWith(".yml"))
					continue;
				
				name = name.substring(0, name.length() - 4);
				
				try
				{
					BPPlayer bpPlayer = BPPlayer.load(name);
					
					if(bpPlayer.hasDefaultData())
						bpPlayer.deleteFile();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			sender.sendMessage("Player files with default data successfully deleted.");
		}
		else if(args[0].equalsIgnoreCase("exportPlayers"))
		{
			if(args.length <= 2)
			{
				sender.sendMessage("/bp exportPlayers [StorageType From] [StorageType To]");
				return true;
			}
			
			try
			{
				final StorageType from = StorageType.valueOf(args[1]), to = StorageType.valueOf(args[2]);
				final CommandSender finalSender = sender;
				final boolean useMySQL = from == StorageType.MYSQL || to == StorageType.MYSQL;
				final FruitSQL mySQL = !useMySQL ? null : Breakpoint.hasMySQL() ? Breakpoint.getMySQL() : Breakpoint.getBreakpointConfig().connectToMySQL();
				final boolean disconnect = useMySQL && !Breakpoint.hasMySQL();
				
				new Thread(new Runnable() {
	
					@Override
					public void run()
					{
						try
						{
							if(useMySQL)
								BPPlayer.updateTable(mySQL);
							
							File folder = BPPlayer.getFolder();
							File[] files = folder.listFiles();
							
							for(File file : files)
								try
								{
									String fileName = FilenameUtils.removeExtension(file.getName());
									BPPlayer bpPlayer = BPPlayer.load(fileName, from, mySQL);
									
									bpPlayer.save(to, mySQL);
								}
								catch(Exception e)
								{
									finalSender.sendMessage("Error when exporting player file '" + file.getName() + "': " + e.getMessage());
									e.printStackTrace();
								}
							
							if(disconnect)
								mySQL.closeConnection();
							
							finalSender.sendMessage(ChatColor.GREEN + "Done.");
						}
						catch(Exception e)
						{
							finalSender.sendMessage("Error when exporting players: " + e.getMessage());
							e.printStackTrace();
						}
					}
					
				}, "exportPlayersToMySQL").start();
			}
			catch(Exception e)
			{
				sender.sendMessage(ChatColor.RED + "Error: " + e.getMessage());
			}
		}
		
		return true;
	}
}
