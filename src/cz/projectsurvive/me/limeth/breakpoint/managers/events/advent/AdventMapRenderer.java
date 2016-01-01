package cz.projectsurvive.me.limeth.breakpoint.managers.events.advent;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;
import cz.projectsurvive.me.limeth.breakpoint.equipment.BPBlock;
import cz.projectsurvive.me.limeth.breakpoint.language.MessageType;
import cz.projectsurvive.me.limeth.breakpoint.maps.BPMapPalette;
import cz.projectsurvive.me.limeth.breakpoint.maps.BPMapRenderer;

public class AdventMapRenderer extends BPMapRenderer
{
	public static final byte[] COLOR_BACKGROUND = new byte[] {
		BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 0),
		BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 1),
		BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 2),
		BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 1),
		BPMapPalette.getColor(BPMapPalette.DARK_GRAY, 0)
		};
	public static final byte COLOR_HEADER = BPMapPalette.getColor(BPMapPalette.RED, 2);
	public static final byte COLOR_HEADER_BACKGROUND = BPMapPalette.getColor(BPMapPalette.RED, 1);
	public static final byte COLOR_DESCRIPTION = BPMapPalette.getColor(BPMapPalette.RED, 1);
	public static final byte COLOR_DESCRIPTION_BACKGROUND = BPMapPalette.getColor(BPMapPalette.RED, 0);
	public static final byte COLOR_TEXT = BPMapPalette.getColor(BPMapPalette.RED, 2);
	public static final byte COLOR_TEXT_BACKGROUND = BPMapPalette.getColor(BPMapPalette.WHITE, 2);
	public static final byte GLOW_COLOR = BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 2);
	public static final byte GLOW_COLOR_EARNED = BPMapPalette.getColor(BPMapPalette.LIGHT_GREEN, 0);
	public static final byte HIDDEN_COLOR = BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 0);
	
	private final Byte[][][] numbers;
	private final Byte[][][] icons;
	private final int iconWidth = 6, iconHeight, iconStartX = 10, iconStartY, dayOfMonth;
	private final String header, description;
	private final AdventManager advm;
	
	public AdventMapRenderer(AdventManager advm, int dayOfMonth)
	{
		this.advm = advm;
		iconHeight = (int) Math.ceil((double) AdventManager.LAST_DAY / (double) iconWidth);
		iconStartY = MAP_SIZE - (iconHeight * 18 - 1) - 10;
		ArrayList<AdventGift> gifts = advm.getGifts();
		icons = loadIcons(gifts);
		numbers = loadNumbers(gifts.size());
		header = MessageType.EVENT_ADVENT_MAP_HEADER.getTranslation().getValue();
		description = MessageType.EVENT_ADVENT_MAP_DESCRIPTION.getTranslation().getValue();
		this.dayOfMonth = dayOfMonth;
	}

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		String playerName = player.getName();
		boolean earned = advm.getGift().hasEarned(playerName);
		
		drawRectangleFade(canvas, 0, 0, MAP_SIZE, MAP_SIZE, COLOR_BACKGROUND, 0);
		drawIcons(canvas);
		drawNumbers(canvas);
		drawHeader(canvas);
		drawDescription(canvas);
		drawGlow(canvas, earned);
	}
	
	public void drawGlow(MapCanvas canvas, boolean hasEarned)
	{
		int i = dayOfMonth - 1;
		int iconX = iconStartX + (i % 6) * 18;
		int iconY = iconStartY + (i / 6) * 18;
		byte color = !hasEarned ? GLOW_COLOR : GLOW_COLOR_EARNED;
		
		for(int x = 0; x < 2; x++)
			for(int y = 0; y < 18; y++)
			{
				int xx = iconX + x * 17 - 1;
				int yy = iconY + y - 1;
				
				canvas.setPixel(xx, yy, color);
			}
		
		for(int y = 0; y < 2; y++)
			for(int x = 0; x < 16; x++)
			{
				int xx = iconX + x;
				int yy = iconY + y * 17 - 1;
				
				canvas.setPixel(xx, yy, color);
			}
	}
	
	public void drawIcons(MapCanvas canvas)
	{
		for(int i = 0; i < icons.length; i++)
		{
			int iconX = iconStartX + (i % 6) * 18;
			int iconY = iconStartY + (i / 6) * 18;
			
			if(icons[i] != null && i < dayOfMonth)
				for(int x = 0; x < icons[i].length; x++)
					for(int y = 0; y < icons[i][x].length; y++)
					{
						int xx = iconX + x;
						int yy = iconY + y;
						Byte color = icons[i][x][y];
						
						if(color != null)
							canvas.setPixel(xx, yy, color);
					}
			else
				for(int x = 0; x < 16; x++)
					for(int y = 0; y < 16; y++)
					{
						int xx = iconX + x;
						int yy = iconY + y;
						
						canvas.setPixel(xx, yy, HIDDEN_COLOR);
					}
		}
	}
	
	public void drawNumbers(MapCanvas canvas)
	{
		for(int i = 0; i < icons.length && i < dayOfMonth; i++)
			if(icons[i] != null)
			{
				int numberX = iconStartX + (i % 6) * 18 + (16 - numbers[i].length) / 2;
				
				for(int x = 0; x < numbers[i].length; x++)
				{
					int numberY = iconStartY + (i / 6) * 18 + (16 - numbers[i][x].length) / 2;
					
					for(int y = 0; y < numbers[i][x].length; y++)
					{
						int xx = numberX + x;
						int yy = numberY + y;
						Byte color = numbers[i][x][y];
						
						if(color != null)
							canvas.setPixel(xx, yy, color);
					}
				}
			}
	}
	
	public void drawHeader(MapCanvas canvas)
	{
		int width = getWidth(header) * 2;
		int height = MinecraftFont.Font.getHeight() * 2;
		int startX = (MAP_SIZE - width) / 2;
		int startY = (iconStartY - 1 - height) / 3;
		
		drawText(canvas, startX, startY + 1, 2, 2, MinecraftFont.Font, header, COLOR_HEADER_BACKGROUND);
		drawText(canvas, startX, startY, 2, 2, MinecraftFont.Font, header, COLOR_HEADER);
	}
	
	public void drawDescription(MapCanvas canvas)
	{
		int width = getWidth(description);
		int height = MinecraftFont.Font.getHeight();
		int startX = (MAP_SIZE - width) / 2;
		int startY = ((iconStartY - 1 - height) / 4) * 3;
		
		drawText(canvas, startX, startY + 1, 1, 1, MinecraftFont.Font, description, COLOR_DESCRIPTION_BACKGROUND);
		drawText(canvas, startX, startY, 1, 1, MinecraftFont.Font, description, COLOR_DESCRIPTION);
	}
	
	public Byte[][][] loadIcons(ArrayList<AdventGift> gifts)
	{
		Byte[][][] icons = new Byte[AdventManager.LAST_DAY][][];
		
		for(int i = 0; i < icons.length; i++)
		{
			AdventGift gift = gifts.get(i);
			BPBlock block = gift.getBlock();
			loadIcon(icons, block, i);
		}
		
		return icons;
	}
	
	public Byte[][][] loadNumbers(int giftsSize)
	{
		Byte[][][] numbers = new Byte[giftsSize][][];
		
		for(int i = 0; i < giftsSize; i++)
		{
			String day = Integer.toString(i + 1);
			int width = getWidth(day) + 2;
			int height = MinecraftFont.Font.getHeight() + 2;
			Byte[][] number = new Byte[width][height];
			
			//Shadow
			for(int x = 0; x < 3; x++)
				for(int y = 0; y < 3; y++)
					drawText(number, x, y, 1, 1, MinecraftFont.Font, day, COLOR_TEXT_BACKGROUND);
			
			//Front
			drawText(number, 1, 1, 1, 1, MinecraftFont.Font, day, COLOR_TEXT);
			
			numbers[i] = number;
		}
		
		return numbers;
	}
	
	private static void loadIcon(Byte[][][] icons, BPBlock block, int index)
	{
		int id = block.getId();
		byte data = block.getData();
		@SuppressWarnings("deprecation")
		File file = new File("plugins/Breakpoint/images/textures/" + Material.getMaterial(id).name().toLowerCase() + (data != 0 ? ("_" + data) : "") + ".png");
		
		try
		{
			BufferedImage img = ImageIO.read(file);
			
			if(img == null)
				return;

			int width = img.getWidth();
			int height = img.getHeight();
			Byte[][] icon = new Byte[width < 16 ? width : 16][height < 16 ? height : 16];
			
			for(int x = 0; x < icon.length; x++)
				for(int y = 0; y < icon[x].length; y++)
				{
					Color color = Color.decode(Integer.toString(img.getRGB(x, y)));
					icon[x][y] = BPMapPalette.matchColor(color);
				}
			
			icons[index] = icon;
		}
		catch (Throwable e)
		{
			Breakpoint.warn("Error when loading gift icon: " + file.getName() + " (" + id + ":" + data + ")");
			e.printStackTrace();
		}
	}

	public AdventManager getAdventManager()
	{
		return advm;
	}
}
