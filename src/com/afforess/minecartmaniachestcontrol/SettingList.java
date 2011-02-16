package com.afforess.minecartmaniachestcontrol;

import com.afforess.minecartmaniacore.config.Setting;

public class SettingList {
	public final static Setting[] config = {
		new Setting(
				"Storage Carts Store Nearby Items", 
				Boolean.TRUE, 
				"Storage Carts will automatically grab and store any nearby items on the ground, if they have room",
				MinecartManiaChestControl.description.getName()
		)
	};
}
