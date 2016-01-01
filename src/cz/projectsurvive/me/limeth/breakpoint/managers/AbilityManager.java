package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import cz.projectsurvive.me.limeth.breakpoint.Breakpoint;

public class AbilityManager
{
	Breakpoint plugin;

	public AbilityManager(Breakpoint p)
	{
		plugin = p;
	}

	public static Fireball launchFireball(Player owner, Location loc, Vector vec)
	{
		World world = loc.getWorld();
		Fireball fb = (Fireball) world.spawnEntity(loc.add(vec), EntityType.FIREBALL);
		fb.setShooter(owner);
		fb.setVelocity(vec.multiply(2.0));
		world.playSound(loc, Sound.WITHER_SHOOT, 1F, 1F);
		return fb;
	}

	public static Fireball launchSmallFireball(Player owner, Location loc, Vector vec)
	{
		World world = loc.getWorld();
		Fireball fb = (Fireball) world.spawnEntity(loc.add(vec), EntityType.SMALL_FIREBALL);
		fb.setShooter(owner);
		fb.setVelocity(vec.multiply(2.0));
		world.playSound(loc, Sound.GHAST_FIREBALL, 1F, 2F);
		return fb;
	}

	public static void fireballHit(Fireball fb)
	{
		Location loc = fb.getLocation();
		showCracks(loc, 2);
		smoke(loc, 16);
	}

	public static void smallFireballHit(SmallFireball fb)
	{
		Location loc = fb.getLocation();
		World world = loc.getWorld();
		showCracks(loc, 1);
		world.playEffect(loc, Effect.MOBSPAWNER_FLAMES, 0);
	}

	public static Vector launchPlayer(Player player)
	{
		Vector vec = AbilityManager.getDirection(player);
		Location loc = player.getLocation();
		World world = loc.getWorld();
		player.setVelocity(vec);
		world.playSound(loc, Sound.LAVA_POP, 0.5F, new Random().nextFloat());
		return vec;
	}

	public static Vector getDirection(Player player)
	{
		Location loc = player.getLocation();
		Vector vec = loc.getDirection();
		return vec;
	}

	public static boolean isHeadshot(Location shooterLocation, Location victimLocation, Arrow arrow)
	{
		Location arrowLocation = arrow.getLocation();
		double arrowY = arrowLocation.getY() + arrow.getVelocity().getY() / 2d;
		double victimY = victimLocation.getY();
		boolean headshot = arrowY - victimY > 1.35d && victimLocation.distance(shooterLocation) > 8;
		return headshot;
	}

	public static void playHeadshotEffect(Player player)
	{
		Location loc = player.getLocation();
		World world = loc.getWorld();
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
		world.playEffect(loc, Effect.ZOMBIE_CHEW_IRON_DOOR, 0);
	}

	public static void showCracks(Location loc, int radius)
	{
		World world = loc.getWorld();
		for (int x = -radius; x <= radius; x++)
			for (int y = -radius; y <= radius; y++)
				for (int z = -radius; z <= radius; z++)
				{
					Location curLoc = loc.clone().add(x, y, z);
					if (curLoc.distance(loc) > radius)
						continue;
					Block block = world.getBlockAt(curLoc);
					world.playEffect(curLoc, Effect.STEP_SOUND, block.getType());
				}
	}

	public static void smoke(Location loc, int amount)
	{
		World world = loc.getWorld();
		Random rnd = new Random();
		for (int i = 0; i < amount; i++)
			world.playEffect(loc, Effect.SMOKE, rnd.nextInt(16));
	}
}
