package com.suppergerrie2.sdrones.entities.AI.hauler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;

public class EntityAISearchItems extends EntityAIBase {

	private final EntityBasicDrone drone;
	private EntityAINearestAttackableTarget.Sorter sorter;
	private EntityItem target;

	static Map<EntityItem, Integer> claimedItems = new HashMap<EntityItem, Integer>(); 

//	private int waitTime;

	public EntityAISearchItems (EntityBasicDrone drone) {
		this.drone = drone;

		this.sorter = new EntityAINearestAttackableTarget.Sorter(drone);
		this.setMutexBits(7);
	}

	@Override
	public boolean shouldExecute() {
		if(!drone.canPickupItem()) {
			return false;
		}

		double range = drone.getRange();
		List<EntityItem> itemsInRange = this.drone.world.<EntityItem>getEntitiesWithinAABB(EntityItem.class, this.drone.getEntityBoundingBox().grow(range, 4.0D,  range));
		Collections.sort(itemsInRange, this.sorter);
		
		if(itemsInRange.isEmpty()) {
			return false;
		} else {
			for(EntityItem item : itemsInRange) {
				int itemsLeft = item.getItem().getCount();
				
				if(claimedItems.containsKey(item)) {
					itemsLeft-=claimedItems.get(item);
				}

				if(itemsLeft>0&&drone.canPickupItem(item.getItem())) {
					if(this.drone.getDistanceSq(item)<1||drone.getNavigator().getPathToEntityLiving(item)!=null) {

						target = item;
						claimedItems.put(item, drone.getCarrySize());
						return true;
					}
				}
			}
		}
		return false;

	}

	@Override
	public boolean shouldContinueExecuting() {
		if(target!=null&&target.isDead&&claimedItems.containsKey(target)) {
			claimedItems.remove(target);
		}
		
		boolean c = (target!=null&&!target.isDead&&this.drone.canPickupItem(target.getItem()));
		
		if(!c) {
			claimedItems.remove(target);
		}
		
		return c;
	}

	@Override
	public void startExecuting() {
		this.drone.getNavigator().tryMoveToEntityLiving(target, drone.getSpeed());
		//waitTime = 5*20;
	}

	@Override
	public void updateTask() {
		this.drone.getNavigator().tryMoveToEntityLiving(target, drone.getSpeed());
//		waitTime--;
//
//		if(waitTime<=0) {
//			claimedItems.remove(target);
//			target=null;
//			return;
//		}

		if(this.drone.getDistanceSq(target)<1&&!target.cannotPickup()) {
			this.drone.pickupEntityItem(target);

			if(claimedItems.containsKey(target)&&claimedItems.get(target)!=null) {
				int count = claimedItems.get(target);
				claimedItems.put(target, --count);
				
				if(claimedItems.get(target)<=0) {
					claimedItems.remove(target);
					target = null;
				}
			}
		}
	}

	protected double getFollowRange()
	{
		IAttributeInstance iattributeinstance = this.drone.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
		return iattributeinstance == null ? 16.0D : iattributeinstance.getAttributeValue();
	}
}
