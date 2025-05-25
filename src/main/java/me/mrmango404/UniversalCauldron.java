package me.mrmango404;

import me.mrmango404.utils.ConfigHandler;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class UniversalCauldron extends JavaPlugin {

	private static UniversalCauldron instance;

	@Override
	public void onEnable() {
		instance = this;
		getCommand("unicauldron").setExecutor(new CommandReload());
		getServer().getPluginManager().registerEvents(new CauldronListener(), this);

		ConfigHandler.loadConfig();

		int bStatsID = 25925;
		new Metrics(this, bStatsID);
	}

	@Override
	public void onDisable() {
	}

	public static UniversalCauldron getInstance() {
		return instance;
	}

	public static boolean isFolia() {
		try {
			Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
