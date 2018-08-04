package com.suppergerrie2.sdrones.entities.AI.cropfarm;

import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIPlantCrop extends EntityAIBase {

	private final EntityCropFarmDrone drone;
	int range;
	int waterDist = 9;
	
	BlockPos destination;
	
	public EntityAIPlantCrop(EntityCropFarmDrone entityCropFarmDrone) {
		this.drone = entityCropFarmDrone;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		range = drone.getRange();
		return this.searchEmptyFarmland()&&this.drone.hasSeeds()&&this.drone.aiPrepareFarmLand.isPrepared();
	}
	
	@Override
	public boolean shouldContinueExecuting() {
		return (drone.getDistanceSq(destination)>2&&drone.world.isAirBlock(destination));
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
			drone.plantSeeds(destination);
		}
	}
	
	boolean searchEmptyFarmland() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -range; xOffset <= range; xOffset++) {
			for (int zOffset = -range; zOffset <= range; zOffset++) {
				if (xOffset % waterDist == 0&&zOffset % waterDist == 0)
					continue;
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				IBlockState blockstate = drone.world.getBlockState(pos);
				if (blockstate.getBlock().equals(Blocks.FARMLAND)&&(this.drone.world.isAirBlock(pos.up())||drone.world.getBlockState(pos.up()).getBlock().isReplaceable(drone.world, pos.up()))) {
					destination = pos.up();
					return true;
				}
			}
		}

		return false;
	}
}
