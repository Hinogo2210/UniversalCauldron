package me.mrmango404.cauldron;

import me.mrmango404.utils.ColorLayerManager;
import me.mrmango404.utils.SpecialEffect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Responsible for cauldron cleaning.
 */
public class CauldronCleanHandler extends ICHandler {

	public CauldronCleanHandler(Block block, Player player) {
		super(block, player);
	}

	@Override
	public void handle() {
		ColorLayerManager.getEntity(blockLoc).ifPresent(entity -> {
			if (isCauldronCleanEventCancelled(entity)) return;
			new SpecialEffect(blockLoc).play(SpecialEffect.EffectType.CLEAR_CAULDRON);
			ColorLayerManager.remove(blockLoc);
		});
	}
}
