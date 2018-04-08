package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EntityFighterDrone extends EntityBasicDrone {
	
	//TODO: Variable weapons?
	
	public EntityFighterDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
		this.setRange(16);
	}

	public EntityFighterDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	public EntityFighterDrone(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(1, new EntityAIGoHome(this, 1.0f));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityMob>(this, EntityMob.class, true, true));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
	}	

	@Override
	public boolean attackEntityAsMob(Entity entityIn)
	{
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7);
	}
	
	public ItemStack getWeapon() {
		return new ItemStack(Items.DIAMOND_SWORD);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0)
        {
            this.heal(1.0F);
        }
	}

}
