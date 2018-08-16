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
		this.done = true;
		this.range = this.drone.getRange();
		return this.searchUnpreparedLand();
	}

	@Override
	public boolean shouldContinueExecuting() {
		boolean isCorrect = this.destroy ? this.drone.world.isAirBlock(this.destination) : !this.shouldReplace(this.destination);
		return (this.drone.getDistanceSq(this.destination) > 2 * 2 && !isCorrect);
	}

	@Override
	public void startExecuting() {
		this.drone.getNavigator().tryMoveToXYZ(this.destination.getX(), this.destination.getY(), this.destination.getZ(), this.drone.getSpeed((float) this.drone.getDistance(this.destination.getX(), this.destination.getY(), this.destination.getZ())));
	}

	@Override
	public void updateTask() {
		if (this.drone.world.getBlockState(this.drone.getPosition()).causesSuffocation()) {
			this.drone.getJumpHelper().setJumping();
		}
		if (this.drone.getDistanceSq(this.destination.add(0, 1, 0)) > 3 * 3) {
			this.drone.getNavigator().tryMoveToXYZ(this.destination.getX(), this.destination.getY(), this.destination.getZ(), this.drone.getSpeed((float) this.drone.getDistance(this.destination.getX(), this.destination.getY(), this.destination.getZ())));
		} else {
			if (this.destroy) {
				this.drone.world.destroyBlock(this.destination, true);
			} else {
				this.drone.world.setBlockState(this.destination, Blocks.DIRT.getDefaultState());
				this.drone.world.destroyBlock(this.destination.up(), true);
				this.drone.useDirt();
			}
		}
	}

	boolean destroy = false;

	boolean searchUnpreparedLand() {
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for (int xOffset = -this.range; xOffset <= this.range; xOffset++) {
			for (int zOffset = -this.range; zOffset <= this.range; zOffset++) {
				if (zOffset % this.waterDist == 0 && xOffset % this.waterDist == 0) {
					continue;
				}
				BlockPos pos = homepos.add(xOffset, -1, zOffset);

				if (this.drone.world.isAirBlock(pos) && this.drone.hasDirt()) {
					this.destination = pos;
					this.destroy = false;
					this.done = false;
					return true;
				} else if (!this.drone.world.isAirBlock(pos.up()) && !(this.drone.world.getBlockState(pos.up()).getBlock() instanceof BlockCrops)) {
					this.destination = pos.up();
					this.destroy = true;
					this.done = false;
					return true;
				} else if (this.shouldReplace(pos) && this.drone.hasDirt()) {
					this.destination = pos;
					this.destroy = true;
					this.done = false;
					return true;
				}
			}
		}

		return false;
	}

	boolean shouldReplace(BlockPos pos) {
		Block block = this.drone.world.getBlockState(pos).getBlock();
		return !(block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.GRASS);
	}

	public boolean isDone() {
		return this.done;
	}
}
