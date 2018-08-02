package com.suppergerrie2.sdrones.upgrades;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.util.EnumActionResult;

public class TickableDroneUpgrade extends DroneUpgrade {

	public TickableDroneUpgrade(int maxLevel) {
		super(maxLevel);
	}

	public EnumActionResult tickUpgrade(EntityBasicDrone drone) {
		return EnumActionResult.PASS;
	}
	
}
