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
		if (isDyeEventCancelled(entity)) return;

		ItemMatcher itemMatcher = new ItemMatcher(itemInHand);

		// Attempt to dye non-stackable items like name tags, armor... etc.
		itemMatcher.matchNonStackableItem().ifPresent(item -> {
			String itemColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			switch (item) {
				case LEATHER_ARMOR, LEATHER_HORSE_ARMOR, WOLF_ARMOR -> dyeLeatherArmor(color, entity);
				case BED -> {
					if (isSameColor(itemInHand, itemColor)) return;
					Material material = Material.valueOf(itemColor + "_BED");
					setItem(player, material);
					consumeWater(blockLoc, player);
				}
				case BUNDLE -> {
					if (isSameColor(itemInHand, itemColor)) return;
					Material material = Material.valueOf(itemColor + "_BUNDLE");
					setItem(player, material);
					consumeWater(blockLoc, player);
				}
				case SHULKER_BOX -> {
					if (isSameColor(itemInHand, itemColor)) return;
					ItemStack newItem = new ItemStack(Material.valueOf(itemColor + "_SHULKER_BOX"));

					BlockStateMeta oldMeta = (BlockStateMeta) itemInHand.getItemMeta();
					BlockStateMeta newMeta = (BlockStateMeta) newItem.getItemMeta();

					ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();

					newMeta.setBlockState(oldBox);
					newItem.setItemMeta(newMeta);

					player.getInventory().setItemInMainHand(newItem);
					consumeWater(blockLoc, player);
				}
				case HARNESS -> {
					if (isSameColor(itemInHand, itemColor)) return;
					Material material = Material.valueOf(itemColor + "_HARNESS");
					setItem(player, material);
					consumeWater(blockLoc, player);
				}
			}
		});

		// Attempt to dye stackable items like wools, banners... etc.
		itemMatcher.matchStackableItem().ifPresent(item -> {
			String itemColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			switch (item) {
				case NAME_TAG -> {
					if (addItem(player, NAME_TAG, ItemMatcher.StackableItem.NAME_TAG, color)) {
						consumeWater(blockLoc, player);
					}
				}
				case WOOL -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_WOOL");
					if (addItem(player, material, ItemMatcher.StackableItem.WOOL)) {
						consumeWater(blockLoc, player);
					}
				}
				case CARPET -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_CARPET");
					if (addItem(player, material, ItemMatcher.StackableItem.CARPET)) {
						consumeWater(blockLoc, player);
					}
				}
				case TERRACOTTA -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_TERRACOTTA");
					if (addItem(player, material, ItemMatcher.StackableItem.TERRACOTTA)) {
						consumeWater(blockLoc, player);
					}
				}
				case CONCRETE -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_CONCRETE");
					if (addItem(player, material, ItemMatcher.StackableItem.CONCRETE)) {
						consumeWater(blockLoc, player);
					}
				}
				case CONCRETE_POWDER -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_CONCRETE_POWDER");

					if (addItem(player, material, ItemMatcher.StackableItem.CONCRETE_POWDER)) {
						consumeWater(blockLoc, player);
					}
				}
				case GLAZED_TERRACOTTA -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_GLAZED_TERRACOTTA");
					if (addItem(player, material, ItemMatcher.StackableItem.GLAZED_TERRACOTTA)) {
						consumeWater(blockLoc, player);
					}
				}
				case GLASS -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_STAINED_GLASS");
					if (addItem(player, material, ItemMatcher.StackableItem.GLASS)) {
						consumeWater(blockLoc, player);
					}
				}
				case GLASS_PANE -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_STAINED_GLASS_PANE");
					if (addItem(player, material, ItemMatcher.StackableItem.GLASS_PANE)) {
						consumeWater(blockLoc, player);
					}
				}
				case CANDLE -> {
					if (isSameColor(itemInHand, itemColor)) return;

					Material material = Material.valueOf(itemColor + "_CANDLE");
					if (addItem(player, material, ItemMatcher.StackableItem.CANDLE)) {
						consumeWater(blockLoc, player);
					}
				}
			}
		});
	}

	private void resetToDefault() {
		if (isWashEventCancelled()) return;

		ItemMatcher itemMatcher = new ItemMatcher(itemInHand);
		Material material = itemInHand.getType();

		itemMatcher.matchNonStackableItem().ifPresent(item -> {
			switch (item) {
				case LEATHER_ARMOR, LEATHER_HORSE_ARMOR, WOLF_ARMOR -> washLeatherArmor();
				case BED -> {
					if (material != WHITE_BED) {
						setItem(player, WHITE_BED);
						consumeWater(blockLoc, player);
					}
				}
				case BUNDLE -> {
					if (material != BUNDLE) {
						setItem(player, BUNDLE);
						consumeWater(blockLoc, player);
					}
				}
				case SHULKER_BOX -> {
					if (material != SHULKER_BOX) {
						ItemStack newItem = new ItemStack(SHULKER_BOX);

						BlockStateMeta oldMeta = (BlockStateMeta) itemInHand.getItemMeta();
						BlockStateMeta newMeta = (BlockStateMeta) newItem.getItemMeta();

						ShulkerBox oldBox = (ShulkerBox) oldMeta.getBlockState();

						newMeta.setBlockState(oldBox);
						newItem.setItemMeta(newMeta);

						player.getInventory().setItemInMainHand(newItem);
						consumeWater(blockLoc, player);
					}
				}
				case HARNESS -> {
					if (material != WHITE_HARNESS) {
						setItem(player, WHITE_HARNESS);
						consumeWater(blockLoc, player);
					}
				}
			}
		});

		itemMatcher.matchStackableItem().ifPresent(item -> {
			switch (item) {
				case NAME_TAG -> {
					String content = itemMeta.getDisplayName();
					if (!content.isEmpty()) {
						itemMeta.setDisplayName(ChatColor.stripColor(content));
						itemInHand.setItemMeta(itemMeta);
						consumeWater(blockLoc, player);
					}
				}
				case WOOL -> {
					if (material != WHITE_WOOL) {
						setItem(player, WHITE_WOOL);
						consumeWater(blockLoc, player);
					}
				}
				case CARPET -> {
					if (material != WHITE_CARPET) {
						setItem(player, WHITE_CARPET);
						consumeWater(blockLoc, player);
					}
				}
				case TERRACOTTA -> {
					if (material != TERRACOTTA) {
						setItem(player, TERRACOTTA);
						consumeWater(blockLoc, player);
					}
				}
				case CONCRETE -> {
					if (material != WHITE_CONCRETE) {
						setItem(player, WHITE_CONCRETE);
						consumeWater(blockLoc, player);
					}
				}
				case CONCRETE_POWDER -> {
					if (material != WHITE_CONCRETE_POWDER) {
						setItem(player, WHITE_CONCRETE_POWDER);
						consumeWater(blockLoc, player);
					}
				}
				case GLAZED_TERRACOTTA -> {
					if (material != WHITE_GLAZED_TERRACOTTA) {
						setItem(player, WHITE_GLAZED_TERRACOTTA);
						consumeWater(blockLoc, player);
					}
				}
				case GLASS -> {
					if (material != GLASS) {
						setItem(player, GLASS);
						consumeWater(blockLoc, player);
					}
				}
				case GLASS_PANE -> {
					if (material != GLASS_PANE) {
						setItem(player, GLASS_PANE);
						consumeWater(blockLoc, player);
					}
				}
				case CANDLE -> {
					if (material != CANDLE) {
						setItem(player, CANDLE);
						consumeWater(blockLoc, player);
					}
				}
			}
		});
	}

	private boolean isDyeEventCancelled(TextDisplay entity) {
		ItemDyeEvent customEvent = new ItemDyeEvent(block, entity, player);
		Bukkit.getPluginManager().callEvent(customEvent);
		return customEvent.isCancelled();
	}

	private boolean isWashEventCancelled() {
		ItemWashEvent customEvent = new ItemWashEvent(block, null, player);
		Bukkit.getPluginManager().callEvent(customEvent);
		return customEvent.isCancelled();
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

			if (!PermissionManager.hasPermission(player, PermissionManager.INFINITE_WATER)) {
				CauldronLevelChangeEvent event = new CauldronLevelChangeEvent(block, player, CauldronLevelChangeEvent.ChangeReason.UNKNOWN, state);
				Bukkit.getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					block.setBlockData(state.getBlockData());
				}
			}

			new SpecialEffect(location).play(SpecialEffect.EffectType.DYE_N_WASH_ITEM);
		}
	}

	private void setItem(Player player, Material material) {
		ItemStack newItem = new ItemStack(material);
		newItem.setAmount(itemInHand.getAmount());
		newItem.setItemMeta(itemMeta);
		player.getInventory().setItemInMainHand(newItem);
	}

	private boolean addItem(Player player, Material material, ItemMatcher.StackableItem item, Color... color) {
		int amount = ConfigHandler.Settings.STACKABLE_ITEMS.get(item.name());
		int handAmount = itemInHand.getAmount();
		int maxStackSize = itemInHand.getMaxStackSize();

		if (handAmount < amount) amount = handAmount;

		ItemStack newItem = new ItemStack(material, amount);
		newItem.setItemMeta(itemMeta);

		if (material == NAME_TAG) {
			String content = itemMeta.getDisplayName();
			if (!content.isEmpty()) {
				itemMeta.setDisplayName(ColorManager.translateColor(color[0], ChatColor.stripColor(content)));
				newItem.setItemMeta(itemMeta);
			}
		}

		for (ItemStack slotItem : player.getInventory().getContents()) {
			if (slotItem != null && slotItem.getType() == material) {
				int combined = slotItem.getAmount() + amount;

				if (combined <= maxStackSize) {
					slotItem.setAmount(combined);
					itemInHand.setAmount(handAmount - amount);
					return true;
				}

				if (slotItem.getAmount() < maxStackSize) {
					int available = maxStackSize - slotItem.getAmount();
					int excess = amount - available;

					slotItem.setAmount(maxStackSize);
					itemInHand.setAmount(handAmount - amount);

					ItemStack dropped = new ItemStack(material, excess);
					dropped.setItemMeta(itemMeta);
					blockLoc.getWorld().dropItem(player.getEyeLocation(), dropped)
							.setVelocity(player.getLocation().getDirection().multiply(0.2f));

					return true;
				}
			}
		}

		int emptySlot = player.getInventory().firstEmpty();
		if (emptySlot != -1) {
			player.getInventory().setItem(emptySlot, newItem);
			itemInHand.setAmount(handAmount - amount);
			return true;
		}

		// No space
		return false;
	}


	private void dyeLeatherArmor(Color color, TextDisplay entity) {
		if (isDyeEventCancelled(entity)) return;
		LeatherArmorMeta meta = (LeatherArmorMeta) itemInHand.getItemMeta();
		meta.setColor(color);
		itemInHand.setItemMeta(meta);

		if (UniversalCauldron.isFolia()) {
			player.getScheduler().runDelayed(UniversalCauldron.getInstance(), task -> player.updateInventory(), null, 1L);
		} else {
			Bukkit.getScheduler().runTaskLater(UniversalCauldron.getInstance(), player::updateInventory, 1L);
		}
		consumeWater(blockLoc, player);
	}

	private void washLeatherArmor() {
		ItemStack newItem = new ItemStack(itemInHand.getType());
		LeatherArmorMeta newMeta = (LeatherArmorMeta) newItem.getItemMeta();
		LeatherArmorMeta oldMeta = (LeatherArmorMeta) itemInHand.getItemMeta();
		if (oldMeta.getColor() != newMeta.getColor()) {
			if (isWashEventCancelled()) return;
			oldMeta.setColor(null);
			newItem.setItemMeta(oldMeta);
			player.getInventory().setItemInMainHand(newItem);
			consumeWater(blockLoc, player);
		}
	}

	/**
	 * Checks if an item have the same color.
	 */
	private boolean isSameColor(ItemStack item, String color) {
		return item.getType().name().contains(color);
	}
}
