package com.suppergerrie2.sdrones.items.crafting;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.suppergerrie2.sdrones.items.ItemDrone;
import com.suppergerrie2.sdrones.items.crafting.ingredient.IngredientDrone;
import com.suppergerrie2.sdrones.upgrades.DroneUpgrade;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedDroneUpgradeRecipe extends ShapedOreRecipe {

	final DroneUpgrade upgrade;

	public ShapedDroneUpgradeRecipe(ResourceLocation group, ShapedPrimer primer, DroneUpgrade upgrade) {
		super(group, ItemStack.EMPTY, primer);
		this.upgrade = upgrade;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World world)
	{
		ItemStack drone = ItemStack.EMPTY;
		for(int i = 0; i < inv.getSizeInventory(); i++) {
			if(inv.getStackInSlot(i).getItem() instanceof ItemDrone) {
				drone = inv.getStackInSlot(i).copy();
				break;
			}
		}
		
		return upgrade.canCraftUpgrade(drone)&&super.matches(inv, world);
	}

	@Override
	@Nonnull
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inventory){ 
		ItemStack output = ItemStack.EMPTY;
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			if(inventory.getStackInSlot(i).getItem() instanceof ItemDrone) {
				output = inventory.getStackInSlot(i).copy();
				break;
			}
		}

		return upgrade.saveToStack(output); 
	}

	@Override
	@Nonnull
	public ItemStack getRecipeOutput(){
		for(Ingredient i : this.getIngredients()) {
			if(i instanceof IngredientDrone) {
				return i.getMatchingStacks()[0].setTranslatableName("upgrade_drone");
			}
		}

		return this.output; 
	}


	public static class Factory implements IRecipeFactory {

		@Override
		public IRecipe parse(JsonContext context, JsonObject json) {
			String upgrade_type = JsonUtils.getString(json, "upgrade_type");

			DroneUpgrade upgrade = DroneUpgrade.getByName(context.appendModId(upgrade_type));

			if(upgrade==null) {
				throw new JsonSyntaxException("Invalid upgrade type: " + upgrade_type);
			}

			//Add a temporary result so the ShapedOreRecipe factory can be reused and doesn't crash.
			JsonObject tempResult = new JsonObject();
			tempResult.addProperty("item", "minecraft:air");
			tempResult.addProperty("count", 1);
			json.add("result", tempResult);

			ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

			boolean hasDrone = false;
			for(Ingredient i : recipe.getIngredients()) {
				if(i instanceof IngredientDrone) {
					if(hasDrone) { 
						throw new JsonSyntaxException("Upgrade recipe can only have 1 drone ingredient, this should be the drone that will be upgraded");
					}

					hasDrone = true;
				}
			}

			if(!hasDrone) { 
				throw new JsonSyntaxException("Upgrade recipe must have 1 drone ingredient, this should be the drone that will be upgraded");
			}

			ShapedPrimer primer = new ShapedPrimer();
			primer.width = recipe.getRecipeWidth();
			primer.height = recipe.getRecipeHeight();
			primer.input = recipe.getIngredients();
			primer.mirrored = false;

			return new ShapedDroneUpgradeRecipe(new ResourceLocation(recipe.getGroup()), primer, upgrade);
		}

	}

}
