package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Material;

import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;

public class StorageMinecartUtils {

	public static void doAutoFarm(MinecartManiaStorageCart minecart) {
		if (minecart.getDataValue("AutoHarvest") == null && minecart.getDataValue("AutoTill") == null && minecart.getDataValue("AutoSeed") == null) {
			return;
		}
		int range = minecart.getEntityDetectionRange();
		for (int dx = -(range); dx <= range; dx++){
			for (int dy = -(range); dy <= range; dy++){
				for (int dz = -(range); dz <= range; dz++){
					int x = minecart.getX() + dx;
					int y = minecart.getY() + dy;
					int z = minecart.getZ() + dz;
					int id = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y, z);
					int aboveId = MinecartManiaWorld.getBlockIdAt(minecart.minecart.getWorld(), x, y+1, z);
					if (minecart.getDataValue("AutoHarvest") != null) {
						int data = MinecartManiaWorld.getBlockDataThreadSafe(minecart.minecart.getWorld(), x, y, z);
						if (id == Material.CROPS.getId()) {
							//fully grown
							if (data == 0x7) {
								minecart.addItem(Material.WHEAT.getId());
								minecart.addItem(Material.SEEDS.getId());
								MinecartManiaWorld.setBlockAtThreadSafe(minecart.minecart.getWorld(), Material.AIR.getId(), x, y, z);
							}
						}
					}
					
					if (minecart.getDataValue("AutoSeed") != null) {
						if (id == Material.SOIL.getId()) {
							if (aboveId == Material.AIR.getId()) {
								if (minecart.removeItem(Material.SEEDS.getId())) {
									MinecartManiaWorld.setBlockAtThreadSafe(minecart.minecart.getWorld(), Material.CROPS.getId(), x, y+1, z);
								}
							}
						}
					}
					
					if (minecart.getDataValue("AutoTill") != null) {
						if (id == Material.GRASS.getId() ||  id == Material.DIRT.getId()) {
							if (aboveId == Material.AIR.getId()) {
								MinecartManiaWorld.setBlockAtThreadSafe(minecart.minecart.getWorld(), Material.SOIL.getId(), x, y, z);
							}
						}
					}
				}
			}
		}
	}

}
