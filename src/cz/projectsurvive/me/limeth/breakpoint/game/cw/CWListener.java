package cz.projectsurvive.me.limeth.breakpoint.game.cw;

import cz.projectsurvive.me.limeth.breakpoint.game.Game;
import cz.projectsurvive.me.limeth.breakpoint.game.ctf.CTFListener;

public class CWListener extends CTFListener
{
	public CWListener(Game game)
	{
		super(game, CWGame.class);
	}
	
	@Override
	public CWGame getGame()
	{
		return (CWGame) super.getGame();
	}
}
