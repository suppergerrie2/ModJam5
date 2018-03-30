package com.suppergerrie2.drones.items;

import com.suppergerrie2.drones.entities.EntityBasicDrone;
import com.suppergerrie2.drones.init.ModItems;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBasicDrone extends ItemBasic {

	public ItemBasicDrone(String name) {
		super(name);
		this.setCreativeTab(ModItems.tabDronesMod);
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
			EntityBasicDrone entitybasicdrone = new EntityBasicDrone(worldIn, pos.getX() + hitX, pos.getY() + hitY, pos.getZ() + hitZ, stack);
			worldIn.spawnEntity(entitybasicdrone);
		}

		return EnumActionResult.SUCCESS;
	}
	
	
}
