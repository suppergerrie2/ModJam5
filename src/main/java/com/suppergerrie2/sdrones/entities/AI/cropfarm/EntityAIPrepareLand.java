package com.suppergerrie2.sdrones.entities.AI.cropfarm;

import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIPrepareLand extends EntityAIBase {

	private final EntityCropFarmDrone drone;
	int range;

	BlockPos destination;
	boolean done = false;
	
	int waterDist = 9;
	
	public EntityAIPrepareLand(EntityCropFarmDrone entityCropFarmDrone) {
		this.drone = entityCropFarmDrone;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		done = true;
		range = drone.getRange();
		return this.searchUnpreparedLand()&&drone.hasDirt();
	}

	@Override
	public boolean shouldContinueExecuting() {
		boolean isCorrect = destroy?drone.world.isAirBlock(destination):!this.shouldReplace(destination);
		return (drone.getDistanceSq(destination)>2*2&&!isCorrect);
	}

	@Override
	public void startExecuting() {
		drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed((float) drone.getDistance(destination.getX(), destination.getY(), destination.getZ())));
	}

	public void updateTask() {
		if(this.drone.world.getBlockState(drone.getPosition()).causesSuffocation()) {
			this.drone.getJumpHelper().setJumping();
		}
		if(drone.getDistanceSq(destination.add(0, 1, 0))>3*3) {
			drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed((float) drone.getDistance(destination.getX(), destination.getY(), destination.getZ())));
		} else {
			if(destroy) {
				drone.world.destroyBlock(destination, true);
			} else {
				drone.world.setBlockState(destination, Blocks.DIRT.getDefaultState());
				drone.world.destroyBlock(destination.up(), true);
				drone.useDirt();
			}
		}
	}

	boolean destroy = false;
	
	boolean searchUnpreparedLand() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -range; xOffset <= range; xOffset++) {
			for (int zOffset = -range; zOffset <= range; zOffset++) {
				if(zOffset%waterDist==0&&xOffset%waterDist==0) {
					continue;
				}
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				if (drone.world.isAirBlock(pos)) {
					destination = pos;
					destroy = false;
					done = false;
					return true;
				} else if(!drone.world.isAirBlock(pos.up())&&!(drone.world.getBlockState(pos.up()).getBlock() instanceof BlockCrops)) {
					destination = pos.up();
					destroy = true;
					done = false;
					return true;
				} else if(this.shouldReplace(pos)) {
					destination = pos;
					destroy = true;
					done = false;
					return true;
				}
			}
		}

		return false;
	}
	
	boolean shouldReplace(BlockPos pos) {
		Block block = drone.world.getBlockState(pos).getBlock();
		return !(block==Blocks.FARMLAND||block==Blocks.DIRT||block==Blocks.GRASS);
	}

	public boolean isDone() {
		return done;
	}
}
