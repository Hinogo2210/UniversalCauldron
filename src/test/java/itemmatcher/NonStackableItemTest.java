package itemmatcher;

import me.mrmango404.UniversalCauldron;
import me.mrmango404.utils.ConfigHandler;
import me.mrmango404.utils.ItemMatcher;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NonStackableItemTest {

	@BeforeEach
	public void setUp() {
		MockBukkit.mock();
		MockBukkit.load(UniversalCauldron.class);
	}

	@AfterEach
	public void tearDown() {
		MockBukkit.unmock();
	}

	// Test if the non-stackable item matching returns the correct NonStackableItem type
	@Test
	public void testNonStackableItemMatching() {

		Map<ItemStack, ItemMatcher.NonStackableItem> validMaterials = Map.of(
				new ItemStack(Material.LEATHER_HELMET), ItemMatcher.NonStackableItem.LEATHER_ARMOR,
				new ItemStack(Material.LEATHER_CHESTPLATE), ItemMatcher.NonStackableItem.LEATHER_ARMOR,
				new ItemStack(Material.LEATHER_LEGGINGS), ItemMatcher.NonStackableItem.LEATHER_ARMOR,
				new ItemStack(Material.LEATHER_BOOTS), ItemMatcher.NonStackableItem.LEATHER_ARMOR,
				new ItemStack(Material.WHITE_BED), ItemMatcher.NonStackableItem.BED,
				new ItemStack(Material.WHITE_BUNDLE), ItemMatcher.NonStackableItem.BUNDLE,
				new ItemStack(Material.SHULKER_BOX), ItemMatcher.NonStackableItem.SHULKER_BOX,
				new ItemStack(Material.WHITE_HARNESS), ItemMatcher.NonStackableItem.HARNESS
		);
		List<ItemStack> invalidMaterials = Arrays.asList(
				new ItemStack(Material.DRAGON_EGG),
				null
		);

		validMaterials.forEach((item, type) -> {
			Optional<ItemMatcher.NonStackableItem> optional = new ItemMatcher(item).searchNonStackableItem();
			Assertions.assertTrue(optional.isPresent());
			Assertions.assertEquals(type, optional.get());
		});

		for (ItemStack item : invalidMaterials) {
			Assertions.assertFalse(new ItemMatcher(item).searchNonStackableItem().isPresent());
		}
	}

	// Test if the configuration for non-stackable item's list is working properly
	@Test
	public void testNonStackableItemConfig() {
		ConfigHandler.Settings.NON_STACKABLE_ITEMS = List.of("BED", "HARNESS");

		Assertions.assertTrue(new ItemMatcher(new ItemStack(Material.WHITE_BED)).isItemDyeable());
		Assertions.assertTrue(new ItemMatcher(new ItemStack(Material.YELLOW_BED)).isItemDyeable());
		Assertions.assertTrue(new ItemMatcher(new ItemStack(Material.WHITE_HARNESS)).isItemDyeable());
		Assertions.assertFalse(new ItemMatcher(new ItemStack(Material.BUNDLE)).isItemDyeable());
		Assertions.assertFalse(new ItemMatcher(new ItemStack(Material.DRAGON_EGG)).isItemDyeable());
		Assertions.assertFalse(new ItemMatcher(null).isItemDyeable());
	}
}
