package com.suppergerrie2.sdrones.entities.AI.treefarm;

import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;

import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIPlantSapling extends EntityAIBase {

	private final EntityTreeFarmDrone drone;
	private double speed;
	int range;
	int treeDist = 5;

	BlockPos destination;

	public EntityAIPlantSapling (EntityTreeFarmDrone drone, double speed, int range) {
		this.drone = drone;
		this.speed = speed;
		this.range = range;

		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		return drone.hasItems()&&this.searchForDestination();
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (drone.getDistanceSq(destination)>2&&Blocks.SAPLING.canPlaceBlockAt(drone.world, destination));
	}

	@Override
	public void startExecuting() {
		drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), speed);
	}

	public void updateTask() {
		if(drone.getDistanceSq(destination)>2) {
			drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), speed);
		} else {
			drone.placeSapling(destination);
		}
	}

	private boolean searchForDestination()
	{
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for(int yOffset = -2; yOffset <= 1; yOffset++) {
			for(int xOffset = -range; xOffset < range; xOffset++) {
				if(xOffset%treeDist!=0) continue;

				for(int zOffset = -range; zOffset < range; zOffset++) {
					if(zOffset%treeDist!=0) continue;

					BlockPos pos = homepos.add(xOffset, yOffset, zOffset);

					if(Blocks.SAPLING.canPlaceBlockAt(drone.world, pos)&&!(drone.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) {
						this.destination = pos;
						return true;
					}
				}
			}
		}

		return false;
	}
}
