package me.mrmango404.utils;

import me.mrmango404.UniversalCauldron;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigHandler {

	public static class Settings {
		public static List<String> DYEABLES;
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

		Settings.DYEABLES = config.getStringList("Settings.dyeables");
		Settings.ALLOW_MIXING = config.getBoolean("Settings.allow-mixing");
		Settings.ENABLE_SOUND_EFFECTS = config.getBoolean("Settings.Effects.enable-sound");
		Settings.ENABLE_PARTICLE_EFFECTS = config.getBoolean("Settings.Effects.enable-particle");
		Settings.WASH_ITEM = config.getString("Settings.wash-item");
		Settings.SOLID_COLOR = config.getBoolean("Settings.solid-color");

		Messages.RELOADED = config.getString("Messages.reloaded");
		Messages.UNKNOWN_USAGE = config.getString("Messages.unknown-usage");
	}
}
