package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIBringItemHome extends EntityAIBase {

    private final EntityAbstractDrone drone;

    public EntityAIBringItemHome(EntityAbstractDrone drone) {
        this.drone = drone;
        this.setMutexBits(0b11);
    }

    @Override
    public boolean shouldExecute() {
        return drone.hasItems();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return drone.hasItems() && drone.getDistanceSq(drone.getHomePosition()) > 1.3f;
    }

    @Override
    public void startExecuting() {
        moveHome();
    }

    @Override
    public void tick() {
        if (drone.getDistanceSq(drone.getHomePosition()) < 1.5f * 1.5f) {
            drone.insertInventoryInBlock(drone.getHomePosition());
        }
    }

    private void moveHome() {
        BlockPos home = this.drone.getHomePosition();
        this.drone.getNavigator().tryMoveToXYZ(home.getX(), home.getY(), home.getZ(), drone.getSpeed((float) drone.getDistance(home.getX(), home.getY(), home.getZ())));
    }
}
