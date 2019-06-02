package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIGoHome extends EntityAIBase {

    private final EntityAbstractDrone drone;

    public EntityAIGoHome(EntityAbstractDrone drone) {
        this.drone = drone;
        this.setMutexBits(0b011);
    }

    @Override
    public boolean shouldExecute() {
        return (this.drone.hasHome() && this.drone.getDistanceSq(this.drone.getHomePosition()) > 1.5 * 1.5);
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

        if (this.drone.getNavigator().noPath() || !this.drone.getNavigator().getTargetPos().equals(home) || this.drone.getNavigator().getPath().isFinished()) {
            this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) drone.getDistance(home.getX(), home.getY(), home.getZ())));
        }
    }
}
