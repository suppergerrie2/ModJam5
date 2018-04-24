package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.items.ItemCropFarmDrone;
import com.suppergerrie2.sdrones.items.ItemDroneStick;
import com.suppergerrie2.sdrones.items.ItemFighterDrone;
import com.suppergerrie2.sdrones.items.ItemHaulerDrone;
import com.suppergerrie2.sdrones.items.ItemTreeFarmDrone;
import com.suppergerrie2.sdrones.items.crafting.RecipeFilterDrone;
import com.suppergerrie2.sdrones.items.crafting.RecipeUpgradeStorage;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid=Reference.MODID)
public class ModItems {

	public static Item itemHaulerDrone;
	public static Item itemFighterDrone;
	public static Item itemTreeFarmDrone;
	public static Item itemCropFarmDrone;
	
	public static Item droneStick;
	
	public static final CreativeTabs tabDronesMod = new CreativeTabs("tabDronesMod") {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(itemHaulerDrone);
		}
		
		@Override
		public boolean hasSearchBar() {
			return true;
		}
		
	}.setBackgroundImageName("item_search.png");
	
	public static void init() {
		itemHaulerDrone = new ItemHaulerDrone("item_hauler_drone");
		itemFighterDrone = new ItemFighterDrone("item_fighter_drone");
		itemTreeFarmDrone = new ItemTreeFarmDrone("item_tree_farm_drone");
		itemCropFarmDrone = new ItemCropFarmDrone("item_crop_farm_drone");
		
		droneStick = new ItemDroneStick("drone_stick");
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		init();
		
		event.getRegistry().registerAll(itemHaulerDrone, itemFighterDrone, droneStick, itemTreeFarmDrone, itemCropFarmDrone);
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		registerRender(itemHaulerDrone);
		registerRender(itemFighterDrone);
		registerRender(itemTreeFarmDrone);
		registerRender(itemCropFarmDrone);
		
		registerRender(droneStick);
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IRecipe recipeFilter = new RecipeFilterDrone().setRegistryName("filter_recipe");
		IRecipe recipeStorage = new RecipeUpgradeStorage().setRegistryName("filter_storage_upgrade");
		
		event.getRegistry().registerAll(recipeFilter, recipeStorage);
	}
	
	private static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}
	
}
