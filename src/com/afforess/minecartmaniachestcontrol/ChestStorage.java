package com.afforess.minecartmaniachestcontrol;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;

public abstract class ChestStorage {

	public static boolean doChestStorage(MinecartManiaStorageCart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		boolean action = false;
		
		for (Block block : blockList) {
			if (block.getState() instanceof Chest) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)block.getState());
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 1);
				
				for (Sign sign : signList) {
					MinecartManiaInventory withdraw = null;
					MinecartManiaInventory deposit = null;
					
					if (sign.getLine(0).toLowerCase().contains("collect items")) {
						sign.setLine(0, "[Collect Items]");
						withdraw = (MinecartManiaInventory)minecart;
						deposit = (MinecartManiaInventory)chest;
					}
					if (sign.getLine(0).toLowerCase().contains("deposit items")) {
						sign.setLine(0, "[Deposit Items]");
						withdraw = (MinecartManiaInventory)chest;
						deposit = (MinecartManiaInventory)minecart;
					}
					for (int i = 1; i < 4; i++) {
						if (sign.getLine(i).trim().isEmpty()) {
							continue;
						}
						if (sign.getLine(i).toLowerCase().contains("all items")) {
							sign.setLine(i, "[All Items]");
							//Transfer as much as possible
							for (int j = 0; j < withdraw.size(); j++) {
								if (!deposit.addItem(withdraw.getItem(j))) {
									break;
								}
								deposit.setItem(j, null);
							}
						}
						else {
							Material[] items = ItemUtils.getItemStringToMaterial(sign.getLine(i).toLowerCase());
							for (Material m : items) {
								if (m != null) {
									sign.setLine(i, StringUtils.addBrackets(StringUtils.removeBrackets(sign.getLine(i))));
									while (withdraw.contains(m)) {
										action = true;
										if (!deposit.addItem(withdraw.getItem(withdraw.getInventory().first(m)))) {
											break;
										}
										withdraw.setItem(withdraw.getInventory().first(m), null);
									}
								}
							}
						}
					}
					sign.update();
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
