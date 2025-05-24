package me.mrmango404.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum PermissionManager {
	INTERACTION("universalcauldron.use"),
	RELOAD("universalcauldron.reload"),
	INFINITE_DYE("universalcauldron.infinite.dye");

	private final String permission;

	PermissionManager(final String permission) {
		this.permission = permission;
	}

	public String get() {
		return permission;
	}

	public static boolean hasPermission(Player player, PermissionManager permission) {
		return player.hasPermission(permission.get());
	}

	public static boolean hasPermission(CommandSender sender, PermissionManager permission) {
		if (sender instanceof Player player) {
			return hasPermission(player, permission);
		}
		return true;
	}
}
