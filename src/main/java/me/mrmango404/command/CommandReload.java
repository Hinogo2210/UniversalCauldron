package me.mrmango404.command;

import me.mrmango404.UniversalCauldron;
import me.mrmango404.utils.ColorManager;
import me.mrmango404.utils.ConfigHandler;
import me.mrmango404.utils.PermissionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandReload implements CommandExecutor, TabCompleter {
	private final UniversalCauldron instance = UniversalCauldron.getInstance();

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (!(commandSender instanceof Player player)) {
			instance.getLogger().warning("This command is only available to the player!");
			return true;
		}

		if (strings.length == 1) {
			if (strings[0].equalsIgnoreCase("reload")) {
				if (PermissionManager.hasPermission(player, PermissionManager.RELOAD)) {
					ConfigHandler.loadConfig();
					player.sendMessage(ColorManager.translateColor(ConfigHandler.Messages.RELOADED));
					return true;
				}
			}
		}

		player.sendMessage(ColorManager.translateColor(ConfigHandler.Messages.UNKNOWN_USAGE));
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

		if (!(commandSender instanceof Player player)) {
			return null;
		}

		if (strings.length == 1) {
			ArrayList<String> list = new ArrayList<>();

			if (PermissionManager.hasPermission(player, PermissionManager.RELOAD)) {
				list.add("reload");
			}

			list.removeIf(str -> !str.startsWith(strings[0]));

			return list;
		}

		return new ArrayList<>();
	}
}
