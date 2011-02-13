package com.afforess.minecartmaniachestcontrol;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.inventory.Inventory;

import com.afforess.minecartmaniacore.ItemUtils;
import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.SignUtils;

public abstract class ChestStorage {

	public static boolean doChestStorage(MinecartManiaMinecart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		Inventory inv = minecart.getInventory();
		if (inv == null) {
			return false;
		}
		boolean action = false;
		
		for (Block block : blockList) {
			if (block.getState() instanceof Chest) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 1);
				for (Sign sign : signList) {
					if (sign.getLine(0).toLowerCase().contains("collect items")) {
						sign.setLine(0, "[Collect Items]");
						for (int i = 1; i < 4; i++) {
							if (sign.getLine(i).trim().isEmpty()) {
								continue;
							}
							
							if (sign.getLine(i).toLowerCase().contains("all items")) {
								sign.setLine(i, "[All Items]");
								//Transfer as much as possible to the other chest
								for (int j = 0; j < inv.getSize(); j++) {
									if (!chest.addItem(inv.getItem(j))) {
										break;
									}
									inv.clear(j);
								}
							}
							
							Material item = ItemUtils.itemStringToMaterial(sign.getLine(i).toLowerCase());
							if (item != null) {
								if (!sign.getLine(i).contains("[")) {
									sign.setLine(i, "["+sign.getLine(i)+"]");
								}
								while (inv.contains(item)) {
									action = true;
									if (!chest.addItem(inv.getItem(inv.first(item)))) {
										break;
									}
									inv.clear(inv.first(item));
								}
							}
						}
						sign.update();
					}
				}
				chest.update();
			}
		}
		return action;
	}
	
	public static boolean doCollectParallel(MinecartManiaMinecart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		for (Block block : blockList) {
			if (block.getState() instanceof Chest) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 1);
				for (Sign sign : signList) {
					for (int i = 0; i < 4; i++) {
						if (sign.getLine(i).toLowerCase().contains("parallel")) {
							sign.setLine(i, "[Parallel]");
							sign.update();
							if (!minecart.isMovingAway(block.getLocation())) {
								if (chest.addItem(minecart.getType().getId())) {
									minecart.kill(false);
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
