package com.afforess.minecartmaniachestcontrol;

import java.util.ArrayList;

import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.MinecartManiaChest;
import com.afforess.minecartmaniacore.utils.SignUtils;

public class SignCommands {
	
	public static boolean isNoCollection(MinecartManiaChest chest) {
		ArrayList<Sign> signList = SignUtils.getAdjacentSignList(chest.getLocation(), 2);
		for (Sign sign : signList) {
			for (int i = 0; i < 4; i++) {
				if (sign.getLine(i).toLowerCase().contains("no collection")) {
					sign.setLine(i, "[No Collection]");
					sign.update();
					return true;
				}
			}
		}
		return false;
	}

}
