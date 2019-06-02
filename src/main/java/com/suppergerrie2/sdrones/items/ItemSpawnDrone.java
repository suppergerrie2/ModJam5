package com.suppergerrie2.sdrones.items;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSpawnDrone<E extends EntityAbstractDrone> extends ItemDrone {

    Function<DroneSpawnData, E> droneCreator;

    public ItemSpawnDrone(String name, Function<DroneSpawnData, E> droneCreator) {
        super(name);
        this.droneCreator = droneCreator;
    }

    @Override
    public EnumActionResult onItemUse(ItemUseContext context) {

        ItemStack itemstack = context.getItem();
        World world = context.getWorld();

        if (!world.isRemote) {
            ItemStack spawnStack = itemstack.copy();
            spawnStack.setCount(1);
            BlockPos pos = context.getPos();

            DroneSpawnData spawnData = new DroneSpawnData(
                world,
                pos.getX() + context.getHitX(),
                pos.getY() + context.getHitY(), pos.getZ() +
                context.getHitZ(),
                spawnStack,
                context.getFace()
            );

            EntityAbstractDrone entityDrone = this.droneCreator.apply(spawnData);

//			DroneUpgrade.applyUpgradesFromStack(itemstack, entityDrone);
//
//			if (this.hasFilter(stack)) {
//				entityDrone.setFilter(this.getFilter(itemstack));
//			}

            world.spawnEntity(entityDrone);
        }

        if (!context.getPlayer().isCreative()) {
            itemstack.shrink(1);
        }

        return EnumActionResult.SUCCESS;
    }

    public class DroneSpawnData {

        public final World world;
        public final double x, y, z;
        public final ItemStack spawnItem;
        public final EnumFacing spawnFacing;


        DroneSpawnData(World world, double x, double y, double z, ItemStack spawnItem, EnumFacing spawnFacing) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.spawnItem = spawnItem;
            this.spawnFacing = spawnFacing;
        }
    }

}
