package me.mrmango404.utils;

import me.mrmango404.UniversalCauldron;
import net.kyori.adventure.text.Component;

public final class Logger {

	public static void info(String message) {
		UniversalCauldron.getInstance().getComponentLogger().info(Component.text(message));
	}

	public static void debug(String message) {
		UniversalCauldron.getInstance().getComponentLogger().debug(Component.text(message));
	}

	public static void warn(String message) {
		UniversalCauldron.getInstance().getComponentLogger().warn(Component.text(message));
	}

	public static void error(String message) {
		UniversalCauldron.getInstance().getComponentLogger().error(Component.text(message));
	}
}
