package com.suppergerrie2.sdrones.entities.ai.treefarm;

import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.Tags;

import java.util.EnumSet;
import java.util.Iterator;

public class EntityAICutTree extends Goal {
    private final EntityTreeFarmDrone drone;

    private int range;
    @SuppressWarnings("FieldCanBeLocal")
    private int treeDist = 5;

    private BlockPos destination;
    private BlockPos woodBlock;

    public EntityAICutTree(EntityTreeFarmDrone drone) {
        this.drone = drone;

        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.TARGET, Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        range = (int) drone.getRange();
        return this.searchForDestination() && drone.getNavigator().getPathToPos(destination, 0) != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return BlockTags.LOGS.contains(this.drone.world.getBlockState(woodBlock).getBlock());
    }

    @Override
    public void startExecuting() {
        float distance = (float) Math.sqrt(drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()));
        drone.getNavigator().setPath(drone.getNavigator().getPathToPos(destination, 0), drone.getSpeed(distance));
    }

    @Override
    public void tick() {
        if (drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()) > 6) {
            float distance = (float) Math.sqrt(drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()));
            drone.getNavigator().setPath(drone.getNavigator().getPathToPos(destination, 0), drone.getSpeed(distance));
        } else {
            drone.cutTree(destination);
        }
    }

    private boolean searchForDestination() {
        BlockPos homepos = new BlockPos(this.drone.getHomePosition());

        for (int xOffset = -range; xOffset < range; xOffset++) {
            if (xOffset % treeDist != 0) {
                continue;
            }

            for (int zOffset = -range; zOffset < range; zOffset++) {
                if (zOffset % treeDist != 0) {
                    continue;
                }
                for (int yOffset = -2; yOffset <= 1; yOffset++) {

                    BlockPos pos = homepos.add(xOffset, yOffset, zOffset);

                    if (BlockTags.LOGS.contains(this.drone.world.getBlockState(pos).getBlock())) {
                        if (drone.getNavigator().getPathToPos(pos, 0) == null) {
                            return false;
                        }

                        woodBlock = pos;
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            if (this.drone.world.isAirBlock(pos.offset(direction))) {
                                pos = pos.offset(direction);
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
