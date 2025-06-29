package me.mrmango404.cauldron;

import me.mrmango404.UniversalCauldron;
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
import org.bukkit.inventory.PlayerInventory;
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
		if (isItemDyeEventCancelled(entity)) return;

		ItemMatcher itemMatcher = new ItemMatcher(itemInHand);

		// Attempt to dye non-stackable items like armor, shulker boxes... etc.
		itemMatcher.matchNonStackableItem().ifPresent(item -> {
			String itemColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			switch (item) {
				case LEATHER_ARMOR, LEATHER_HORSE_ARMOR, WOLF_ARMOR -> dyeLeatherArmor(color, entity);
				case BED, BUNDLE, HARNESS -> {
					if (isSameColor(itemInHand, itemColor)) return;
					Material material = Material.valueOf(itemColor + "_" + item.name().toUpperCase());
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
			}
		});

		// Attempt to dye stackable items like name tags, wools, banners... etc.
		itemMatcher.matchStackableItem().ifPresent(item -> {
			ItemMatcher.StackableItem converted = ItemMatcher.StackableItem.valueOf(item.name().toUpperCase());
			if (item == ItemMatcher.StackableItem.NAME_TAG) {
				boolean shouldDye = true;
				String content = itemMeta.getDisplayName();
				Optional<String> itemColor = ColorManager.hasColor(content);

				if (itemColor.isPresent()) {
					String hex = ColorManager.translateColor(color);
					String itemHex = itemColor.get();
					if (hex.equals(itemHex)) shouldDye = false;
				}

				if (shouldDye && !content.isEmpty()) {
					if (addItem(player, ItemMatcher.StackableItem.NAME_TAG, color)) consumeWater(blockLoc, player);
				}
			} else {
				if (isSameColor(itemInHand, color)) return;
				if (addItem(player, converted, color)) consumeWater(blockLoc, player);
			}
		});
	}

	private void resetToDefault() {
		if (isItemWashEventCancelled()) return;

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
					boolean hasColor = ColorManager.hasColor(content).isPresent();
					if (!content.isEmpty() && hasColor) {
						itemMeta.setDisplayName(ChatColor.stripColor(content));
						itemInHand.setItemMeta(itemMeta);
						consumeWater(blockLoc, player);
					}
				}
				case WOOL, CARPET, CONCRETE, CONCRETE_POWDER, GLAZED_TERRACOTTA -> {
					Material whiteMat = Material.valueOf("WHITE_" + item.name().toUpperCase());
					if (material != whiteMat) {
						setItem(player, whiteMat);
						consumeWater(blockLoc, player);
					}
				}
				default -> {
					Material mat = Material.valueOf(item.name().toUpperCase());
					if (material != mat) {
						setItem(player, mat);
						consumeWater(blockLoc, player);
					}
				}
			}
		});
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

	/**
	 * Adds dyed item to player's inventory.
	 *
	 * @param type  Item type will be dyeing.
	 * @param color Color will be applied to the item.
	 * @return If the item was successfully dyed.
	 */
	private boolean addItem(Player player, ItemMatcher.StackableItem type, Color color) {
		Material material;
		int amountPerDye = ConfigHandler.Settings.STACKABLE_ITEMS.get(type.name()); // Number of items consumed/dyed per dyed action
		int undyedAmount = itemInHand.getAmount();
		PlayerInventory inventory = player.getInventory();
		ItemStack[] items = inventory.getContents();
		int addSub = Math.min(undyedAmount, amountPerDye); // Amount to be added/subtracted from the item

		if (type == ItemMatcher.StackableItem.NAME_TAG) {
			material = NAME_TAG;
		} else {
			String itemColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
			material = Material.valueOf(itemColor + type.getColoredSuffix());
		}

		for (ItemStack item : items) {
			if (item != null && item.getType() == material) {
				int dyedAmount = item.getAmount();
				int dyedMaxStack = item.getMaxStackSize();
				if (dyedAmount + addSub <= dyedMaxStack && material != NAME_TAG) {
					item.setAmount(dyedAmount + addSub);
					itemInHand.setAmount(undyedAmount - addSub);
					return true;
				}

				if (undyedAmount <= amountPerDye) {
					int handSlot = inventory.getHeldItemSlot();
					ItemStack newItem = createDyedItem(color, addSub, material);
					inventory.setItem(handSlot, newItem);
					itemInHand.setAmount(undyedAmount - addSub);
					return true;
				}
			}
		}

		// When there is an empty slot
		int firstEmpty = inventory.firstEmpty();
		if (firstEmpty != -1) {
			ItemStack newItem = createDyedItem(color, addSub, material);
			inventory.addItem(newItem);
			itemInHand.setAmount(undyedAmount - addSub);
			return true;
		}

		// Totally full
		return false;
	}

	private ItemStack createDyedItem(Color color, int addSub, Material material) {
		ItemStack newItem = new ItemStack(material);

		ItemMeta meta = itemMeta.clone();
		if (material == NAME_TAG) {
			String content = meta.getDisplayName();
			meta.setDisplayName(ColorManager.translateColor(color, ChatColor.stripColor(content)));
		}
		newItem.setAmount(addSub);
		newItem.setItemMeta(meta);

		return newItem;
	}


	private void dyeLeatherArmor(Color color, TextDisplay entity) {
		if (isItemDyeEventCancelled(entity)) return;
		LeatherArmorMeta meta = (LeatherArmorMeta) itemMeta;
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
		LeatherArmorMeta oldMeta = (LeatherArmorMeta) itemMeta.clone();
		if (oldMeta.getColor() != newMeta.getColor()) {
			if (isItemWashEventCancelled()) return;
			oldMeta.setColor(null);
			newItem.setItemMeta(oldMeta);
			player.getInventory().setItemInMainHand(newItem);
			consumeWater(blockLoc, player);
		}
	}

	/**
	 * Checks if an item have the same color with the color.
	 */
	private boolean isSameColor(ItemStack item, Color color) {
		String itemColor = ColorManager.DyeItemColor.getClosestDye(color).getColorKey();
		return isSameColor(item, itemColor);
	}

	private boolean isSameColor(ItemStack item, String color) {
		return item.getType().name().contains(color);
	}
}
