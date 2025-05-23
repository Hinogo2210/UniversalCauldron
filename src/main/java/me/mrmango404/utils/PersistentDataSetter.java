package me.mrmango404.utils;

import me.mrmango404.UniversalCauldron;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Optional;

public class PersistentDataSetter {

	private static final UniversalCauldron plugin = UniversalCauldron.getInstance();
	private static final NamespacedKey COLOR = new NamespacedKey(plugin, "color");

	public static void storeColorData(TextDisplay entity, Color color) {
		PersistentDataContainer container = entity.getPersistentDataContainer();
		container.set(
				COLOR,
				PersistentDataType.LIST.listTypeFrom(PersistentDataType.INTEGER),
				List.of(color.getRed(), color.getGreen(), color.getBlue())
		);
	}

	public static boolean hasColorData(TextDisplay entity) {
		PersistentDataContainer container = entity.getPersistentDataContainer();
		return container.has(COLOR);
	}

//	public static boolean isDyed(TextDisplay entity) {
//		PersistentDataContainer container = entity.getPersistentDataContainer();
//		if (hasColorData(entity)) {
//			return container.get(IS_DYED, PersistentDataType.INTEGER) == 1;
//		}
//		return false;
//	}

	public static Optional<Color> getColorData(TextDisplay entity) {
		PersistentDataContainer container = entity.getPersistentDataContainer();
		List<Integer> rgb = container.get(COLOR, PersistentDataType.LIST.integers());
		return Optional.of(Color.fromRGB(rgb.get(0), rgb.get(1), rgb.get(2)));
	}

	public static void removeColorData(TextDisplay entity) {
		PersistentDataContainer container = entity.getPersistentDataContainer();
		if (hasColorData(entity)) {
			container.remove(COLOR);
		}
	}
}
