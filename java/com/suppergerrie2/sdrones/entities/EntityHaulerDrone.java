package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.AI.hauler.EntityAIBringItemHome;
import com.suppergerrie2.sdrones.entities.AI.hauler.EntityAISearchItems;

import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityHaulerDrone extends EntityBasicDrone {

	//TODO: Filter?
	
	public EntityHaulerDrone(World worldIn) {
		super(worldIn);
	}

	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
	}

	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}
	
	void setupAI() {
		this.tasks.addTask(0, new EntityAISearchItems(this, 1.0f));
		this.tasks.addTask(0, new EntityAIBringItemHome(this, 1.0f));
		this.tasks.addTask(1, new EntityAIWanderAvoidWater(this, 1.0f));
		this.tasks.addTask(1, new EntityAIGoHome(this, 1.0f));
	}
	
}
