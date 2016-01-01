package cz.projectsurvive.me.limeth.breakpoint.sound;


public class BPSoundSet
{
	private final BPSound[] sounds;
	
	public BPSoundSet(BPSound... sounds)
	{
		this.sounds = sounds;
	}

	public BPSound[] getSounds()
	{
		return sounds;
	}
}
