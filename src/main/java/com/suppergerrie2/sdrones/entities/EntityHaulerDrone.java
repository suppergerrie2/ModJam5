package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.AI.hauler.EntityAIBringItemHome;
import com.suppergerrie2.sdrones.entities.AI.hauler.EntityAISearchItems;

import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.world.World;

public class EntityHaulerDrone extends EntityBasicDrone {

	public EntityHaulerDrone(World worldIn) {
		//		this(worldIn, 0, 0, 0, ItemStack.EMPTY, EnumFacing.UP);
		super(worldIn);
		this.setRange(16);
	}

	//	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
	//		this(worldIn, x, y, z, spawnedWith, facing, 1);
	//	}
	//
	//	@Deprecated
	//	public EntityHaulerDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carrySize) {
	//		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
	//	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new EntityAISearchItems(this));
		this.tasks.addTask(1, new EntityAIBringItemHome(this));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
		this.tasks.addTask(2, new EntityAIGoHome(this));
	}

}
