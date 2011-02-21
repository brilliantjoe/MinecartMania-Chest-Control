package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.event.ChestPoweredEvent;
import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.minecartmaniacore.event.MinecartNearEntityEvent;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils;

public class MinecartManiaActionListener extends MinecartManiaListener{
	
	public void onChestPoweredEvent(ChestPoweredEvent event) {
		if (event.isPowered() && !event.isActionTaken()) {

			MinecartManiaChest chest = event.getChest();
			
			Material minecartType = ChestUtils.getMinecartType(chest);
			Location spawnLocation = ChestUtils.getSpawnLocationSignOverride(chest);
			CompassDirection direction = CompassDirection.NO_DIRECTION;
			
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.NORTH)){
				spawnLocation = new Location(chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ());
				direction = CompassDirection.NORTH;
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.SOUTH)){
				spawnLocation = new Location(chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ());
				direction = CompassDirection.SOUTH;
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1, 2, DirectionUtils.CompassDirection.EAST)){
				spawnLocation = new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1);
				direction = CompassDirection.EAST;
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1, 2, DirectionUtils.CompassDirection.WEST)){
				spawnLocation = new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1);
				direction = CompassDirection.WEST;
			}
			if (spawnLocation != null && chest.contains(minecartType)) {
				if (chest.removeItem(minecartType.getId())) {
					event.setActionTaken(true);
					MinecartManiaMinecart minecart = MinecartManiaWorld.spawnMinecart(spawnLocation, minecartType, chest);
					minecart.setMotion(direction, MinecartManiaWorld.getDoubleValue(MinecartManiaWorld.getConfigurationValue("Spawn At Speed")));
				}
			}
		}
	}
	
	public void onMinecartNearEntityEvent(MinecartNearEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getEntity() instanceof Item) {
			Item item = (Item)event.getEntity();
			if (event.getMinecart().isStorageMinecart()) {
				MinecartManiaStorageCart minecart = (MinecartManiaStorageCart) event.getMinecart();
				if (minecart.addItem(MinecartManiaWorld.ItemToItemStack(item))) {
					MinecartManiaWorld.kill(item);
					event.setCancelled(true);
				}
			}
		}
	}
	
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			MinecartManiaMinecart minecart = event.getMinecart();
			
			boolean action = false;
			
			if (!action) {
				action = ChestStorage.doMinecartCollection(minecart);
			}
			if (!action && minecart.isStorageMinecart()) {
				action = ChestStorage.doChestStorage((MinecartManiaStorageCart) minecart);
			}
			if (!action) {
				action = ChestStorage.doCollectParallel(minecart);
			}

			event.setActionTaken(action);
		}
	}

}
