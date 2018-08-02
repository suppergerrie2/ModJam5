package com.suppergerrie2.sdrones.items;

import java.util.function.Function;

import com.suppergerrie2.sdrones.DroneMod;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.upgrades.DroneUpgrade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSpawnDrone<E extends EntityBasicDrone> extends ItemDrone {

	Function<World, E> droneCreator;
	
	public ItemSpawnDrone(String name, ResourceLocation droneLocation, Function<World, E> droneCreator) {
		super(name);
		if(entityNameToItem.containsKey(droneLocation)) {
			DroneMod.logger.warn("Already have spawn item for " + droneLocation.toString());
		}
		
		entityNameToItem.put(droneLocation, this);
		this.droneCreator = droneCreator;
	}
	
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		ItemStack itemstack = player.getHeldItem(hand);

		if (!player.capabilities.isCreativeMode)
		{
			itemstack.shrink(1);
		}

		if (!worldIn.isRemote)
		{		
			ItemStack stack = itemstack.copy();
			stack.setCount(1);
			EntityBasicDrone entityDrone = droneCreator.apply(worldIn);
			entityDrone.init(pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack, facing);
			
			DroneUpgrade.applyUpgradesFromStack(itemstack, entityDrone);
			
			if(this.hasFilter(stack)) {
				entityDrone.setFilter(this.getFilter(itemstack));
			}
			
			worldIn.spawnEntity(entityDrone);
		}

		return EnumActionResult.SUCCESS;
	}

}
