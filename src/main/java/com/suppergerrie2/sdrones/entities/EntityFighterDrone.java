package com.suppergerrie2.sdrones.entities;

import com.google.common.base.Predicate;
import com.suppergerrie2.sdrones.entities.ai.EntityAIGoHome;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
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

public class EntityFighterDrone extends EntityAbstractDrone {

    public EntityFighterDrone(ItemSpawnDrone.DroneSpawnData spawnData) {
        super(ModEntities.fighter_drone, spawnData.world, spawnData.x, spawnData.y, spawnData.z, spawnData.spawnItem, spawnData.spawnFacing, 0);
    }

    //TODO: Variable weapons?
    public EntityFighterDrone(World worldIn) {
        super(ModEntities.fighter_drone, worldIn);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(0, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(1, new EntityAIGoHome(this));
        this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));

        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityMob.class, 10, false, true, (Predicate<EntityLiving>) possibleTarget -> possibleTarget != null && !(possibleTarget instanceof EntityCreeper)));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntitySlime.class, false, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityShulker.class, false, true));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);

        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 7); //TODO: Change this to get the damage from the weapon
    }

    @Override
    public ItemStack getHeldItemMainhand() {
        return new ItemStack(Items.DIAMOND_SWORD); //TODO: Dont recreate the itemstack when changeable weapons are added.
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote) {
            //Drone heals 1 health (0.5 heart) every second
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                this.heal(1.0F);
            }
        }
    }

}
