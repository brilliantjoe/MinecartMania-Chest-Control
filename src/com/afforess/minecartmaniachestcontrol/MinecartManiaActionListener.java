package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;

import com.afforess.minecartmaniacore.DirectionUtils;
import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.MinecartUtils;
import com.afforess.minecartmaniacore.event.ChestPoweredEvent;
import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;

public class MinecartManiaActionListener extends MinecartManiaListener{
	
	public void onChestPoweredEvent(ChestPoweredEvent event) {
		if (event.isPowered() && !event.isActionTaken()) {

			MinecartManiaChest chest = event.getChest();
			
			Material minecartType = ChestUtils.getMinecartType(chest);
			Location spawnLocation = ChestUtils.getSpawnLocationSignOverride(chest);
			
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.NORTH)){
				spawnLocation = new Location(chest.chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ());
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.SOUTH)){
				spawnLocation = new Location(chest.chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ());
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1, 2, DirectionUtils.CompassDirection.EAST)){
				spawnLocation = new Location(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1);
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1, 2, DirectionUtils.CompassDirection.WEST)){
				spawnLocation = new Location(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1);
			}
			if (spawnLocation != null && chest.contains(minecartType)) {
				chest.removeItem(minecartType.getId());
				event.setActionTaken(true);
				MinecartManiaWorld.spawnMinecart(chest.chest.getWorld(), spawnLocation.getBlockX(), spawnLocation.getBlockY(), spawnLocation.getBlockZ(), minecartType, chest);
			}
		}
	}
	
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			MinecartManiaMinecart minecart = event.getMinecart();
			
			boolean action = false;
			
			if (!action && minecart.getBlockTypeAhead() != null) {
				if (minecart.getBlockTypeAhead().getType().getId() == Material.CHEST.getId()) {
					MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)minecart.getBlockTypeAhead().getState());
					chest.addItem(minecart.getType().getId());
					minecart.kill(false);
					action = true;
				}
			}

			if (!action && minecart.isStorageMinecart()) {
				action = ChestStorage.doChestStorage(minecart);
			}
			
			event.setActionTaken(action);
		}
	}

}
