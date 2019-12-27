package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.EnumSet;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;

public class EntityAIGoHome extends Goal {

    private final EntityAbstractDrone drone;

    public EntityAIGoHome(EntityAbstractDrone drone) {
        this.drone = drone;
        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.TARGET));
    }

    @Override
    public boolean shouldExecute() {
        return !(this.drone.getHomePosition().withinDistance(drone.getPositionVec(), 1.5));
    }

    @Override
    public boolean shouldContinueExecuting() {
        return this.shouldExecute() && !drone.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        moveToHome();
    }

    private void moveToHome() {
        BlockPos home = this.drone.getHomePosition();

        if (this.drone.getNavigator().noPath() || !this.drone.getNavigator().getTargetPos().equals(home) || (this.drone.getNavigator().getPath() != null && this.drone.getNavigator().getPath().isFinished())) {
            this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) home.distanceSq(drone.getPositionVec(), true)));
        }
    }
}
