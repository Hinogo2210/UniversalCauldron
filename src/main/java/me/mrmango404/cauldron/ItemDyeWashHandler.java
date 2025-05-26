package me.mrmango404.cauldron;

import me.mrmango404.UniversalCauldron;
import me.mrmango404.api.events.ItemDyeEvent;
import me.mrmango404.api.events.ItemWashEvent;
import me.mrmango404.utils.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Optional;

import static org.bukkit.Material.*;

/**
 * Responsible for dyeing and washing item.
 */
public class ItemDyeWashHandler extends ICHandler {

	private final ItemStack itemInHand;
	private final ItemMeta itemMeta;

	public ItemDyeWashHandler(Block block, Player player) {
		super(block, player);
		itemInHand = player.getInventory().getItemInMainHand();
		itemMeta = itemInHand.getItemMeta();
	}

	@Override
	public void handle() {
		Optional<TextDisplay> optional = ColorLayerManager.getEntity(blockLoc);
		if (optional.isPresent()) {
			TextDisplay entity = optional.get();
			PersistentDataSetter.getColorData(entity).ifPresent(entityColor -> {
				dyeWithColor(entity, entityColor);
			});
		} else {
			resetToDefault();
		}
	}

	private void dyeWithColor(TextDisplay entity, Color color) {
		if (ItemMatcher.matchNametag(itemInHand)) {
			ItemMeta meta = itemInHand.getItemMeta();
			String content = meta.getDisplayName();
			if (!content.isEmpty()) {
				meta.setDisplayName(ColorManager.translateColor(color, ChatColor.stripColor(content)));
				itemInHand.setItemMeta(meta);
				dyeItem(entity);
			}
		}

		if (ItemMatcher.matchLeatherArmor(itemInHand)) {
			dyeLeatherArmor(color, entity);
		}

		if (ItemMatcher.matchWolfArmor(itemInHand)) {
			dyeLeatherArmor(color, entity);
		}

		if (ItemMatcher.matchBed(itemInHand)) {
			String bedColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			Material material = Material.valueOf(bedColor + "_BED");
			setItem(player, material);
			dyeItem(entity);
		}

		if (ItemMatcher.matchBundle(itemInHand)) {
			String bundleColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			Material material = Material.valueOf(bundleColor + "_BUNDLE");
			setItem(player, material);
			dyeItem(entity);
		}

		if (ItemMatcher.matchShulkerBox(itemInHand)) {
			String shulkerColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			ItemStack newItem = new ItemStack(Material.valueOf(shulkerColor + "_SHULKER_BOX"));

			BlockStateMeta oldMeta = (BlockStateMeta) itemInHand.getItemMeta();
			BlockStateMeta newMeta = (BlockStateMeta) newItem.getItemMeta();

			ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();

			newMeta.setBlockState(oldBox);
			newItem.setItemMeta(newMeta);

			player.getInventory().setItemInMainHand(newItem);
			dyeItem(entity);
		}
	}

	private void resetToDefault() {
		if (ItemMatcher.matchNametag(itemInHand)) {
			ItemMeta meta = itemInHand.getItemMeta();
			String content = meta.getDisplayName();
			if (!content.equals("")) {
				meta.setDisplayName(ChatColor.stripColor(content));
				itemInHand.setItemMeta(meta);
				washItem();
			}
		}

		if (ItemMatcher.matchLeatherArmor(itemInHand)) {
			washLeatherArmor();
		}

		if (ItemMatcher.matchWolfArmor(itemInHand)) {
			washLeatherArmor();
		}

		if (ItemMatcher.matchBed(itemInHand)) {
			if (itemInHand.getType() != WHITE_BED) {
				setItem(player, WHITE_BED);
				washItem();
			}
		}

		if (ItemMatcher.matchBundle(itemInHand)) {
			if (itemInHand.getType() != BUNDLE) {
				setItem(player, BUNDLE);
				washItem();
			}
		}

		if (ItemMatcher.matchShulkerBox(itemInHand)) {
			if (itemInHand.getType() != SHULKER_BOX) {
				ItemStack newItem = new ItemStack(SHULKER_BOX);

				BlockStateMeta oldMeta = (BlockStateMeta) itemInHand.getItemMeta();
				BlockStateMeta newMeta = (BlockStateMeta) newItem.getItemMeta();

				ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();

				newMeta.setBlockState(oldBox);
				newItem.setItemMeta(newMeta);

				player.getInventory().setItemInMainHand(newItem);
				washItem();
			}
		}
	}

	private void dyeItem(TextDisplay entity) {
		ItemDyeEvent customEvent = new ItemDyeEvent(block, entity, player);
		Bukkit.getPluginManager().callEvent(customEvent);
		if (customEvent.isCancelled()) {
			return;
		}
		consumeWater(blockLoc, player);
	}

	private void washItem() {
		ItemWashEvent customEvent = new ItemWashEvent(block, null, player);
		Bukkit.getPluginManager().callEvent(customEvent);
		if (customEvent.isCancelled()) {
			return;
		}
		consumeWater(blockLoc, player);
	}

	private void consumeWater(Location location, Player player) {
		Block block = location.getBlock();

		if (block.getType() == WATER_CAULDRON) {
			BlockState state = block.getState();
			Levelled data = (Levelled) block.getBlockData();
			int nextLevel = data.getLevel() - 1;

			if (nextLevel == 0) {
				state.setType(CAULDRON);
			} else {
				data.setLevel(nextLevel);
				state.setBlockData(data);
			}

			CauldronLevelChangeEvent event = new CauldronLevelChangeEvent(block, player, CauldronLevelChangeEvent.ChangeReason.UNKNOWN, state);
			Bukkit.getPluginManager().callEvent(event);

			if (!event.isCancelled()) {
				block.setBlockData(state.getBlockData());
				new SpecialEffect(location).play(SpecialEffect.EffectType.DYE_N_WASH_ITEM);
			}
		}
	}

	private void setItem(Player player, Material material) {
		ItemStack newItem = new ItemStack(material);
		newItem.setItemMeta(itemMeta);
		player.getInventory().setItemInMainHand(newItem);
	}

	private void dyeLeatherArmor(Color color, TextDisplay entity) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemInHand.getItemMeta();
		meta.setColor(color);
		itemInHand.setItemMeta(meta);
		dyeItem(entity);
		Bukkit.getScheduler().runTaskLater(UniversalCauldron.getInstance(), player::updateInventory, 1L);
	}

	private void washLeatherArmor() {
		ItemStack newItem = new ItemStack(itemInHand.getType());
		LeatherArmorMeta newMeta = (LeatherArmorMeta) newItem.getItemMeta();
		LeatherArmorMeta oldMeta = (LeatherArmorMeta) itemInHand.getItemMeta();
		if (oldMeta.getColor() != newMeta.getColor()) {
			oldMeta.setColor(null);
			newItem.setItemMeta(oldMeta);
			player.getInventory().setItemInMainHand(newItem);
			washItem();
		}
	}
}
