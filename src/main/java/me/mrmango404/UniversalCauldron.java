package me.mrmango404;

import me.mrmango404.command.CommandReload;
import me.mrmango404.listener.Cauldron;
import me.mrmango404.utils.ConfigHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class UniversalCauldron extends JavaPlugin {

	private final int bStatsID = 25925;
	private static UniversalCauldron instance;

	@Override
	public void onEnable() {
		instance = this;
		getCommand("unicauldron").setExecutor(new CommandReload());
		getServer().getPluginManager().registerEvents(new Cauldron(), this);

		ConfigHandler.loadConfig();

		System.out.println(Bukkit.getServer().getVersion());
	}

	@Override
	public void onDisable() {
	}

	public static UniversalCauldron getInstance() {
		return instance;
	}
}
