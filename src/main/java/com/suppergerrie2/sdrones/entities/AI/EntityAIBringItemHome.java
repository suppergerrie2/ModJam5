package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class EntityAIBringItemHome extends Goal {

    private final EntityAbstractDrone drone;

    public EntityAIBringItemHome(EntityAbstractDrone drone) {
        this.drone = drone;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        return drone.hasItems();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return drone.hasItems() && !drone.getHomePosition().withinDistance(drone.getPositionVec(), 1.3f);
    }

    @Override
    public void startExecuting() {
        moveHome();
    }

    @Override
    public void tick() {
        if(this.drone.getNavigator().noPath()) {
            moveHome();
        }

        if (drone.getHomePosition().withinDistance(drone.getPositionVec(), 1.5)) {
            drone.insertInventoryInBlock(drone.getHomePosition());
        }
    }

    private void moveHome() {
        BlockPos home = this.drone.getHomePosition();
        this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) home.distanceSq(drone.getPositionVec(), true)));
    }
}
