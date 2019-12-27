package com.suppergerrie2.sdrones.entities.ai;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.Path;
import net.minecraftforge.common.util.LazyOptional;

import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityAISearchItems extends Goal {

    private static final int PICKUP_DISTANCE = 1;
    //Item entities claimed by drones and how many items they will pickup
    private static final HashMap<UUID, Integer> claimedItems = new HashMap<>();
    private static final HashMap<UUID, Long> lastCheckAttempt = new HashMap<>();

    private final EntityAbstractDrone drone;

    private ItemEntity target;
    private int amountClaimed = 0;

    public EntityAISearchItems(EntityAbstractDrone drone) {
        this.drone = drone;

        this.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.TARGET, Flag.JUMP));
    }

    @Override
    public boolean shouldExecute() {
        drone.world.getProfiler().startSection("drone_search_should_execute");
        if (!drone.hasSpaceInInventory()) {
            drone.world.getProfiler().endSection();
            return false;
        }

        drone.world.getProfiler().startSection("find_items");
        //Get all items in the drone's range with a y diff of 4
        double range = drone.getRange();

        List<ItemEntity> itemsInRange = this.drone.world.getEntitiesWithinAABB(ItemEntity.class, this.drone.getBoundingBox().grow(range, 4.0D, range));

        itemsInRange.sort((item1, item2) -> {

            double d0 = this.drone.getDistanceSq(item1);
            double d1 = this.drone.getDistanceSq(item2);

            if (d0 < d1) {
                return -1;
            } else {
                return d0 > d1 ? 1 : 0;
            }

        });

        drone.world.getProfiler().endSection();

        if (itemsInRange.isEmpty()) {
            //No items found, so we cant do anything
            drone.world.getProfiler().endSection();
            return false;
        } else {
            drone.world.getProfiler().startSection("check_items");
            //Now find the first item that we can pickup and isn't already fully claimed
            for (ItemEntity item : itemsInRange) {
                if (item.cannotPickup() || drone.world.getGameTime() - lastCheckAttempt.getOrDefault(item.getUniqueID(), 0L) < 20) {
                    continue; //Item didnt want to be picked up so continue
                }

                //Check if there are any items left to pickup after all drones that claimed it have picked up the item
                int itemsLeft = item.getItem().getCount();

                if (claimedItems.containsKey(item.getUniqueID())) {
                    itemsLeft -= claimedItems.get(item.getUniqueID());
                }

                //If itemsLeft > 0 then we know at least 1 item can be picked up by this drone.
                if (itemsLeft > 0 && drone.canPickupItem(item.getItem())) {
                    if(pathToEntity(item) == null) {
                        lastCheckAttempt.put(item.getUniqueID(), drone.world.getGameTime());

                        continue;
                    }

                    target = item; //We can reach it so set it as target!

                    int currentClaimed = claimedItems.getOrDefault(item.getUniqueID(), 0);
                    amountClaimed = Math.min(drone.getEmptySpace(), itemsLeft);
                    claimedItems.put(item.getUniqueID(), currentClaimed + amountClaimed); //Tell the system we can pickup this many items
                    drone.world.getProfiler().endSection();
                    drone.world.getProfiler().endSection();
                    return true;
                }
            }
            drone.world.getProfiler().endSection();
        }
        drone.world.getProfiler().endSection();
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (target == null) {
            return false;
        }

        //We need to continue as long as the target is not null, it's alive and we can still pickup our item
        if (!target.isAlive() || !this.drone.canPickupItem(target.getItem())) {
            unclaimTarget();
            return false;
        }

        return true;
    }

    private void unclaimTarget() {
        if(drone.world.isRemote) return;

        int totalClaimed = claimedItems.getOrDefault(target.getUniqueID(), 0);
        if(totalClaimed == amountClaimed) {
            claimedItems.remove(target.getUniqueID());
        } else {
            claimedItems.put(target.getUniqueID(), totalClaimed - amountClaimed);
        }

        target = null;
    }

    @Override
    public void startExecuting() {
        this.drone.getNavigator().setPath(pathToEntity(target), 1);
    }

    private Path pathToEntity(Entity e) {
        drone.world.getProfiler().startSection("pathToEntity");
        Path path =  drone.getNavigator().getPathToEntityLiving(e, 0);

        if(path != null && path.func_224771_h()) {
            drone.world.getProfiler().endSection();
            return path;
        }

        drone.world.getProfiler().endSection();
        return null;
    }

    @Override
    public void tick() {
        if(drone.world.isRemote) return;

        if(target == null) return;

        if (this.drone.getNavigator().noPath()) {
            unclaimTarget();
            return;
        }

        if (this.drone.getDistanceSq(target) < PICKUP_DISTANCE && !target.cannotPickup()) {
            this.drone.pickupEntityItem(target);

            if (claimedItems.containsKey(target.getUniqueID()) && claimedItems.get(target.getUniqueID()) != null) {
                int count = claimedItems.get(target.getUniqueID());
                claimedItems.put(target.getUniqueID(), --count); //Decrease count because we picked up the item
                amountClaimed--;

                if (claimedItems.get(target.getUniqueID()) <= 0) {
                    claimedItems.remove(target.getUniqueID());
                    target = null;
                }
            }
        }
    }

}
