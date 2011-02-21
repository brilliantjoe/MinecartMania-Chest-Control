package com.afforess.minecartmaniachestcontrol;

import com.afforess.minecartmaniacore.config.Setting;

public class SettingList {
	public final static Setting[] config = {
		new Setting(
				"Nearby Collection Range", 
				new Integer(2), 
				"The range that Storage Minecarts will search for items to pick up, and automate farming. Negative numbers disable both settings.",
				MinecartManiaChestControl.description.getName()
		)
	};
}
