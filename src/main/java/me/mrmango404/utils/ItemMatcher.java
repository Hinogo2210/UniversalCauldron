package me.mrmango404.utils;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static org.bukkit.Material.*;

public class ItemMatcher {

	public enum DyeableItem {
		NAME_TAG(),
		LEATHER_ARMOR(),
		LEATHER_HORSE_ARMOR(),
		WOLF_ARMOR(),
		BED(),
		BUNDLE(),
		CANDLE(),
		SHULKER_BOX()
	}

	public static boolean matchNametag(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.NAME_TAG)) {
			return false;
		}
		return itemStack.getType() == NAME_TAG;
	}

	public static boolean matchLeatherArmor(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.LEATHER_ARMOR)) {
			return false;
		}

		final List<Material> LEATHER_ARMORS = List.of(
				Material.LEATHER_HELMET,
				Material.LEATHER_CHESTPLATE,
				Material.LEATHER_LEGGINGS,
				Material.LEATHER_BOOTS);

		return LEATHER_ARMORS.contains(itemStack.getType());
	}

	public static boolean matchLeatherHorseArmor(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.LEATHER_HORSE_ARMOR)) {
			return false;
		}
		return itemStack.getType() == LEATHER_HORSE_ARMOR;
	}

	public static boolean matchWolfArmor(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.WOLF_ARMOR)) {
			return false;
		}
		return itemStack.getType() == WOLF_ARMOR;
	}

	public static boolean matchBed(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.BED)) {
			return false;
		}
		return Tag.BEDS.isTagged(itemStack.getType());
	}

	public static boolean matchBundle(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.BUNDLE)) {
			return false;
		}
		return Tag.ITEMS_BUNDLES.isTagged(itemStack.getType());
	}

	public static boolean matchCandle(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.CANDLE)) {
			return false;
		}
		return Tag.CANDLES.isTagged(itemStack.getType());
	}

	public static boolean matchShulkerBox(ItemStack itemStack) {
		if (!isItemDyeable(DyeableItem.SHULKER_BOX)) {
			return false;
		}
		return Tag.SHULKER_BOXES.isTagged(itemStack.getType());
	}

	public static boolean isItemDyeable(ItemStack itemStack) {
		return matchNametag(itemStack) || matchLeatherArmor(itemStack) || matchLeatherHorseArmor(itemStack)
				|| matchWolfArmor(itemStack) || matchBed(itemStack) || matchBundle(itemStack) || matchCandle(itemStack) || matchShulkerBox(itemStack);
	}

	private static boolean isItemDyeable(DyeableItem dyeableItem) {
		return ConfigHandler.Settings.DYEABLES.stream()
				.map(String::toUpperCase)
				.anyMatch(str -> str.equals(dyeableItem.name()));
	}
}
