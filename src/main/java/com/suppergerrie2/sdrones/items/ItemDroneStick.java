package com.suppergerrie2.sdrones.items;

import java.util.ArrayList;
import java.util.List;

import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;

public class ItemDroneStick extends ItemBasic {

	List<EntityBasicDrone> selected;

	public ItemDroneStick(String name) {
		super(name);
		selected = new ArrayList<EntityBasicDrone>();
	}

	public void addSelected(EntityBasicDrone drone) {
		selected.add(drone);
	}

	public void clearSelected() {
		for(EntityBasicDrone drone : selected) {
			drone.setGlowing(false);
			drone.setSelected(false);
		}
		selected.clear();
	}

	public void setHomePos(BlockPos pos, EnumFacing facing) {
		for(EntityBasicDrone drone : selected) {
			drone.setHomePosAndDistance(pos, 64);
			drone.setHomeFacing(facing);
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {

		if(!worldIn.isRemote&&playerIn.isSneaking()) {
			this.clearSelected();
		}

		System.out.println(Integer.MAX_VALUE);
		
		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {

		if(!worldIn.isRemote) {
			setHomePos(pos, facing);
		}
		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);
		
		if(worldIn!=null) {
			if(worldIn.getTotalWorldTime()%200<50) {
				tooltip.add(I18n.format("dronestick.message.select"));			
			} else if(worldIn.getTotalWorldTime()%200<100) {
				tooltip.add(I18n.format("dronestick.message.deselect"));			
			} else if(worldIn.getTotalWorldTime()%200<150) {
				tooltip.add(I18n.format("dronestick.message.kill"));			
			} else if(worldIn.getTotalWorldTime()%200<200) {
				tooltip.add(I18n.format("dronestick.message.sethome"));			
			}
		}
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

	

}
