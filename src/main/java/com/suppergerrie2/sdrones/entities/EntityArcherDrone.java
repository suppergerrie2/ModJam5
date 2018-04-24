package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityArcherDrone extends EntityBasicDrone implements IRangedAttackMob {
	
	//TODO: Variable weapons?
	
	public EntityArcherDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
		this.setRange(16);
	}

	public EntityArcherDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	public EntityArcherDrone(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIAttackRanged(this, 1.0D, 5, 20.0f));
		this.tasks.addTask(1, new EntityAIGoHome(this, 1.0f));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityMob>(this, EntityMob.class, true, true));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntitySlime>(this, EntitySlime.class, true, true));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityShulker>(this, EntityShulker.class, true, true));
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
	
	public ItemStack getTool() {
		return new ItemStack(Items.BOW);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0)
        {
            this.heal(1.0F);
        }
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		EntityArrow arrow = new EntityTippedArrow(this.world, this);
		double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - arrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	       
		arrow.shoot(d0, d1 + d3 * 0.1D, d2, 1.5f, 0);
		this.world.spawnEntity(arrow);
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {
	}

}
