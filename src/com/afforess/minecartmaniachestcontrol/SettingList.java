package com.afforess.minecartmaniachestcontrol;

import com.afforess.minecartmaniacore.config.Setting;

public class SettingList {
	public final static Setting[] config = {
		new Setting(
				"Nearby Collection Range", 
				new Integer(2), 
				"The range that Storage Minecarts will search for items to pick up, and automate farming. Negative numbers disable both settings.",
				MinecartManiaChestControl.description.getName()
		),
		new Setting(
				"Maximum Collection Range", 
				new Integer(25), 
				"The maximum range that Storage Minecarts will search for items to pick up, and automate farming. Used when players create signs that alter collection range",
				MinecartManiaChestControl.description.getName()
		),
		new Setting(
				"Spawn At Speed", 
				new Double(0), 
				"The speed that minecarts are spawned at. 0 by default. For reference, 0.6 is full speed (and the speed launchers launch at).",
				MinecartManiaChestControl.description.getName()
		)
	};
}
