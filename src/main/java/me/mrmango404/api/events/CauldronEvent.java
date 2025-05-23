package me.mrmango404.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class CauldronEvent extends Event implements Cancellable {

	Block block;
	Player player;
	TextDisplay entity;
	private boolean cancelled;

	public CauldronEvent(Block block, TextDisplay entity, Player player, boolean cancelled) {
		this.cancelled = cancelled;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}

	public Block getBlock() {
		return block;
	}

	public Player getPlayer() {
		return player;
	}

	public TextDisplay getEntity() {
		return entity;
	}
}
