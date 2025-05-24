package me.mrmango404;

import me.mrmango404.cauldron.CauldronCleanHandler;
import me.mrmango404.cauldron.CauldronDyeHandler;
import me.mrmango404.cauldron.ItemDyeWashHandler;
import me.mrmango404.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CauldronListener implements Listener {

	public enum DyeableItem {
		NAME_TAG("NAME_TAG"),
		LEATHER_ARMOR("LEATHER_ARMOR"),
		BED("BED"),
		BUNDLE("BUNDLE"),
		SHULKER_BOX("SHULKER_BOX");

		String name;

		DyeableItem(String name) {
			this.name = name;
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onCauldronInteraction(PlayerInteractEvent event) {
		Block block;
		Material materialInHand;
		Player player = event.getPlayer();

		if (!PermissionManager.hasPermission(player, PermissionManager.INTERACTION)) {
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
			return;
		}

		block = event.getClickedBlock();
		materialInHand = player.getInventory().getItemInMainHand().getType();

		if (block.getType() != Material.WATER_CAULDRON) {
			return;
		}

		// Resets the color of the cauldron by removing the color layer.
		// Triggered by right-clicking the cauldron using a cleaner item.
		Material washItem = Material.getMaterial(ConfigHandler.Settings.WASH_ITEM);

		if (materialInHand == washItem) {
			new CauldronCleanHandler(block, player).handle();
		} else if (ColorManager.DyeItemColor.fromMaterial(materialInHand).isPresent()) {
			new CauldronDyeHandler(block, player).handle();
		} else {
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			if (ItemMatcher.isItemDyeable(itemInHand)) {
				event.setUseItemInHand(Event.Result.DENY);
				event.setUseInteractedBlock(Event.Result.DENY);
				event.setCancelled(true);
			}
			new ItemDyeWashHandler(block, player).handle();
		}
	}

	/**
	 * Despawns the color layer when a cauldron is broken.
	 */
	// stayed after breaking -> works
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		Location location = event.getBlock().getLocation();
		ColorLayerManager.remove(location);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityExplosion(EntityExplodeEvent event) {
		onExplosion(event.blockList());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockExplosion(BlockExplodeEvent event) {
		onExplosion(event.blockList());
	}

	/**
	 * Moves the color layer when its cauldron is pushed or pulled by a piston.
	 */
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonExtend(BlockPistonExtendEvent event) {
		onPiston(event, event.getBlocks());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPistonRetract(BlockPistonRetractEvent event) {
		onPiston(event, event.getBlocks());
	}

	private void onPiston(BlockPistonEvent event, List<Block> blocks) {
		BlockFace direction = event.getDirection();

		for (Block block : blocks) {
			Location oldLoc = block.getLocation();
			Location newLoc = block.getRelative(direction).getLocation();

			ColorLayerManager.getEntity(oldLoc).ifPresent(textDisplay -> {
				PersistentDataSetter.getColorData(textDisplay).ifPresent(entityColor -> {
					ColorLayerManager.teleport(textDisplay, newLoc);
				});
			});
		}
	}

	private void onExplosion(List<Block> blocks) {
		for (Block block : blocks) {
			if (block.getType() == Material.WATER_CAULDRON) {
				ColorLayerManager.remove(block.getLocation());
			}
		}
	}

	/**
	 * Updates the position of the color layer to match the cauldron's water level change.
	 */
	@EventHandler
	public void onWaterLevelChange(CauldronLevelChangeEvent event) {
		Location location = event.getBlock().getLocation();
		BlockData data = event.getNewState().getBlockData();
		Material material = data.getMaterial();

		if (data instanceof Levelled levelled && material == Material.WATER_CAULDRON) {
			int waterLevel = levelled.getLevel();
			ColorLayerManager.getEntity(location).ifPresent(textDisplay -> {
				PersistentDataSetter.getColorData(textDisplay).ifPresent(entityColor -> {
					ColorLayerManager.update(location, entityColor, waterLevel);
				});
			});
			return;
		}

		ColorLayerManager.remove(location);
	}
}
