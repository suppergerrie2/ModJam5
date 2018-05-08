package com.suppergerrie2.sdrones.entities.AI.hauler;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIBringItemHome extends EntityAIBase {

	private final EntityBasicDrone drone;
	
	public EntityAIBringItemHome (EntityBasicDrone drone) {
		this.drone = drone;
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
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed());
	}
	
	@Override
	public void updateTask() {
		BlockPos home = this.drone.getHomePosition();
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed());
				
		if(drone.getDistanceSq(drone.getHomePosition())<1.5f*1.5f) {
			drone.insertItemsInBlock(drone.getHomePosition());
		}
	}
}
