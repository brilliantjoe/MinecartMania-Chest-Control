package com.afforess.minecartmaniachestcontrol;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.SignUtils;

public abstract class ChestStorage {
	
	public static boolean doMinecartCollection(MinecartManiaMinecart minecart) {
		if (minecart.getBlockTypeAhead() != null) {
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
					return true;
				}
			}
		}
		return false;
	}

	public static boolean doChestStorage(MinecartManiaStorageCart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		boolean action = false;
		
		for (Block block : blockList) {
			MinecartManiaInventory withdraw = (MinecartManiaInventory)minecart;
			MinecartManiaInventory deposit = null;
			if (block.getState() instanceof Chest) {
				deposit = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
			}
			else if (block.getState() instanceof Dispenser) {
				deposit = MinecartManiaWorld.getMinecartManiaDispenser((Dispenser)block.getState());
			}
			else if (block.getState() instanceof Furnace) {
				deposit = MinecartManiaWorld.getMinecartManiaFurnace((Furnace)block.getState());
			}
			if (deposit != null) {
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart.minecart.getWorld(), block.getX(), block.getY(), block.getZ(), 1);
				
				for (Sign sign : signList) {
					if (sign.getLine(0).toLowerCase().contains("collect items")) {
						sign.setLine(0, "[Collect Items]");
					}
					else if (sign.getLine(0).toLowerCase().contains("deposit items")) {
						sign.setLine(0, "[Deposit Items]");
						
						//Swap these around
						MinecartManiaInventory temp = withdraw;
						withdraw = deposit;
						deposit = temp;
					}
					else {
						continue;
					}
					
					action = InventoryUtils.doInventoryTransaction(withdraw, deposit, sign);
					
				}
			}
		}
		return action;
	}
	
	public static boolean doCollectParallel(MinecartManiaMinecart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		for (Block block : blockList) {
			if (block.getState() instanceof Chest) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 1);
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
