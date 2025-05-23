package me.mrmango404.utils;

import org.bukkit.*;

public final class SpecialEffect {

	public enum EffectType {
		DYE_CAULDRON,
		CLEAR_CAULDRON,
		DYE_ITEM,
		CLEAR_ITEM
	}

	private final World world;
	private final Location location;
	private final Color color;

	public SpecialEffect(Location location) {
		this.color = null;
		this.location = location.clone().add(0.5, 1, 0.5);
		world = location.getWorld();
	}

	public SpecialEffect(Location location, Color color) {
		this.color = color;
		this.location = location.clone().add(0.5, 1, 0.5);
		world = location.getWorld();
	}

	public void play(EffectType type) {
		switch (type) {
			case DYE_CAULDRON -> {
				dyeCauldronParticle();
				dyeCauldronSound();
			}
			case CLEAR_CAULDRON -> {
				clearCauldronSound();
			}
			case DYE_ITEM -> {
				dyeItemSound();
			}
		}
	}

	private void dyeCauldronParticle() {
		if (!isParticleSpawnable()) {
			return;
		}
		Particle.DustOptions option = new Particle.DustOptions(color, 1f);
		world.spawnParticle(Particle.DUST, location, 10, 0.2, 0.2, 0.2, option);
	}

	private void dyeCauldronSound() {
		if (!isSoundPlayable()) {
			return;
		}
		world.playSound(location, Sound.ENTITY_PLAYER_SPLASH, 1f, 1.5f);
	}

	private void dyeItemSound() {
		if (!isSoundPlayable()) {
			return;
		}
		world.playSound(location, Sound.ENTITY_PLAYER_SPLASH, 0.5f, 4f);

	}

	private void clearCauldronSound() {
		if (!isSoundPlayable()) {
			return;
		}
		world.playSound(location, Sound.ITEM_BRUSH_BRUSHING_SAND_COMPLETE, 1f, 0.7f);
	}

	private boolean isSoundPlayable() {
		return ConfigHandler.Settings.ENABLE_SOUND_EFFECTS;
	}

	private boolean isParticleSpawnable() {
		return ConfigHandler.Settings.ENABLE_PARTICLE_EFFECTS && color != null;
	}
}
