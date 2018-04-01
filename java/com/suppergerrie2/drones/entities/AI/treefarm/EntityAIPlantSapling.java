package com.suppergerrie2.drones.entities.AI.treefarm;

import com.suppergerrie2.drones.entities.EntityBasicDrone;

import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIPlantSapling extends EntityAIBase {

	private final EntityBasicDrone drone;
	private double speed;
	int range;
	
	//TODO: finish AI
	public EntityAIPlantSapling (EntityBasicDrone drone, double speed, int range) {
		this.drone = drone;
		this.speed = speed;
		this.range = range;
		
		this.setMutexBits(7);
	}
	
	@Override
	public boolean shouldExecute() {
		return false;
	}

}
