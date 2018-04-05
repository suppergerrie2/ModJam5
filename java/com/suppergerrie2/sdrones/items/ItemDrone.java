package com.suppergerrie2.sdrones.items;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

public abstract class ItemDrone extends ItemBasic {

	public ItemDrone(String name) {
		super(name);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt!=null) {
			NBTTagList list = nbt.getTagList("filter", 10);

			ItemStack[] filter = new ItemStack[list.tagCount()];
			for(int i = 0; i < list.tagCount(); i++) {
				filter[i] = new ItemStack(list.getCompoundTagAt(i));
			}

			if(filter.length>0) {
				tooltip.add("Contains items for filter:");
				for(int i = 0; i < filter.length; i++) {
					tooltip.add(" -" + filter[i].getDisplayName());
				}
			}
		}
	}
	
	public boolean hasFilter(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt!=null) {
			NBTTagList list = nbt.getTagList("filter", 10);

			ItemStack[] filter = new ItemStack[list.tagCount()];
			for(int i = 0; i < list.tagCount(); i++) {
				filter[i] = new ItemStack(list.getCompoundTagAt(i));
				if(!filter[i].isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public List<ItemStack> getFilter(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt!=null) {
			NBTTagList list = nbt.getTagList("filter", 10);

			List<ItemStack> filter = new ArrayList<ItemStack>();
			for(int i = 0; i < list.tagCount(); i++) {
				filter.add(new ItemStack(list.getCompoundTagAt(i)));
			}
			return filter;
		}
		return new ArrayList<ItemStack>();
	}
}