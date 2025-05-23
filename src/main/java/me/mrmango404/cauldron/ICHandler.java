package me.mrmango404.cauldron;

import me.mrmango404.utils.PermissionManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ICHandler {

	Block block;
	Location blockLoc;
	Player player;

	public ICHandler(Block block, Player player) {
		this.block = block;
		this.player = player;
		this.blockLoc = block.getLocation();
	}

	protected void consumeDye() {
		if (!PermissionManager.hasPermission(player, PermissionManager.INFINITE_DYE)) {
			ItemStack itemInHand = player.getInventory().getItemInMainHand();
			itemInHand.setAmount(itemInHand.getAmount() - 1);
		}
	}

	public abstract void handle();
}
