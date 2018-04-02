package com.suppergerrie2.sdrones.items;

import com.suppergerrie2.sdrones.init.ModItems;

import net.minecraft.item.Item;

public class ItemBasic extends Item {

	public ItemBasic(String name) {
		setUnlocalizedName(name);
		setRegistryName(name);
		this.setCreativeTab(ModItems.tabDronesMod);
	}
	
}
