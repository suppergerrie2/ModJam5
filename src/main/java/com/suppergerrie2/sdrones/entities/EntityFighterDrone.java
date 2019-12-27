package com.suppergerrie2.sdrones.entities;

import com.google.common.base.Predicate;
import com.suppergerrie2.sdrones.entities.ai.EntityAIGoHome;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityFighterDrone extends EntityAbstractDrone {

    public EntityFighterDrone(ItemSpawnDrone.DroneSpawnData spawnData) {
        super(ModEntities.fighter_drone, spawnData.world, spawnData.x, spawnData.y, spawnData.z, spawnData.spawnItem, spawnData.spawnFacing, 0);
    }

    //TODO: Variable weapons?
    public EntityFighterDrone(World worldIn) {
        this(ModEntities.fighter_drone, worldIn);
    }

    public EntityFighterDrone(EntityType<EntityAbstractDrone> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(1, new EntityAIGoHome(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0f));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, MonsterEntity.class, 10, false, true, (Predicate<LivingEntity>) possibleTarget -> possibleTarget != null && !(possibleTarget instanceof CreeperEntity)));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, SlimeEntity.class, false, true));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, ShulkerEntity.class, false, true));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7); //TODO: Change this to get the damage from the weapon
    }

    @Override
    @Nonnull
    public ItemStack getHeldItemMainhand() {
        return new ItemStack(Items.DIAMOND_SWORD); //TODO: Dont recreate the itemstack when changeable weapons are added.
    }

    @Override
    public void tick() {
        super.tick();

        //TODO: Dont regen during fight?
        if (!world.isRemote) {
            //Drone heals 1 health (0.5 heart) every second
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                this.heal(1.0F);
            }
        }
    }

}
