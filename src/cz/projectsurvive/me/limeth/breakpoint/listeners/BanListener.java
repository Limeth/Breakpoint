package cz.projectsurvive.me.limeth.breakpoint.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;

public class BanListener implements Listener {
	
	@EventHandler public void e(BlockBreakEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockBurnEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockDamageEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockDispenseEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockFadeEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockFormEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockFromToEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockGrowEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockIgniteEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockPhysicsEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockPistonRetractEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockPistonExtendEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockPlaceEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BlockSpreadEvent event) { event.setCancelled(true); }
	@EventHandler public void e(BrewEvent event) { event.setCancelled(true); }
	@EventHandler public void e(CraftItemEvent event) { event.setCancelled(true); }
	@EventHandler public void e(CreatureSpawnEvent event) { event.setCancelled(true); }
	@EventHandler public void e(EnchantItemEvent event) { event.setCancelled(true); }
	@EventHandler public void e(EntityDamageEvent event) { event.setCancelled(true); }
	@EventHandler public void e(FoodLevelChangeEvent event) { event.setCancelled(true); }
	@EventHandler public void e(HangingBreakEvent event) { event.setCancelled(true); }
	@EventHandler public void e(HangingPlaceEvent event) { event.setCancelled(true); }
	@EventHandler public void e(InventoryClickEvent event) { event.setCancelled(true); }
	@EventHandler public void e(InventoryOpenEvent event) { event.setCancelled(true); }
	@EventHandler public void e(LeavesDecayEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PlayerBedEnterEvent event) { event.setCancelled(true); }
	@EventHandler public void e(AsyncPlayerChatEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PlayerDropItemEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PlayerInteractEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PlayerPickupItemEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PlayerPortalEvent event) { event.setCancelled(true); }
	@EventHandler public void e(PotionSplashEvent event) { event.setCancelled(true); }
	@EventHandler public void e(ProjectileLaunchEvent event) { event.setCancelled(true); }
	@EventHandler public void e(StructureGrowEvent event) { event.setCancelled(true); }
	@EventHandler public void e(WeatherChangeEvent event) { event.setCancelled(true); }
	
}
