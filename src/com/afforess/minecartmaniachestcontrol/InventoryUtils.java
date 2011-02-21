package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class InventoryUtils {

	public static boolean doInventoryTransaction(MinecartManiaInventory withdraw, MinecartManiaInventory deposit, Sign sign) {
		boolean action = false;
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
					action = true;
					withdraw.setItem(j, null);
				}
			}
			else {
				Material[] items = ItemUtils.getItemStringToMaterial(sign.getLine(i).toLowerCase());
				for (Material m : items) {
					if (m != null) {
						sign.setLine(i, StringUtils.addBrackets(sign.getLine(i)));
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
		
		return action;
	}
}
