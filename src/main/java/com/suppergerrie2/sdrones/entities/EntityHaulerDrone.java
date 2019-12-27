package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.ai.EntityAIBringItemHome;
import com.suppergerrie2.sdrones.entities.ai.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.ai.EntityAISearchItems;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.world.World;

public class EntityHaulerDrone extends EntityAbstractDrone {

    public EntityHaulerDrone(ItemSpawnDrone.DroneSpawnData spawnData) {
        super(ModEntities.hauler_drone, spawnData.world, spawnData.x, spawnData.y, spawnData.z, spawnData.spawnItem, spawnData.spawnFacing, 2);
    }

    public EntityHaulerDrone(World world) {
        this(ModEntities.hauler_drone, world);
    }

    public EntityHaulerDrone(EntityType<EntityAbstractDrone> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new EntityAISearchItems(this));
        this.goalSelector.addGoal(1, new EntityAIBringItemHome(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0f));
        this.goalSelector.addGoal(2, new EntityAIGoHome(this));
    }

}
