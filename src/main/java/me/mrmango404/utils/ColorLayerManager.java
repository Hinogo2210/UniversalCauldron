package me.mrmango404.utils;

import me.mrmango404.UniversalCauldron;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Optional;

public class ColorLayerManager {

	/**
	 * Holds the y-offset values used to position the color layer in a cauldron,
	 * based on its water level.
	 */
	public enum DisplayPosition {
		EMPTY(0f),
		LOW(0.063f),
		MEDIUM(0.2505f),
		FULL(0.438f);

		private final float yOffset;

		DisplayPosition(float yOffset) {
			this.yOffset = yOffset;
		}

		public float getValue() {
			return yOffset;
		}

		public static float convert(int waterLevel) {
			return switch (waterLevel) {
				case 1 -> LOW.getValue();
				case 2 -> MEDIUM.getValue();
				case 3 -> FULL.getValue();
				default -> EMPTY.getValue();
			};
		}
	}

	private static final float xzScale = 3.6f;
	private static final float yScale = xzScale * 0.9f;
	private static final float xOffset = -0.046f;
	private static final float zOffset = 0.4f;

	public static void spawn(Location location, Color color, int waterLevel) {
		World world = location.getWorld();
		Location spawnLoc = location.clone().add(0.5, 0.5, 0.5);

		world.spawn(spawnLoc, TextDisplay.class, entity -> {
			entity.setText("⬛");
			entity.setTextOpacity((byte) 4);
			entity.setLineWidth(2);
			entity.setPersistent(true);
			setPosition(entity, waterLevel);
			setColor(entity, color);
		});
	}

	public static void update(Location location, Color color, int waterLevel) {
		getEntity(location).ifPresent(entity -> {
			setPosition(entity, waterLevel);
			setColor(entity, color);
		});
	}

	public static void teleport(Entity entity, Location location) {
		Location loc = location.clone().add(0.5, 0.5, 0.5);
		if (UniversalCauldron.isFolia()) {
			entity.teleportAsync(loc);
		} else {
			entity.teleport(loc);
		}
	}

	public static void remove(Location location) {
		getEntity(location).ifPresent(Entity::remove);
	}

	/**
	 * Retrieves the color layer (TextDisplay) from a cauldron.
	 * Only one layer is allowed, additional layers found will be removed.
	 *
	 * @param location The location of the cauldron.
	 * @return An Optional containing the first TextDisplay entity, or null if not found.
	 */
	public static Optional<TextDisplay> getEntity(Location location) {
		final double searchRadius = 0.5;

		Collection<Entity> entities = location.getWorld().getNearbyEntities(
				location.clone().add(0.5, 0.5, 0.5),
				searchRadius, searchRadius, searchRadius);

		TextDisplay first = null;

		// Only keep the first Text Display Entity and remove the rest.
		for (Entity entity : entities) {
			if (entity instanceof TextDisplay textDisplay) {
				if (first == null) {
					first = textDisplay;
				} else {
					entity.remove();
				}
			}
		}

		return Optional.ofNullable(first);
	}

	private static void setColor(TextDisplay entity, Color color) {
		int transparency = ConfigHandler.Settings.SOLID_COLOR ? 255 : 179;

		Color newColor = Color.fromARGB(transparency, color.getRed(), color.getGreen(), color.getBlue());

		entity.setBackgroundColor(newColor);
		PersistentDataSetter.storeColorData(entity, newColor);
	}

	private static void setPosition(TextDisplay entity, int waterLevel) {
		entity.setTransformation(
				new Transformation(
						new Vector3f(xOffset, DisplayPosition.convert(waterLevel), zOffset),
						new AxisAngle4f((float) -Math.toRadians(90), 1, 0, 0),
						new Vector3f(xzScale, yScale, xzScale),
						new AxisAngle4f()));
	}
}
