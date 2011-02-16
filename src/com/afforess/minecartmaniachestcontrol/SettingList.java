package com.afforess.minecartmaniachestcontrol;

import com.afforess.minecartmaniacore.config.Setting;

public class SettingList {
	public final static Setting[] config = {
		new Setting(
				"Storage Carts Store Nearby Items", 
				Boolean.TRUE, 
				"Storage Carts will automatically grab and store any nearby items on the ground, if they have room",
				MinecartManiaChestControl.description.getName()
		),
		new Setting(
				"Nearby Items Range", 
				new Integer(2), 
				"The range to search for items to pick up. Warning: very large values may cause items to dissappear from half a map away, confusing players.",
				MinecartManiaChestControl.description.getName()
		)
	};
}
