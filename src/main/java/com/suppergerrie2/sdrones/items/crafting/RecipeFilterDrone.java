package com.suppergerrie2.sdrones.items.crafting;

import java.util.ArrayList;
import java.util.List;

import com.suppergerrie2.sdrones.items.ItemDrone;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class RecipeFilterDrone extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	List<ItemStack> filters = new ArrayList<ItemStack>();
	ItemStack in = ItemStack.EMPTY;

	List<ItemStack> orig = new ArrayList<ItemStack>();
	
	boolean clearFilter = false;

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		filters.clear();
		orig.clear();
		in = ItemStack.EMPTY;
		clearFilter = false;

		boolean foundDrone = false;
		boolean hasOtherItem = false;

		if(inv.getStackInRowAndColumn(0, 0).getItem() instanceof ItemDrone) {
			foundDrone = true;
			in = inv.getStackInRowAndColumn(0, 0);
		}

		if(inv.getStackInRowAndColumn(2, 0).getItem() instanceof ItemDrone) {
			clearFilter = true;
			in = inv.getStackInRowAndColumn(2, 0);
		}

		for(int i = 0; i < inv.getSizeInventory(); i++) {

			if((!clearFilter&&i==0)||(clearFilter&&i==2)) 
			{
				orig.add(ItemStack.EMPTY);
				continue;
			}
			
			ItemStack stack = inv.getStackInSlot(i);
			orig.add(stack);
			if(!stack.isEmpty()&&shouldAddFilter(stack)) {
				stack = stack.copy();
				stack.setCount(1);
				filters.add(stack);
				hasOtherItem = true;
			}
		}

		return foundDrone && hasOtherItem || clearFilter;
	}

	public boolean shouldAddFilter(ItemStack stack) {
		for(ItemStack stack2 : filters) {
			if(stack.isItemEqual(stack2)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {

		if(in.isEmpty()) return ItemStack.EMPTY;		

		List<ItemStack> filters = new ArrayList<ItemStack>(this.filters);

		ItemStack item = in.copy();
		item.setCount(1);

		NBTTagList nbttaglist = new NBTTagList();

		if(!clearFilter) {

			NBTTagCompound nbt = in.getTagCompound();
			if(nbt!=null) {
				NBTTagList list = nbt.getTagList("filter", 10);

				for(int i = 0; i < list.tagCount(); i++) {
					for(ItemStack stack : filters) {
						if(!stack.isItemEqual(new ItemStack(list.getCompoundTagAt(i)))) {
							filters.add(new ItemStack(list.getCompoundTagAt(i)));
							break;
						}
					}
				}
			}

			for (ItemStack itemstack : filters)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();

				if (itemstack!=null&&!itemstack.isEmpty())
				{
					itemstack.writeToNBT(nbttagcompound);
				}

				nbttaglist.appendTag(nbttagcompound);
			}

			item.setTagInfo("filter", nbttaglist);
		} else if(filters.size()==0) {
			item.setTagCompound(null);
		} else {
			List<ItemStack> origFilter = new ArrayList<ItemStack>();

			NBTTagCompound nbt = in.getTagCompound();
			if(nbt!=null) {
				NBTTagList list = nbt.getTagList("filter", 10);

				for(int i = 0; i < list.tagCount(); i++) {
					origFilter.add(new ItemStack(list.getCompoundTagAt(i)));
				}
			}

			for(ItemStack stack : filters) {
				for(ItemStack stack2 : origFilter) {
					if(stack.isItemEqual(stack2)) {
						origFilter.remove(stack2);
						break;
					}
				}
			}

			for (ItemStack itemstack : origFilter)
			{
				NBTTagCompound nbttagcompound = new NBTTagCompound();

				if (itemstack!=null&&!itemstack.isEmpty())
				{
					itemstack.writeToNBT(nbttagcompound);
				}

				nbttaglist.appendTag(nbttagcompound);
			}

			item.setTagInfo("filter", nbttaglist);
		}

		return item;
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv)
	{
		ItemStack[] itemStacks = new ItemStack[inv.getSizeInventory()];
		for(int i = 0; i < itemStacks.length; i++) {
			itemStacks[i] = orig.get(i).copy();
			itemStacks[i].setCount(1);
		}

		return NonNullList.<ItemStack>from(ItemStack.EMPTY, itemStacks);
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height > 2;
	}

	@Override
	public boolean isDynamic() {
		return true;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return in;
	}

}
