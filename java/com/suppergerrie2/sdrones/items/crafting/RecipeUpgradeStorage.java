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

		for(int x = 0; x < inv.getWidth(); x++) {
			for(int y = 0; y < inv.getHeight(); y++) {
				if(x==y&&x==1) continue;
				if(inv.getStackInRowAndColumn(x, y).getItem()!=Item.getItemFromBlock(Blocks.CHEST)) {
					return false;
				}
			}
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
