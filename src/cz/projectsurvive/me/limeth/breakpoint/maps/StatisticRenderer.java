package cz.projectsurvive.me.limeth.breakpoint.maps;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

public abstract class StatisticRenderer extends BPMapRenderer
{
	public static final byte[] BACKGROUND_COLORS = {
			BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 0),
			BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 1),
			BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 2),
			BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 1),
			BPMapPalette.getColor(BPMapPalette.DARK_BROWN, 0),
		};
	public static final byte LABEL_COLOR = BPMapPalette.getColor(BPMapPalette.WHITE, 2);
	public static final int FONT_SCALE_Y = 3, FONT_DISTANCE_FROM_CENTER = 2, BORDER = 2;
	
	private String label;
	private byte color;
	
	public StatisticRenderer(String label, byte color)
	{
		Validate.notNull(label, "The label cannot be null!");
		
		this.label = label;
		this.color = color;
	}
	
	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		String value = getValue();
		
		drawRectangleFade(canvas, 0, 0, MAP_SIZE, MAP_SIZE, BACKGROUND_COLORS, 0);
		drawCenteredText(canvas, label, LABEL_COLOR, - MinecraftFont.Font.getHeight() * FONT_SCALE_Y - FONT_DISTANCE_FROM_CENTER);
		
		if(value != null)
			drawCenteredText(canvas, value, color, FONT_DISTANCE_FROM_CENTER);
		
		drawBorder(canvas, color, BORDER);
	}
	
	private void drawCenteredText(MapCanvas canvas, String text, byte color, int distanceFromCenter)
	{
		int labelWidth = getWidth(text);
		int labelScaleX = getScaleX(text, labelWidth);
		labelWidth *= labelScaleX;
		int labelX = (MAP_SIZE - labelWidth) / 2;
		int labelY = (MAP_SIZE / 2) + distanceFromCenter;
		
		drawText(canvas, labelX, labelY, labelScaleX, FONT_SCALE_Y, MinecraftFont.Font, text, color);
	}
	
	private void drawBorder(MapCanvas canvas, byte color, int width)
	{
		drawRectangle(canvas, 0, 0, BORDER, MAP_SIZE, color);
		drawRectangle(canvas, MAP_SIZE - BORDER, 0, BORDER, MAP_SIZE, color);
		drawRectangle(canvas, BORDER, 0, MAP_SIZE - BORDER * 2, BORDER, color);
		drawRectangle(canvas, BORDER, MAP_SIZE - BORDER, MAP_SIZE - BORDER * 2, BORDER, color);
	}
	
	public abstract String getValue();
	
	public int getScaleX(String string, int width)
	{
		return (MAP_SIZE - BORDER) / width;
	}
	
	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		Validate.notNull(label, "The label cannot be null!");
		
		this.label = label;
	}

	public byte getColor()
	{
		return color;
	}

	public void setColor(byte color)
	{
		this.color = color;
	}
}
