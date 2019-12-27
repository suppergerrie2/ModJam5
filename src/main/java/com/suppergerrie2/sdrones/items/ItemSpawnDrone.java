package com.suppergerrie2.sdrones.items;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemSpawnDrone<E extends EntityAbstractDrone> extends ItemDrone {

    private final Function<DroneSpawnData, E> droneCreator;

    public ItemSpawnDrone(Function<DroneSpawnData, E> droneCreator) {
        super();
        this.droneCreator = droneCreator;
    }

    @Override
    @Nonnull
    public ActionResultType onItemUse(ItemUseContext context) {

        ItemStack itemstack = context.getItem();
        World world = context.getWorld();

        if (!world.isRemote) {
            ItemStack spawnStack = itemstack.copy();
            spawnStack.setCount(1);

            Vec3d hitVec = context.getHitVec();

            DroneSpawnData spawnData = new DroneSpawnData(
                world,
                hitVec.getX(),
                hitVec.getY(),
                hitVec.getZ(),
                spawnStack,
                context.getFace()
            );

            EntityAbstractDrone entityDrone = this.droneCreator.apply(spawnData);

//			DroneUpgrade.applyUpgradesFromStack(itemstack, entityDrone);
//
//			if (this.hasFilter(stack)) {
//				entityDrone.setFilter(this.getFilter(itemstack));
//			}

            world.addEntity(entityDrone);
        }

        if (context.getPlayer() == null || !context.getPlayer().isCreative()) {
            itemstack.shrink(1);
        }

        return ActionResultType.SUCCESS;
    }

    public class DroneSpawnData {

        public final World world;
        public final double x, y, z;
        public final ItemStack spawnItem;
        public final Direction spawnFacing;


        DroneSpawnData(World world, double x, double y, double z, ItemStack spawnItem, Direction spawnFacing) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.spawnItem = spawnItem;
            this.spawnFacing = spawnFacing;
        }
    }

}
