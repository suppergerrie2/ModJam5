package com.suppergerrie2.drones.entities.AI;

import java.util.Collections;
import java.util.List;

import com.suppergerrie2.drones.DroneMod;
import com.suppergerrie2.drones.entities.EntityBasicDrone;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;

public class EntityAISearchItems extends EntityAIBase {

	private final EntityBasicDrone drone;
	private EntityAINearestAttackableTarget.Sorter sorter;
	private EntityItem target;
	private double speed;
	
	public EntityAISearchItems (EntityBasicDrone drone, double speed) {
		this.drone = drone;
		this.speed = speed;
		
		this.sorter = new EntityAINearestAttackableTarget.Sorter(drone);
	}
	
	@Override
	public boolean shouldExecute() {
		if(!drone.canPickupItem()) {
			return false;
		}
		
		double range = this.getFollowRange();
		List<EntityItem> itemsInRange = this.drone.world.<EntityItem>getEntitiesWithinAABB(EntityItem.class, this.drone.getEntityBoundingBox().grow(range, 4.0D,  range));
		Collections.sort(itemsInRange, this.sorter);
				
		if(itemsInRange.isEmpty()) {
			return false;
		} else {
			target = itemsInRange.get(0);
			return true;
		}
		
	}

	@Override
	public boolean shouldContinueExecuting() {
		return (target!=null&&!target.isDead);
	}
	
	@Override
	public void startExecuting() {
		this.drone.getNavigator().tryMoveToEntityLiving(target, this.speed);
	}
	
	@Override
	public void updateTask() {
		this.drone.getNavigator().tryMoveToEntityLiving(target, this.speed);
		
		if(this.drone.getDistanceSq(target)<0.5) {
			this.drone.pickupItem(target);
		}
	}
	
    protected double getFollowRange()
    {
        IAttributeInstance iattributeinstance = this.drone.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
    }
}
