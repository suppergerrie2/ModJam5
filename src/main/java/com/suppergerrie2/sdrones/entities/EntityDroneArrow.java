package com.suppergerrie2.sdrones.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

/**
 * This arrow can't hit the archer drones, this means the archer drones can be in a great group and not hit eachother.
 * This arrow also sets the hurtresistanttime of the target to 0. Cheaty right :P
 */
public class EntityDroneArrow extends EntityArrow {

	public EntityDroneArrow(World worldIn) {
		super(worldIn);
	}
	
	public EntityDroneArrow(World worldIn, EntityLivingBase shooter) {
		super(worldIn, shooter);
	}

	@Override
	protected ItemStack getArrowStack() {
		return null;
	}

	@Override
	protected void onHit(RayTraceResult raytraceResultIn) {
		Entity entity = raytraceResultIn.entityHit;
		if(entity!=null&&entity instanceof EntityArcherDrone) {
			return;
		}
		if (entity instanceof EntityLivingBase)
        {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity;
            entitylivingbase.hurtResistantTime = 0;
        }
		
		super.onHit(raytraceResultIn);
	}
	
	public void onUpdate()
    {
		super.onUpdate();
		
		if(this.inGround) {
			this.setDead();
    }
    }
}
