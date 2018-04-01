package com.suppergerrie2.drones.entities.AI.hauler;

import com.suppergerrie2.drones.entities.EntityBasicDrone;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIBringItemHome extends EntityAIBase {

	private final EntityBasicDrone drone;
	private double speed;
	
	public EntityAIBringItemHome (EntityBasicDrone drone, double speed) {
		this.drone = drone;
		this.speed = speed;
		this.setMutexBits(7);
	}
	
	@Override
	public boolean shouldExecute() {
		return drone.hasItems();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return drone.hasItems() && drone.getDistanceSq(drone.getHomePosition())>1.3f;
	}
	
	@Override
	public void startExecuting() {
		BlockPos home = this.drone.getHomePosition();
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), speed);
	}
	
	@Override
	public void updateTask() {
		BlockPos home = this.drone.getHomePosition();
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), speed);
				
		if(drone.getDistanceSq(drone.getHomePosition())<1.5f*1.5f) {
			drone.insertItems(drone.getHomePosition());
		}
	}
}
