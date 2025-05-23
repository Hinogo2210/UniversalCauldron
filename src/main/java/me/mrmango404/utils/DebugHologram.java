//package me.mrmango404.utils;
//
//import net.md_5.bungee.api.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.Display;
//import org.bukkit.entity.TextDisplay;
//import org.bukkit.util.Transformation;
//import org.joml.AxisAngle4f;
//import org.joml.Vector3f;
//
//public class DebugHologram {
//	private static final float textScale = 0.8f;
//	private static final double heightOffset = 1.5;
//	private static final double radius = 0.5;
//
//	public static void spawn(Location location) {
//		World world = location.getWorld();
//		world.spawn(location.add(0.5, heightOffset, 0.5), TextDisplay.class, entity -> {
//			entity.setText(ChatColor.translateAlternateColorCodes('&', text));
//			entity.setBillboard(Display.Billboard.VERTICAL);
//			entity.setTransformation(
//					new Transformation(
//							new Vector3f(),
//							new AxisAngle4f(),
//							new Vector3f(textScale, textScale, textScale),
//							new AxisAngle4f()));
//		});
//	}
//
//	public static void remove(Location location) {
//		World world = location.getWorld();
//		world.getNearbyEntities(location.add(0.5, heightOffset, 0.5), radius, radius, radius).forEach(entity -> {
//			if (entity instanceof TextDisplay textDisplay) {
//				textDisplay.remove();
//			}
//		});
//	}
//
//	public static void update(Location location, String text) {
//		World world = location.getWorld();
//		world.getNearbyEntities(location.add(0.5, heightOffset, 0.5), radius, radius, radius).forEach(entity -> {
//			if (entity instanceof TextDisplay textDisplay) {
//				textDisplay.setText(ChatColor.translateAlternateColorCodes('&', text));
//			}
//		});
//
//	}
//}
