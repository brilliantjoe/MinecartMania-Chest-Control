package com.afforess.minecartmaniachestcontrol;

import org.bukkit.Material;
import org.bukkit.block.Sign;

import com.afforess.minecartmaniacore.MinecartManiaInventory;
import com.afforess.minecartmaniacore.utils.ItemUtils;
import com.afforess.minecartmaniacore.utils.StringUtils;

public class InventoryUtils {

	public static boolean doInventoryTransaction(MinecartManiaInventory withdraw, MinecartManiaInventory deposit, Sign sign) {
		boolean action = false;
		String[] lines = new String[3];
		for (int i = 1; i < 4; i++) {
			lines[i-1] = sign.getLine(i);
			if (!sign.getLine(i).trim().isEmpty())
				sign.setLine(i, StringUtils.addBrackets(sign.getLine(i)));
		}
		sign.update();
		
		Material[] items = ItemUtils.getItemStringListToMaterial(lines);
		for (Material m : items) {
			if (m != null) {
				while (withdraw.contains(m)) {
					if (deposit == null) {
						//do nothing, just remove it from the withdraw inventory
					}
					else if (!deposit.addItem(withdraw.getItem(withdraw.first(m)))) {
						break;
					}
					withdraw.setItem(withdraw.first(m), null);
					action = true;
				}
			}
		}
		return action;
	}
}
