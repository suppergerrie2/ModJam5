package com.suppergerrie2.sdrones.items.crafting;

import com.suppergerrie2.sdrones.items.ItemDrone;

import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeUpgradeStorage extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

	ItemStack in = ItemStack.EMPTY;

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		in = ItemStack.EMPTY;

		boolean foundDrone = false;

		if(inv.getStackInRowAndColumn(1, 1).getItem() instanceof ItemDrone) {
			foundDrone = true;
			in = inv.getStackInRowAndColumn(1, 1).copy();
			in.setCount(1);
		}

		if(inv.getStackInRowAndColumn(0, 1).getItem()!=Item.getItemFromBlock(Blocks.CHEST)) {
			return false;
		}
		
		if(inv.getStackInRowAndColumn(2, 1).getItem()!=Item.getItemFromBlock(Blocks.CHEST)) {
			return false;
		}
		
		if(inv.getStackInRowAndColumn(1, 0).getItem()!=Item.getItemFromBlock(Blocks.CHEST)) {
			return false;
		}
		
		if(inv.getStackInRowAndColumn(1, 2).getItem()!=Item.getItemFromBlock(Blocks.CHEST)) {
			return false;
		}
		
		if(!inv.getStackInRowAndColumn(0, 0).isEmpty()||!inv.getStackInRowAndColumn(2, 0).isEmpty()||!inv.getStackInRowAndColumn(0, 2).isEmpty()||!inv.getStackInRowAndColumn(2, 2).isEmpty()) {
			return false;
		}

		return foundDrone;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		
		int upgrade = 0;
		if(in.getTagCompound()!=null) {
			upgrade = in.getTagCompound().getInteger("storageupgrade");
		}
		
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		nbttagcompound.setInteger("storageupgrade", upgrade+1);
		in.setTagCompound(nbttagcompound);
		return in;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width * height >= 9;
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
