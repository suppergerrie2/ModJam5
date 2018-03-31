package com.suppergerrie2.drones.entities;

import com.suppergerrie2.drones.entities.AI.EntityAIBringItemHome;
import com.suppergerrie2.drones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.drones.entities.AI.EntityAISearchItems;

import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityHaulerDrone extends EntityBasicDrone {

	public EntityHaulerDrone(World worldIn) {
		super(worldIn);
	}

	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, int carrySize) {
		super(worldIn, x, y, z, spawnedWith, carrySize);
	}

	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith) {
		super(worldIn, x, y, z, spawnedWith);
	}
	
	void setupAI() {
		this.tasks.addTask(0, new EntityAISearchItems(this, 1.0f));
		this.tasks.addTask(0, new EntityAIBringItemHome(this, 1.0f));
		this.tasks.addTask(1, new EntityAIWanderAvoidWater(this, 1.0f));
		this.tasks.addTask(1, new EntityAIGoHome(this, 1.0f));
	}
	
}
