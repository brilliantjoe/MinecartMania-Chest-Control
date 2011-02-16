package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.event.ChestPoweredEvent;
import com.afforess.minecartmaniacore.event.MinecartActionEvent;
import com.afforess.minecartmaniacore.event.MinecartManiaListener;
import com.afforess.minecartmaniacore.event.MinecartNearItemDropEvent;
import com.afforess.minecartmaniacore.utils.MinecartUtils;
import com.afforess.minecartmaniacore.utils.DirectionUtils;

public class MinecartManiaActionListener extends MinecartManiaListener{
	
	public void onChestPoweredEvent(ChestPoweredEvent event) {
		if (event.isPowered() && !event.isActionTaken()) {

			MinecartManiaChest chest = event.getChest();
			
			Material minecartType = ChestUtils.getMinecartType(chest);
			Location spawnLocation = ChestUtils.getSpawnLocationSignOverride(chest);
			
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.NORTH)){
				spawnLocation = new Location(chest.getWorld(), chest.getX() - 1, chest.getY(), chest.getZ());
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ(), 2, DirectionUtils.CompassDirection.SOUTH)){
				spawnLocation = new Location(chest.getWorld(), chest.getX() + 1, chest.getY(), chest.getZ());
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1, 2, DirectionUtils.CompassDirection.EAST)){
				spawnLocation = new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() - 1);
			}
			if (spawnLocation == null && MinecartUtils.validMinecartTrack(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1, 2, DirectionUtils.CompassDirection.WEST)){
				spawnLocation = new Location(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ() + 1);
			}
			if (spawnLocation != null && chest.contains(minecartType)) {
				if (chest.removeItem(minecartType.getId())) {
					event.setActionTaken(true);
					MinecartManiaWorld.spawnMinecart(spawnLocation, minecartType, chest);
				}
			}
		}
	}
	
	public void onMinecartNearItemDropEvent(MinecartNearItemDropEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Item item = event.getItem();
		if (event.getMinecart().isStorageMinecart() && MinecartManiaChestControl.storageCartsStoreNearbyItems()) {
			MinecartManiaStorageCart minecart = (MinecartManiaStorageCart) event.getMinecart();
			if (minecart.getInventory().addItem(MinecartManiaWorld.ItemToItemStack(item)).isEmpty()) {
				MinecartManiaWorld.kill(item);
				event.setCancelled(true);
			}
		}
	}
	
	public void onMinecartActionEvent(MinecartActionEvent event) {
		if (!event.isActionTaken()) {
			MinecartManiaMinecart minecart = event.getMinecart();
			
			boolean action = false;
			
			//Collect minecarts
			if (!action && minecart.getBlockTypeAhead() != null) {
				if (minecart.getBlockTypeAhead().getType().getId() == Material.CHEST.getId()) {
					MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)minecart.getBlockTypeAhead().getState());
					if (minecart instanceof MinecartManiaStorageCart) {
						MinecartManiaStorageCart storageCart = (MinecartManiaStorageCart)minecart;
						for (ItemStack item : storageCart.getInventory().getContents()) {
							if (!chest.addItem(item)) {
								break;
							}
						}
					}
					if (chest.addItem(minecart.getType().getId())) {
						minecart.kill(false);
						action = true;
					}
				}
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
