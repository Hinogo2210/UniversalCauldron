package me.mrmango404.cauldron;

import me.mrmango404.api.events.CauldronDyeEvent;
import me.mrmango404.utils.ColorLayerManager;
import me.mrmango404.utils.ColorManager;
import me.mrmango404.utils.PersistentDataSetter;
import me.mrmango404.utils.SpecialEffect;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.Optional;

/**
 * Responsible for cauldron dyeing.
 */
public class CauldronDyeHandler extends ICHandler {

	private final BlockData data;
	private final Material materialInHand;

	public CauldronDyeHandler(Block block, Player player) {
		super(block, player);
		this.data = block.getBlockData();
		this.materialInHand = player.getInventory().getItemInMainHand().getType();
	}

	// Dye the cauldron.
	@Override
	public void handle() {
		Color materialColor = ColorManager.DyeItemColor.fromMaterial(materialInHand).orElse(null);
		if (materialColor != null) {
			int waterLevel = ((Levelled) data).getLevel();
			Optional<TextDisplay> optional = ColorLayerManager.getEntity(blockLoc);

			if (optional.isPresent()) {
				TextDisplay entity = optional.get();
				Color entityColor = PersistentDataSetter.getColorData(entity).orElse(null);

				if (entityColor == null) {
					return;
				}

				CauldronDyeEvent customEvent = new CauldronDyeEvent(block, entity, player);
				Bukkit.getPluginManager().callEvent(customEvent);
				if (customEvent.isCancelled()) {
					return;
				}

				// Before mixing and applying the colors, make sure it’s not too similar.
				if (ColorManager.areColorsDifferent(materialColor, entityColor)) {
					Color newColor = ColorManager.mix(materialColor, entityColor);
					ColorLayerManager.update(blockLoc, newColor, waterLevel);
					consumeDye();
					new SpecialEffect(blockLoc, newColor).play(SpecialEffect.EffectType.DYE_CAULDRON);
				}
			} else {
				CauldronDyeEvent customEvent = new CauldronDyeEvent(block, null, player);
				Bukkit.getPluginManager().callEvent(customEvent);
				if (customEvent.isCancelled()) {
					return;
				}

				ColorLayerManager.spawn(blockLoc, materialColor, waterLevel);
				consumeDye();
				new SpecialEffect(blockLoc, materialColor).play(SpecialEffect.EffectType.DYE_CAULDRON);
			}
		}
	}
}
