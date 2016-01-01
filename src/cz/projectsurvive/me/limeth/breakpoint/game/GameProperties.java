package cz.projectsurvive.me.limeth.breakpoint.game;

import cz.projectsurvive.me.limeth.breakpoint.players.BPPlayer;

public abstract class GameProperties
{
	private final Game game;
	private final BPPlayer bpPlayer;
	private CharacterType characterType;
	
	public GameProperties(Game game, BPPlayer bpPlayer, CharacterType ct)
	{
		this.game = game;
		this.bpPlayer = bpPlayer;
	}
	
	public GameProperties(Game game, BPPlayer bpPlayer)
	{
		this(game, bpPlayer, null);
	}
	
	public abstract boolean isPlaying();
	public abstract boolean hasSpawnProtection();
	public abstract String getChatPrefix();
	public abstract String getTagPrefix();
	
	public GameType getGameType()
	{
		return getGame().getType();
	}

	public BPPlayer getPlayer()
	{
		return bpPlayer;
	}

	public boolean hasCharacterType()
	{
		return characterType != null;
	}
	
	public CharacterType getCharacterType()
	{
		return characterType;
	}

	protected void setCharacterType(CharacterType characterType)
	{
		this.characterType = characterType;
	}

	public Game getGame()
	{
		return game;
	}
}
