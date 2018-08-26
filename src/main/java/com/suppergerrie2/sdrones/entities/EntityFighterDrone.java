package com.suppergerrie2.sdrones.entities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFighterDrone extends EntityBasicDrone {

	//TODO: Variable weapons?
	public EntityFighterDrone(World worldIn) {
		super(worldIn);
	}

	@Override
	protected void initEntityAI() {
		super.initEntityAI();

		this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
		this.tasks.addTask(1, new EntityAIGoHome(this));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityMob>(this, EntityMob.class, 10, false, true, new Predicate<EntityLiving>() {

			@Override
			public boolean apply(@Nullable EntityLiving possibleTarget) {
				return possibleTarget != null && !(possibleTarget instanceof EntityCreeper);
			}
		}));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntitySlime>(this, EntitySlime.class, false, true));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<EntityShulker>(this, EntityShulker.class, false, true));
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
	}

	@Override
	public boolean attackEntityAsMob(Entity entityIn) {
		return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7);
	}

	@Override
	public ItemStack getTool() {
		return new ItemStack(Items.DIAMOND_SWORD);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();

		if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
			this.heal(1.0F);
		}
	}

}
