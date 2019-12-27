package com.suppergerrie2.sdrones.entities.ai.treefarm;

import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;

import java.util.EnumSet;

public class EntityAIPlantSapling extends Goal {
    private final EntityTreeFarmDrone drone;
    @SuppressWarnings("FieldCanBeLocal")
    private final int TREE_DIST = 5;
    private int range;
    private BlockPos destination;

    public EntityAIPlantSapling(EntityTreeFarmDrone drone) {
        this.drone = drone;

        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.TARGET, Flag.LOOK));
    }

    @Override
    public boolean shouldExecute() {
        range = (int) drone.getRange();
        return drone.hasItems() && this.searchForDestination();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return (drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()) > 2 && isValidPlacementLocation(destination));
    }

    @Override
    public void startExecuting() {
        float distance = (float) Math.sqrt(drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()));
        drone.getNavigator().setPath(drone.getNavigator().getPathToPos(destination, 0), drone.getSpeed(distance));
    }

    @Override
    public void tick() {
        if (drone.getDistanceSq(destination.getX() + 0.5, destination.getY(), destination.getZ()) + 0.5 > 2) {
            float distance = (float) Math.sqrt(drone.getDistanceSq(destination.getX(), destination.getY(), destination.getZ()));
            drone.getNavigator().setPath(drone.getNavigator().getPathToPos(destination, 0), drone.getSpeed(distance));
        } else {
            drone.placeSapling(destination);
        }
    }

    private boolean searchForDestination() {
        BlockPos homepos = new BlockPos(this.drone.getHomePosition());

        for (int yOffset = -2; yOffset <= 1; yOffset++) {
            for (int xOffset = -range; xOffset < range; xOffset++) {
                if (xOffset % TREE_DIST != 0) {
                    continue;
                }

                for (int zOffset = -range; zOffset < range; zOffset++) {
                    if (zOffset % TREE_DIST != 0) {
                        continue;
                    }

                    BlockPos pos = homepos.add(xOffset, yOffset, zOffset);

                    if (isValidPlacementLocation(pos)) {
                        this.destination = pos;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean isValidPlacementLocation(BlockPos pos) {

        ItemStack saplingStack = drone.getSapling();
        Item sapling = saplingStack.getItem();

        if (sapling instanceof BlockItem) {
            BlockItemUseContext context = new DirectionalPlaceContext(drone.world, pos, Direction.DOWN, saplingStack, Direction.UP);

            if (!(drone.world.getBlockState(pos).isReplaceable(context) || drone.world.isAirBlock(pos))) {
                return false;
            }

            BlockState placementState = ((BlockItem) sapling).getBlock().getStateForPlacement(context);

            if (placementState == null) {
                return false;
            }

            return canPlace(pos, placementState);
        }

        return false;
    }

    private boolean canPlace(BlockPos position, BlockState placementState) {
        ISelectionContext iselectioncontext = ISelectionContext.forEntity(drone);
        return placementState.isValidPosition(drone.world, position) && drone.world.func_217350_a(placementState, position, iselectioncontext);
    }
}
