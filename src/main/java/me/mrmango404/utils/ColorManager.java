package me.mrmango404.utils;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ColorManager {

	public enum DyeItemColor {
		WHITE(Material.WHITE_DYE, "WHITE", 249, 255, 254),
		ORANGE(Material.ORANGE_DYE, "ORANGE", 249, 128, 29),
		MAGENTA(Material.MAGENTA_DYE, "MAGENTA", 199, 78, 189),
		LIGHT_BLUE(Material.LIGHT_BLUE_DYE, "LIGHT_BLUE", 58, 179, 218),
		YELLOW(Material.YELLOW_DYE, "YELLOW", 254, 216, 61),
		LIME(Material.LIME_DYE, "LIME", 128, 199, 31),
		PINK(Material.PINK_DYE, "PINK", 243, 139, 170),
		GRAY(Material.GRAY_DYE, "GRAY", 71, 79, 82),
		LIGHT_GRAY(Material.LIGHT_GRAY_DYE, "LIGHT_GRAY", 157, 157, 151),
		CYAN(Material.CYAN_DYE, "CYAN", 22, 156, 156),
		PURPLE(Material.PURPLE_DYE, "PURPLE", 137, 50, 183),
		BLUE(Material.BLUE_DYE, "BLUE", 60, 68, 170),
		BROWN(Material.BROWN_DYE, "BROWN", 131, 84, 50),
		GREEN(Material.GREEN_DYE, "GREEN", 94, 124, 22),
		RED(Material.RED_DYE, "RED", 176, 46, 38),
		BLACK(Material.BLACK_DYE, "BLACK", 29, 29, 33);

		private final Material material;
		private final String colorKey;
		private final int r, g, b;
		private static final Map<Material, DyeItemColor> colors = Arrays.stream(values())
				.collect(Collectors.toMap(DyeItemColor::getMaterial, dye -> dye));

		DyeItemColor(Material material, String colorKey, int r, int g, int b) {
			this.material = material;
			this.colorKey = colorKey;
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public Material getMaterial() {
			return material;
		}

		public String getColorKey() {
			return colorKey;
		}

		public Color getColor() {
			return Color.fromRGB(r, g, b);
		}

		public static Optional<Color> fromMaterial(Material material) {
			return Optional.ofNullable(colors.get(material)).map(DyeItemColor::getColor);
		}

		public static DyeItemColor getClosestDye(Color color) {
			DyeItemColor closest = null;
			double closestDistance = 500;
			for (DyeItemColor dye : DyeItemColor.values()) {
				double distance = calColorDistance(color, dye.getColor());
				if (distance < closestDistance) {
					closestDistance = distance;
					closest = dye;
				}
			}
			return closest != null ? closest : DyeItemColor.WHITE;
		}
	}

	/**
	 * Translates hex code to ChatColor.
	 *
	 * @return Colorized text.
	 */
	public static String translateColor(String message) {
		Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			String color = message.substring(matcher.start(), matcher.end());
			message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)) + "");
			matcher = pattern.matcher(message);
		}

		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Converts Color to Hex code.
	 *
	 * @return Colorized text.
	 */
	public static String translateColor(Color color) {
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * Converts Color to ChatColor.
	 *
	 * @return Colorized text.
	 */
	public static String translateColor(Color color, String message) {
		message = "&" + translateColor(color) + message;
		return translateColor(message);
	}

	/**
	 * Extracts hex code from a string if there's any
	 *
	 * @param message String to be searched from
	 * @return An Optional String with the hex code
	 */
	public static Optional<String> hasColor(String message) {
		String result = null;
		StringBuilder resultBuilder = new StringBuilder();
		Pattern pattern = Pattern.compile("§x§[\\da-zA-Z]§[\\da-zA-Z]§[\\da-zA-Z]§[\\da-zA-Z]§[\\da-zA-Z]§[\\da-zA-Z]");
		Matcher matcher = pattern.matcher(message);

		if (matcher.find()) {
			String group = matcher.group();
			for (int i = 3; i <= 14; i += 2) {
				resultBuilder.append(group.charAt(i));
			}
			result = "#" + resultBuilder;
		}

		return Optional.ofNullable(result);
	}

	public static Color mix(Color color1, Color color2) {
		int r = (color1.getRed() + color2.getRed()) / 2;
		int g = (color1.getGreen() + color2.getGreen()) / 2;
		int b = (color1.getBlue() + color2.getBlue()) / 2;

		return Color.fromRGB(r, g, b);
	}

	public static boolean areColorsDifferent(Color color1, Color color2) {
		return calColorDistance(color1, color2) >= (ConfigHandler.Settings.ALLOW_MIXING ? 25 : 500);
	}

	private static double calColorDistance(Color color1, Color color2) {
		return Math.sqrt(
				Math.pow((color2.getRed() - color1.getRed()), 2) +
						Math.pow((color2.getGreen() - color1.getGreen()), 2) +
						Math.pow((color2.getBlue() - color1.getBlue()), 2));
	}
}
