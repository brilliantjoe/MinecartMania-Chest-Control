package com.afforess.minecartmaniachestcontrol;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.Item;
import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.utils.DirectionUtils.CompassDirection;
import com.afforess.minecartmaniacore.utils.SignUtils;

public abstract class ChestUtils {
	public static Item getMinecartType(MinecartManiaChest chest) {
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 2);

		boolean empty = false;
		boolean powered = false;
		boolean storage = false;
		for (Sign sign : signList) {
			if (sign.getLine(0).toLowerCase().contains("dispenser")) {
				sign.setLine(0, "[Dispenser]");
				for (int i = 1; i < 4; i++) {
					if (sign.getLine(i).toLowerCase().contains("empty")) {
						sign.setLine(i, "[Empty]");
						empty = true;
					}
					if (sign.getLine(i).toLowerCase().contains("powered")) {
						sign.setLine(i, "[Powered]");
						powered = true;
					}
					if (sign.getLine(i).toLowerCase().contains("storage")) {
						sign.setLine(i, "[Storage]");
						storage = true;
					}
				}
				sign.update();
			}
		}
		
		if (empty) {
			if (chest.contains(Item.MINECART)) {
				return Item.MINECART;
			}
		}
		if (powered) {
			if (chest.contains(Item.POWERED_MINECART)) {
				return Item.POWERED_MINECART;
			}
		}
		if (storage) {
			if (chest.contains(Item.STORAGE_MINECART)) {
				return Item.STORAGE_MINECART;
			}
		}

		//Returns standard minecart by default
		return Item.MINECART;
	}

	public static Location getSpawnLocationSignOverride(MinecartManiaChest chest) {
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.getWorld(), chest.getX(), chest.getY(), chest.getZ(), 2);
		Location spawn = chest.getChest().getBlock().getLocation();

		for (Sign sign : signList) {
			for (int i = 0; i < 4; i++) {
				if (sign.getLine(i).toLowerCase().contains("spawn north")) {
					sign.setLine(i, "[Spawn North]");
					spawn.setX(spawn.getX() - 1);
					return spawn;
				}
				if (sign.getLine(i).toLowerCase().contains("spawn east")) {
					sign.setLine(i, "[Spawn East]");
					spawn.setZ(spawn.getZ() - 1);
					return spawn;
				}
				if (sign.getLine(i).toLowerCase().contains("spawn south")) {
					sign.setLine(i, "[Spawn South]");
					spawn.setX(spawn.getX() + 1);
					return spawn;
				}
				if (sign.getLine(i).toLowerCase().contains("spawn west")) {
					sign.setLine(i, "[Spawn West]");
					spawn.setZ(spawn.getZ() + 1);
					return spawn;
				}
			}
			sign.update();
		}
		
		
		return null;
	}

	public static CompassDirection getDirection(Location loc1,	Location loc2) {
		if (loc1.getBlockX() - loc2.getBlockX() > 0) {
			return CompassDirection.NORTH;
		}
		if (loc1.getBlockX() - loc2.getBlockX() < 0) {
			return CompassDirection.SOUTH;
		}
		if (loc1.getBlockZ() - loc2.getBlockZ() > 0) {
			return CompassDirection.EAST;
		}
		if (loc1.getBlockZ() - loc2.getBlockZ() < 0) {
			return CompassDirection.WEST;
		}
		
		return CompassDirection.NO_DIRECTION;
	}

}
