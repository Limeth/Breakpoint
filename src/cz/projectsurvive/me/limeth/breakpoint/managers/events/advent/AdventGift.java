package cz.projectsurvive.me.limeth.breakpoint.managers.events.advent;

import java.util.LinkedList;
import java.util.List;

import cz.projectsurvive.me.limeth.breakpoint.equipment.BPBlock;

public class AdventGift
{
	private final BPBlock block;
	private final List<String> giftedTo;
	
	public AdventGift(BPBlock block, List<String> giftedTo)
	{
		if(block == null)
			throw new IllegalArgumentException("block == null");
		
		this.block = block;
		this.giftedTo = giftedTo != null ? giftedTo : new LinkedList<String>();
	}
	
	public AdventGift(BPBlock block)
	{
		this(block, null);
	}
	
	public void addGiftedTo(String playerName)
	{
		giftedTo.add(playerName);
	}

	public boolean hasEarned(String playerName)
	{
		return giftedTo.contains(playerName);
	}
	
	public List<String> getGiftedTo()
	{
		return giftedTo;
	}

	public BPBlock getBlock()
	{
		return block;
	}
}
