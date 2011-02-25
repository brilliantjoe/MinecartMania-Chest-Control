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
import com.afforess.minecartmaniacore.MinecartManiaDoubleChest;
import com.afforess.minecartmaniacore.MinecartManiaFurnace;
import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.MinecartManiaMinecart;
import com.afforess.minecartmaniacore.MinecartManiaStorageCart;
import com.afforess.minecartmaniacore.MinecartManiaWorld;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.utils.SignUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;

public abstract class ChestStorage {
	
	public static boolean doMinecartCollection(MinecartManiaMinecart minecart) {
		if (minecart.getBlockTypeAhead() != null) {
			if (minecart.getBlockTypeAhead().getType().getId() == Material.CHEST.getId()) {
				MinecartManiaChest chest = MinecartManiaWorld.getMinecartManiaChest((Chest)minecart.getBlockTypeAhead().getState());
				if (minecart instanceof MinecartManiaStorageCart) {
					MinecartManiaStorageCart storageCart = (MinecartManiaStorageCart)minecart;
					boolean failed = false;
					for (ItemStack item : storageCart.getInventory().getContents()) {
						if (!chest.addItem(item)) {
							failed = true;
							break;
						}
					}
					if (!failed) {
						storageCart.getInventory().clear();
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
				if (((MinecartManiaChest) deposit).getNeighborChest() != null) {
					deposit = new MinecartManiaDoubleChest((MinecartManiaChest) deposit, ((MinecartManiaChest) deposit).getNeighborChest());
				}
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
						action = InventoryUtils.doInventoryTransaction(withdraw, deposit, sign);
					}
					else if (sign.getLine(0).toLowerCase().contains("deposit items")) {
						sign.setLine(0, "[Deposit Items]");
						action = InventoryUtils.doInventoryTransaction(deposit, withdraw, sign);
					}
				}
			}
		}
		return action;
	}
	
	public static boolean doFurnaceStorage(MinecartManiaStorageCart minecart) {
		boolean action = false;
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		for (Block block : blockList) {
			if (block.getState() instanceof Furnace) {
				MinecartManiaFurnace furnace = MinecartManiaWorld.getMinecartManiaFurnace((Furnace)block.getState());
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(minecart.minecart.getWorld(), block.getX(), block.getY(), block.getZ(), 1);
				for (Sign sign : signList) {
					for (int i = 0; i < 4; i++) {
						String[] split = sign.getLine(i).split(":");
						if (split.length < 2) continue;
						split[0] = split[0].toLowerCase();
						Material[] materials = ItemUtils.getItemStringToMaterial(split[1]);
						if (materials == null) continue;
						for (Material m : materials) {
							MinecartManiaInventory withdraw = minecart;
							MinecartManiaInventory deposit = furnace;	
							int slot;
							if (split[0].contains("fuel")) {
								slot = 1;
							}
							else if (split[0].contains("smelt")) {
								slot = 0;
							}
							//not sure why anyone would want to use this, but whatever
							else if (split[0].contains("process")) {
								slot = 2;
							}
							else {
								continue;
							}
							if (withdraw.contains(m)) {
								if (deposit.getItem(slot) == null) {
									deposit.setItem(slot, withdraw.getItem(withdraw.first(m)));
									withdraw.setItem(withdraw.first(m), null);
									action = true;
								}
								//Merge stacks together
								else if (deposit.getItem(slot).getType() == m){
									ItemStack item = withdraw.getItem(withdraw.first(m));
									if (deposit.getItem(slot).getAmount() + item.getAmount() <= 64) {
										deposit.setItem(slot, new ItemStack(item.getTypeId(), deposit.getItem(slot).getAmount() + item.getAmount(), item.getDurability()));
										item = null;
									}
									else {
										int diff = deposit.getItem(slot).getAmount() + item.getAmount() - 64;
										deposit.setItem(slot, new ItemStack(item.getTypeId(), deposit.getItem(slot).getAmount() + item.getAmount(), item.getDurability()));
										item = new ItemStack(item.getTypeId(), diff);
									}
									withdraw.setItem(withdraw.first(m), item);
									action = true;
								}
							}
						}
						sign.setLine(i, StringUtils.addBrackets(sign.getLine(i)));
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

	public static void doItemCompression(MinecartManiaStorageCart minecart) {
		ArrayList<Block> blockList = minecart.getParallelBlocks();
		for (Block block : blockList) {
			if (block.getType() == Material.WORKBENCH) {
				ArrayList<Sign> signList = SignUtils.getAdjacentSignList(block.getWorld(), block.getX(), block.getY(), block.getZ(), 2);
				for (Sign sign : signList) {
					for (int i = 0; i < 4; i++) {
						if (sign.getLine(i).toLowerCase().contains("compress items")) { 
							sign.setLine(i, "[Compress Items]");
							sign.update();
							//TODO handling for custom recipies?
							Material[][] compressable = { {Material.IRON_INGOT, Material.GOLD_INGOT}, {Material.IRON_BLOCK , Material.GOLD_BLOCK} };
							int n = 0;
							for (Material m : compressable[0]) {
								int amt = 0;
								int slot = 0;
								for (ItemStack item : minecart.getContents()) {
									if (item != null && item.getType() == m) {
										amt += item.getAmount();
										minecart.setItem(slot, null);
									}
									slot++;
								}
								int compressedAmt = amt / 9;
								int left = amt % 9;
								while (compressedAmt > 0) {
									minecart.addItem(compressable[1][n].getId(), Math.min(64, compressedAmt));
									compressedAmt -= 64;
								}
								if (left > 0) {
									minecart.addItem(compressable[0][n].getId(), left);
								}
								
								n++;
							}
						}
					}
				}
			}
		}
	}

}
