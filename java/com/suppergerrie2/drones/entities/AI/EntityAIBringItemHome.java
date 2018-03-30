package com.suppergerrie2.drones.entities.AI;

import com.suppergerrie2.drones.DroneMod;
import com.suppergerrie2.drones.entities.EntityBasicDrone;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIBringItemHome extends EntityAIBase {

	private final EntityBasicDrone drone;
	private double speed;
	
	public EntityAIBringItemHome (EntityBasicDrone drone, double speed) {
		this.drone = drone;
		this.speed = speed;
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
		
		DroneMod.logger.info(drone.getDistanceSq(drone.getHomePosition())<1.3f);
		
		if(drone.getDistanceSq(drone.getHomePosition())<1.3f*1.3f) {
			drone.insertItems(drone.getHomePosition());
		}
	}
}
