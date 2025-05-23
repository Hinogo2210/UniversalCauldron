package me.mrmango404.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.HandlerList;

public class ItemDyeEvent extends CauldronEvent {

	private static final HandlerList HANDLER_LIST = new HandlerList();

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public ItemDyeEvent(Block block, TextDisplay entity, Player player) {
		super(block, entity, player, false);
	}
}
