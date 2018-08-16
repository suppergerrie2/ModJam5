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

	public EntityAIPrepareFarmland(EntityCropFarmDrone entityCropFarmDrone, EntityAIPrepareLand aiPrepareLand) {
		this.drone = entityCropFarmDrone;
		this.aiPrepareLand = aiPrepareLand;
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		this.range = this.drone.getRange();
		return this.searchUnpreparedFarm() && this.aiPrepareLand.isDone();
	}

	boolean searchUnpreparedFarm() {
		this.prepared = true;
		return (this.searchUnpreparedWater() || this.searchUnpreparedFarmland());
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (this.drone.getDistanceSq(this.destination) > 2 && !(this.isBlockPrepared(this.destination)));
	}

	@Override
	public void startExecuting() {
		this.drone.getNavigator().tryMoveToXYZ(this.destination.getX(), this.destination.getY(), this.destination.getZ(), this.drone.getSpeed((float) this.drone.getDistance(this.destination.getX(), this.destination.getY(), this.destination.getZ())));
	}

	@Override
	public void updateTask() {
		if (this.drone.isInWater() && this.drone.getRNG().nextFloat() < 0.8F) {
			this.drone.getJumpHelper().setJumping();
		}

		if (this.drone.getDistanceSq(this.destination.add(0, 1, 0)) > 1.5 * 1.5) {
			this.drone.getNavigator().tryMoveToXYZ(this.destination.getX(), this.destination.getY(), this.destination.getZ(), this.drone.getSpeed((float) this.drone.getDistance(this.destination.getX(), this.destination.getY(), this.destination.getZ())));
		} else {
			if (this.water) {
				this.drone.world.destroyBlock(this.destination, true);
				this.drone.world.setBlockState(this.destination, Blocks.WATER.getDefaultState());
			} else {
				if (this.drone.world.getBlockState(this.destination).getBlock() != Blocks.DIRT && this.drone.world.getBlockState(this.destination).getBlock() != Blocks.GRASS) {
					this.drone.world.destroyBlock(this.destination, true);
				}

				this.drone.world.setBlockState(this.destination, Blocks.FARMLAND.getDefaultState());
			}
		}
	}

	public boolean isPrepared() {
		return this.prepared;
	}

	boolean isBlockPrepared(BlockPos pos) {
		if (this.water) {
			return this.drone.world.getBlockState(pos).getBlock() == Blocks.WATER;
		} else {
			return this.drone.world.getBlockState(pos).getBlock() == Blocks.FARMLAND;
		}
	}

	boolean searchUnpreparedWater() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -this.range; xOffset < this.range; xOffset++) {
			if (xOffset % this.waterDist != 0) {
				continue;
			}

			for (int zOffset = -this.range; zOffset < this.range; zOffset++) {
				if (zOffset % this.waterDist != 0) {
					continue;
				}

				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				IBlockState blockstate = this.drone.world.getBlockState(pos);
				if (!blockstate.getBlock().equals(Blocks.WATER) && !blockstate.getBlock().equals(Blocks.FLOWING_WATER)) {
					this.destination = pos;
					this.water = true;
					this.prepared = false;
					return true;
				}
			}
		}

		return false;
	}

	boolean searchUnpreparedFarmland() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -this.range; xOffset <= this.range; xOffset++) {
			for (int zOffset = -this.range; zOffset <= this.range; zOffset++) {
				if (xOffset % this.waterDist == 0 && zOffset % this.waterDist == 0) {
					continue;
				}
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				IBlockState blockstate = this.drone.world.getBlockState(pos);
				if (!(blockstate.getBlock() == Blocks.FARMLAND)) {
					if (blockstate.getBlock() == Blocks.DIRT || blockstate.getBlock() == Blocks.GRASS) {
						this.destination = pos;
						this.water = false;
						this.prepared = false;
						return true;
					} else {
						this.prepared = false;
					}
				}
			}
		}

		return false;
	}
}
