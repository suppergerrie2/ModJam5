package com.suppergerrie2.sdrones.entities.AI.archer;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackRanged extends EntityAIBase
{
    /** The entity the AI instance has been applied to */
    private final EntityBasicDrone entityHost;
    /** The entity (as a RangedAttackMob) the AI instance has been applied to. */
    private EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int rangedAttackTime;
    private int seeTime;
    private final int attackIntervalMin;
    /** The maximum time the AI has to wait before peforming another ranged attack. */
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;

    public EntityAIAttackRanged(IRangedAttackMob attacker, int maxAttackTime, float maxAttackDistanceIn)
    {
        this(attacker, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
    }

    public EntityAIAttackRanged(IRangedAttackMob attacker, int p_i1650_4_, int maxAttackTime, float maxAttackDistanceIn)
    {
        this.rangedAttackTime = -1;

        if (!(attacker instanceof EntityBasicDrone))
        {
            throw new IllegalArgumentException("EntityAIAttackRanged requires Drone implements RangedAttackMob");
        }
        else
        {
            this.entityHost = (EntityBasicDrone)attacker;
            this.attackIntervalMin = p_i1650_4_;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean shouldContinueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    	this.entityHost.resetActiveHand();
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    public void updateTask()
    {
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
        boolean canSee = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        if(canSee != (this.seeTime>0)) {
        	this.seeTime = 0;
        }
        
        if (canSee)
        {
            ++this.seeTime;
        }
        else
        {
            --this.seeTime;
        }

        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20)
        {
            this.entityHost.getNavigator().clearPath();
        }
        else
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityHost.getSpeed());
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        
        if (this.entityHost.isHandActive())
        {
            if (!canSee && this.seeTime < -60)
            {
                this.entityHost.resetActiveHand();
            }
            else if (canSee)
            {
                int i = this.entityHost.getItemInUseMaxCount();

                if (i >= 20)
                {
                    this.entityHost.resetActiveHand();
                    ((IRangedAttackMob)this.entityHost).attackEntityWithRangedAttack(this.attackTarget, ItemBow.getArrowVelocity(i));
                    float f2 = MathHelper.sqrt(d0) / this.attackRadius;
                    this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
                }
            }
        }
        else if (--this.rangedAttackTime <= 0 && this.seeTime >= -60)
        {
            this.entityHost.setActiveHand(EnumHand.MAIN_HAND);
        }
    }
}