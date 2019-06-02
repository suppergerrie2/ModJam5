package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;

public class EntityAISearchItems extends EntityAIBase {

    private static final int PICKUP_DISTANCE = 1;
    //Item entities claimed by drones and how many items they will pickup
    private static HashMap<UUID, Integer> claimedItems = new HashMap<>();
    private final EntityAbstractDrone drone;
    //Sorts the entities found so the drone goes for the closest item
    private final EntityAINearestAttackableTarget.Sorter sorter;
    private EntityItem target;

    public EntityAISearchItems(EntityAbstractDrone drone) {
        this.drone = drone;

        this.sorter = new EntityAINearestAttackableTarget.Sorter(drone);
        this.setMutexBits(0b011);
    }

    @Override
    public boolean shouldExecute() {
        if (!drone.hasSpaceInInventory()) {
            return false;
        }

        //Get all items in the drone's range with a y diff of 4
        double range = drone.getRange();
        List<EntityItem> itemsInRange = this.drone.world.getEntitiesWithinAABB(EntityItem.class, this.drone.getBoundingBox().grow(range, 4.0D, range));

        itemsInRange.sort(this.sorter);

        if (itemsInRange.isEmpty()) {
            //No items found, so we cant do anything
            return false;
        } else {
            //Now find the first item that we can pickup and isn't already fully claimed
            for (EntityItem item : itemsInRange) {
                if (item.cannotPickup()) {
                    continue; //Item didnt want to be picked up so continue
                }

                //Check if there are any items left to pickup after all drones that claimed it have picked up the item
                int itemsLeft = item.getItem().getCount();

                if (claimedItems.containsKey(item.getUniqueID())) {
                    itemsLeft -= claimedItems.get(item.getUniqueID());
                }

                //If itemsLeft > 0 then we know at least 1 item can be picked up by this drone.
                if (itemsLeft > 0 && drone.canPickupItem(item.getItem())) {
                    if (this.drone.getDistanceSq(item) < PICKUP_DISTANCE || drone.getNavigator().getPathToEntityLiving(item) != null) {
                        target = item; //We can reach it so set it as target!
                        claimedItems.put(item.getUniqueID(), Math.min(drone.getEmptySpace(), target.getItem().getCount())); //Tell the system we can pickup this many items
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (target == null) {
            return false;
        }

        //If our target is dead we remove it from the claimedItems list (Most of the times this means all items are picked up, but it could also have despawned)
        if (!target.isAlive()) {
            claimedItems.remove(target.getUniqueID());
            return false;
        }

        //We need to continue as long as the target is not null, it's alive and we can still pickup our item
//        boolean c = ;

        if (!this.drone.canPickupItem(target.getItem()) && target != null) {
//            claimedItems.remove(target.getUniqueID()); //TODO: Should this remove it from the list? This may cause drone's to think they can pick up an item they actually cant
            target = null;
            return false;
        }

        return true;
    }

    @Override
    public void startExecuting() {
        this.drone.getNavigator().tryMoveToEntityLiving(target, drone.getSpeed(drone.getDistance(target)));
    }

    @Override
    public void tick() {

        if (this.drone.getNavigator().noPath() || this.drone.getNavigator().getPath().isFinished()) {
            System.out.println("Lost path... recalculating"); //TODO: Remove debug log
            this.drone.getNavigator().tryMoveToEntityLiving(target, drone.getSpeed(drone.getDistance(target)));
        }

        if (this.drone.getDistanceSq(target) < PICKUP_DISTANCE && !target.cannotPickup()) {
            this.drone.pickupEntityItem(target);

            if (claimedItems.containsKey(target.getUniqueID()) && claimedItems.get(target.getUniqueID()) != null) {
                int count = claimedItems.get(target.getUniqueID());
                claimedItems.put(target.getUniqueID(), --count); //Decrease count because we picked up the item

                if (claimedItems.get(target.getUniqueID()) <= 0) {
                    claimedItems.remove(target.getUniqueID());
                    target = null;
                }
            }
        }
    }

}
