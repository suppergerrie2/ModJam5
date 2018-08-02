package com.suppergerrie2.sdrones.items.crafting.ingredient;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.suppergerrie2.sdrones.items.ItemDrone;

import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class IngredientDrone extends Ingredient {

	public IngredientDrone(List<ItemStack> items) {
		super(items.toArray(new ItemStack[1]));
	}
	
	public static class Factory implements IIngredientFactory {

		@Override
		public Ingredient parse(JsonContext context, JsonObject json) {

			List<ItemStack> stacks = new ArrayList<ItemStack>();

			if(JsonUtils.hasField(json, "drone_types")) {
				//			ResourceLocation entityName = new ResourceLocation(context.appendModId(JsonUtils.getString(json, "drone_type")));

				JsonArray array = JsonUtils.getJsonArray(json, "drone_types");
				for(int i = 0; i < array.size(); i++) {
					ResourceLocation entityName = new ResourceLocation(context.appendModId(array.get(i).getAsString()));
					
					if(!EntityList.isRegistered(entityName)) {
						throw new JsonSyntaxException("Unknown entity '" + entityName.toString() + "'");
					}

					ItemStack stack = new ItemStack(ItemDrone.getSpawnItemForEntity(entityName));

					if(stack.isEmpty()) {
						throw new JsonSyntaxException("No spawn item for " + entityName.toString());
					}

					stacks.add(stack);
				}
			} else {
				ItemDrone.getAllSpawnItems().forEach((i)->stacks.add(new ItemStack(i)));
			}

			return new IngredientDrone(stacks);
		}

	}
}
