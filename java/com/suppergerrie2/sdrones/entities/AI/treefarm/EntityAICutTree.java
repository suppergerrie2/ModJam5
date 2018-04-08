package com.suppergerrie2.sdrones.entities.AI.treefarm;

import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAICutTree extends EntityAIBase {

	private final EntityTreeFarmDrone drone;
	private double speed;
	int range;
	int treeDist = 5;

	BlockPos destination;
	BlockPos woodBlock;
	
	public EntityAICutTree (EntityTreeFarmDrone drone, double speed, int range) {
		this.drone = drone;
		this.speed = speed;
		this.range = range;

		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		return this.searchForDestination()&&drone.getNavigator().getPathToPos(destination)!=null;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (this.drone.world.getBlockState(woodBlock).getBlock().isWood(drone.world, woodBlock));
	}

	@Override
	public void startExecuting() {
		drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), speed);
	}

	public void updateTask() {
		if(drone.getDistanceSq(destination)>6) {
			drone.getNavigator().tryMoveToXYZ(destination.getX(), destination.getY(), destination.getZ(), speed);
		} else {
			drone.cutTree(destination);
		}
	}

	private boolean searchForDestination()
	{
		BlockPos homepos = new BlockPos(this.drone.getHomePosition());

		for(int xOffset = -range; xOffset < range; xOffset++) {
			if((homepos.getX()+xOffset)%treeDist!=0) continue;

			for(int zOffset = -range; zOffset < range; zOffset++) {
				if((homepos.getZ()+zOffset)%treeDist!=0) continue;
				for(int yOffset = -2; yOffset <= 1; yOffset++) {

					BlockPos pos = homepos.add(xOffset, yOffset, zOffset);

					if(this.drone.world.getBlockState(pos).getBlock().isWood(drone.world, pos)) {
						if(drone.getNavigator().getPathToPos(pos)==null) return false;
						
						woodBlock = pos;
						for(EnumFacing facing : EnumFacing.HORIZONTALS) {
							if(this.drone.world.isAirBlock(pos.offset(facing))) {
								pos = pos.offset(facing);
								break;
							}
						}
						
						this.destination = pos;
						return true;
					}
				}
			}
		}

		return false;
	}

}
