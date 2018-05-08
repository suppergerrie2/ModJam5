package com.suppergerrie2.sdrones.entities.AI.cropfarm;

import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class EntityAIPrepareFarmland extends EntityAIBase {

	private final EntityCropFarmDrone drone;
	int range;
	int waterDist = 9;

	BlockPos destination;
	boolean water = true;
	boolean prepared = false;
	EntityAIPrepareLand aiPrepareLand;
	
	public EntityAIPrepareFarmland(EntityCropFarmDrone entityCropFarmDrone, int range, EntityAIPrepareLand aiPrepareLand) {
		this.drone = entityCropFarmDrone;
		this.range = range;
		this.aiPrepareLand = aiPrepareLand;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		return this.searchUnpreparedFarm()&&aiPrepareLand.isDone();
	}

	boolean searchUnpreparedFarm() {
		prepared = true;
		return (this.searchUnpreparedWater() || this.searchUnpreparedFarmland());
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (drone.getDistanceSq(destination)>2&&!(isBlockPrepared(destination)));
	}

	@Override
	public void startExecuting() {
		drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed());
	}

	public void updateTask() {
		if (this.drone.isInWater()&&this.drone.getRNG().nextFloat() < 0.8F)
        {
            this.drone.getJumpHelper().setJumping();
        }
		
		if(drone.getDistanceSq(destination.add(0, 1, 0))>1.5*1.5) {
			drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), drone.getSpeed());
		} else {
			if(water) {
				drone.world.destroyBlock(destination, true);
				drone.world.setBlockState(destination, Blocks.WATER.getDefaultState());
			} else {
				if(drone.world.getBlockState(destination).getBlock()!=Blocks.DIRT&&drone.world.getBlockState(destination).getBlock()!=Blocks.GRASS) {
					drone.world.destroyBlock(destination, true);
				}
				
				drone.world.setBlockState(destination, Blocks.FARMLAND.getDefaultState());
			}
		}
	}
	
	public boolean isPrepared() {
		return prepared;
	}
	
	boolean isBlockPrepared(BlockPos pos) {
		if(water) {
			return drone.world.getBlockState(pos).getBlock() == Blocks.WATER;
		} else {
			return drone.world.getBlockState(pos).getBlock() == Blocks.FARMLAND;
		}
	}

	boolean searchUnpreparedWater() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -range; xOffset < range; xOffset++) {
			if(xOffset%waterDist!=0) continue;
			
			for (int zOffset = -range; zOffset < range; zOffset++) {
				if(zOffset%waterDist!=0) continue;
				
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				IBlockState blockstate = drone.world.getBlockState(pos);
				if (!blockstate.getBlock().equals(Blocks.WATER)&&!blockstate.getBlock().equals(Blocks.FLOWING_WATER)) {
					destination = pos;
					water = true;
					prepared = false;
					return true;
				}
			}
		}

		return false;
	}

	boolean searchUnpreparedFarmland() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -range; xOffset <= range; xOffset++) {
			for (int zOffset = -range; zOffset <= range; zOffset++) {
				if(xOffset%waterDist==0&&zOffset%waterDist==0) continue;
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				IBlockState blockstate = drone.world.getBlockState(pos);
				if (!blockstate.getBlock().equals(Blocks.FARMLAND)) {
					destination = pos;
					water = false;
					prepared = false;
					return true;
				}
			}
		}

		return false;
	}
}
