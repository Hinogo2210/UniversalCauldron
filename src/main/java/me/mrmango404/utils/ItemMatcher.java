package me.mrmango404.utils;

import me.mrmango404.listener.Cauldron;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static org.bukkit.Material.NAME_TAG;

public class ItemMatcher {

	public static boolean matchNametag(ItemStack itemStack) {
		if (!isItemDyeable(Cauldron.DyeableItem.NAME_TAG)) {
			return false;
		}
		return itemStack.getType() == NAME_TAG;
	}

	public static boolean matchLeatherArmor(ItemStack itemStack) {
		if (!isItemDyeable(Cauldron.DyeableItem.LEATHER_ARMOR)) {
			return false;
		}

		final List<Material> LEATHER_ARMORS = List.of(
				Material.LEATHER_HELMET,
				Material.LEATHER_CHESTPLATE,
				Material.LEATHER_LEGGINGS,
				Material.LEATHER_BOOTS);

		return LEATHER_ARMORS.contains(itemStack.getType());
	}

	public static boolean matchBed(ItemStack itemStack) {
		if (!isItemDyeable(Cauldron.DyeableItem.BED)) {
			return false;
		}
		return itemStack.getType().toString().endsWith("_BED");
	}

	public static boolean matchBundle(ItemStack itemStack) {
		if (!isItemDyeable(Cauldron.DyeableItem.BUNDLE)) {
			return false;
		}
		return itemStack.getType().toString().contains("BUNDLE");
	}

	public static boolean matchShulkerBox(ItemStack itemStack) {
		if (!isItemDyeable(Cauldron.DyeableItem.SHULKER_BOX)) {
			return false;
		}
		return itemStack.getType().toString().contains("SHULKER_BOX");
	}

	public static boolean isItemDyeable(Cauldron.DyeableItem dyeableItem) {
		return ConfigHandler.Settings.DYEABLES.stream()
				.map(String::toUpperCase)
				.anyMatch(str -> str.equals(dyeableItem.name()));
	}
}
