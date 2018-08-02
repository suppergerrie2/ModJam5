package com.suppergerrie2.sdrones.init;

import java.util.HashSet;
import java.util.Set;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.items.ItemDroneStick;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import com.suppergerrie2.sdrones.items.crafting.RecipeFilterDrone;

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
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@Mod.EventBusSubscriber(modid=Reference.MODID)
public class ModItems {

	@ObjectHolder(value = "sdrones:item_hauler_drone")
	public static Item itemHaulerDrone;
	
	static Set<Item> itemsToRegister = new HashSet<Item>();
	
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
		itemsToRegister.add(new ItemDroneStick("drone_stick"));
	}
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		init();

		event.getRegistry().registerAll(itemsToRegister.toArray(new Item[1]));
	}
	
	@SubscribeEvent
	public static void registerRenders(ModelRegistryEvent event) {
		for(Item i : itemsToRegister) {
			registerRender(i);
		}
	}
	
	@SubscribeEvent
	public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		IRecipe recipeFilter = new RecipeFilterDrone().setRegistryName("filter_recipe");
//		IRecipe recipeStorage = new RecipeUpgradeStorage().setRegistryName("filter_storage_upgrade");
		
		event.getRegistry().registerAll(recipeFilter);
	}
	
	private static void registerRender(Item item) {
		ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
	}

	public static <T extends EntityBasicDrone> void registerItem(ItemSpawnDrone<T> itemSpawnDrone) {
		itemsToRegister.add(itemSpawnDrone);
	}
	
}
