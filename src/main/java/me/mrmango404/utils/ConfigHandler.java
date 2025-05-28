package me.mrmango404.utils;

import me.mrmango404.UniversalCauldron;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHandler {

	public static class Settings {
		public static List<String> NON_STACKABLE_ITEMS;
		public static Map<String, Integer> STACKABLE_ITEMS;
		public static boolean REMOVE_COLOR_ON_REFILL;
		public static String WASH_ITEM;
		public static boolean ALLOW_MIXING;
		public static boolean ENABLE_SOUND_EFFECTS;
		public static boolean ENABLE_PARTICLE_EFFECTS;
		public static boolean SOLID_COLOR;
	}

	public static class Messages {
		public static String RELOADED;
		public static String UNKNOWN_USAGE;
	}

	public static void loadConfig() {
		UniversalCauldron instance = UniversalCauldron.getInstance();
		String path = "config.yml";
		File configFile = new File(instance.getDataFolder(), path);

		if (!configFile.exists()) {
			instance.saveResource(path, false);
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		ConfigurationSection section = config.getConfigurationSection("Settings.stackable-items");

		Settings.NON_STACKABLE_ITEMS = config.getStringList("Settings.non-stackable-items");
		Settings.REMOVE_COLOR_ON_REFILL = config.getBoolean("Settings.remove-color-on-refill");
		Settings.ALLOW_MIXING = config.getBoolean("Settings.allow-mixing");
		Settings.ENABLE_SOUND_EFFECTS = config.getBoolean("Settings.Effects.enable-sound");
		Settings.ENABLE_PARTICLE_EFFECTS = config.getBoolean("Settings.Effects.enable-particle");
		Settings.WASH_ITEM = config.getString("Settings.wash-item");
		Settings.SOLID_COLOR = config.getBoolean("Settings.solid-color");
		Settings.STACKABLE_ITEMS = new HashMap<>();
		if (section != null) {
			for (String item : section.getKeys(false)) {
				Settings.STACKABLE_ITEMS.put(item, section.getInt(item));
			}
		}

		Messages.RELOADED = config.getString("Messages.reloaded");
		Messages.UNKNOWN_USAGE = config.getString("Messages.unknown-usage");
	}
}
