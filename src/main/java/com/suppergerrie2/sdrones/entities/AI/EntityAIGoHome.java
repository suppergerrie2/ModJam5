package com.suppergerrie2.sdrones.entities.AI;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIGoHome extends EntityAIBase {

	private final EntityBasicDrone drone;
	
	public EntityAIGoHome (EntityBasicDrone drone) {
		this.drone = drone;
		this.setMutexBits(7);
	}
	
	@Override
	public boolean shouldExecute() {
		return this.drone.hasHome();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return (this.drone.hasHome()&&this.drone.getDistanceSq(this.drone.getHomePosition())>1.5*1.5);
	}
	
	@Override
	public void startExecuting() {
		BlockPos home = this.drone.getHomePosition();
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) drone.getDistance(home.getX(), home.getY(), home.getZ())));
	}
	
	@Override
	public void updateTask() {
		BlockPos home = this.drone.getHomePosition();
		this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) drone.getDistance(home.getX(), home.getY(), home.getZ())));
	}

}
