package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.ai.EntityAIBringItemHome;
import com.suppergerrie2.sdrones.entities.ai.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.ai.EntityAISearchItems;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.world.World;

public class EntityHaulerDrone extends EntityAbstractDrone {

    public EntityHaulerDrone(ItemSpawnDrone.DroneSpawnData spawnData) {
        super(ModEntities.hauler_drone, spawnData.world, spawnData.x, spawnData.y, spawnData.z, spawnData.spawnItem, spawnData.spawnFacing, 2);
    }

    public EntityHaulerDrone(World world) {
        super(ModEntities.hauler_drone, world);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(0, new EntityAISearchItems(this));
        this.tasks.addTask(1, new EntityAIBringItemHome(this));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
        this.tasks.addTask(2, new EntityAIGoHome(this));
    }

}
