package com.suppergerrie2.sdrones.entities.AI.cropfarm;

import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIFarmCrop extends EntityAIBase {

	private final EntityCropFarmDrone drone;
	int range;
	int waterDist = 9;
	
	BlockPos destination;
	
	public EntityAIFarmCrop(EntityCropFarmDrone entityCropFarmDrone) {
		this.drone = entityCropFarmDrone;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		range = drone.getRange();
		return this.searchGrownCrop();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return (drone.getDistanceSq(destination)>2&&!drone.world.isAirBlock(destination));
	}

	@Override
	public void startExecuting() {
		drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed((float) drone.getDistance(destination.getX(), destination.getY(), destination.getZ())));
	}

	public void updateTask() {
		if (this.drone.isInWater()&&this.drone.getRNG().nextFloat() < 0.8F)
        {
            this.drone.getJumpHelper().setJumping();
        }
		
		if(drone.getDistanceSq(destination)>2) {
			drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed((float) drone.getDistance(destination.getX(), destination.getY(), destination.getZ())));
		} else {
			drone.world.destroyBlock(destination, true);
		}
		
	}
	
	boolean searchGrownCrop() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -range; xOffset <= range; xOffset++) {
			for (int zOffset = -range; zOffset <= range; zOffset++) {
				if (xOffset % waterDist == 0&&zOffset % waterDist == 0)
					continue;
				BlockPos pos = homepos.add(xOffset, 0, zOffset);

				IBlockState blockstate = drone.world.getBlockState(pos);
				if (blockstate.getBlock() instanceof BlockCrops && ((BlockCrops)blockstate.getBlock()).isMaxAge(blockstate)) {
					destination = pos;
					return true;
				}
			}
		}

		return false;
	}

}
