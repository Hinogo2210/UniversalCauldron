package me.mrmango404.utils;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ItemMatcher {

	private final ItemStack itemStack;

	public ItemMatcher(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public enum NonStackableItem {
		LEATHER_ARMOR(null),
		LEATHER_HORSE_ARMOR(Material.LEATHER_HORSE_ARMOR),
		WOLF_ARMOR(Material.WOLF_ARMOR),
		BED(null),
		BUNDLE(null),
		SHULKER_BOX(null),
		HARNESS(null);

		private final Material material;

		NonStackableItem(Material material) {
			this.material = material;
		}

		public boolean match(ItemStack item) {
			Material material = item.getType();
			return switch (this) {
				case LEATHER_ARMOR -> List.of(
						Material.LEATHER_HELMET,
						Material.LEATHER_CHESTPLATE,
						Material.LEATHER_LEGGINGS,
						Material.LEATHER_BOOTS).contains(material);
				case BED -> Tag.BEDS.isTagged(material);
				case BUNDLE -> material.name().contains("BUNDLE");
				case SHULKER_BOX -> Tag.SHULKER_BOXES.isTagged(material);
				case HARNESS -> material.name().contains("_HARNESS");
				default -> material == this.material;
			};
		}
	}

	public enum StackableItem {
		NAME_TAG,
		WOOL,
		CARPET,
		TERRACOTTA,
		CONCRETE,
		CONCRETE_POWDER,
		GLAZED_TERRACOTTA,
		GLASS,
		GLASS_PANE,
		CANDLE;

		public boolean match(ItemStack item) {
			Material material = item.getType();
			return switch (this) {
				case NAME_TAG -> material.name().equals("NAME_TAG");
				case WOOL -> Tag.WOOL.isTagged(material);
				case CARPET -> Tag.WOOL_CARPETS.isTagged(material);
				case TERRACOTTA -> Tag.TERRACOTTA.isTagged(material);
				case CONCRETE -> material.name().endsWith("_CONCRETE");
				case CONCRETE_POWDER -> Tag.CONCRETE_POWDER.isTagged(material);
				case GLAZED_TERRACOTTA -> material.name().endsWith("_GLAZED_TERRACOTTA");
				case GLASS -> material.name().equals("GLASS") || material.name().endsWith("_STAINED_GLASS");
				case GLASS_PANE ->
						material.name().equals("GLASS_PANE") || material.name().endsWith("_STAINED_GLASS_PANE");
				case CANDLE -> Tag.CANDLES.isTagged(material);
			};
		}
	}

	public Optional<NonStackableItem> matchNonStackableItem() {
		return Arrays.stream(NonStackableItem.values())
				.filter(item -> item.match(itemStack))
				.findFirst();
	}

	public Optional<StackableItem> matchStackableItem() {
		return Arrays.stream(StackableItem.values())
				.filter(item -> item.match(itemStack))
				.findFirst();
	}

	public boolean isItemDyeable() {
		return matchNonStackable() || matchStackable();
	}

	/**
	 * Determines if a NonStackableItem is allowed to be dyed in the config.
	 */
	private boolean matchNonStackable() {
		return Arrays.stream(NonStackableItem.values())
				.anyMatch(item -> isItemDyeable(item) && item.match(itemStack));
	}

	private boolean matchStackable() {
		return Arrays.stream(StackableItem.values())
				.anyMatch(item -> isItemDyeable(item) && item.match(itemStack));
	}

	/**
	 * Determines if a NonStackableItem is allowed to be dyed in the config.
	 */
	private boolean isItemDyeable(NonStackableItem nonStackableItem) {
		return ConfigHandler.Settings.NON_STACKABLE_ITEMS.stream()
				.map(String::toUpperCase)
				.anyMatch(str -> str.equals(nonStackableItem.name()));
	}

	private boolean isItemDyeable(StackableItem stackableItem) {
		for (String item : ConfigHandler.Settings.STACKABLE_ITEMS.keySet()) {
			if (item.toUpperCase().equals(stackableItem.name())) {
				return true;
			}
		}
		return false;
	}
}
