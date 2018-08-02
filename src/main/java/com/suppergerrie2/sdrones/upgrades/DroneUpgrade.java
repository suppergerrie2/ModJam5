package com.suppergerrie2.sdrones.upgrades;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.suppergerrie2.sdrones.DroneMod;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class DroneUpgrade extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<DroneUpgrade> {

	private static IForgeRegistry<DroneUpgrade> REGISTRY;

	public final int maxLevel;
	
	public DroneUpgrade(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	public EnumActionResult applyUpgrade(EntityBasicDrone drone, int level) {
		if(this.canApplyUpgrade(drone)) {
			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.FAIL;
	}

	/**
	 * Saves the upgrade to the stack's nbt, will also increment the level of this upgrade on the stack.
	 * This means that saving to a stack that already has this upgrade will increment the level.
	 * @param itemstack
	 * @return ItemStack with modified nbt
	 */
	public ItemStack saveToStack(ItemStack itemstack) {
		NBTTagCompound tag = itemstack.getOrCreateSubCompound("upgrades");

		NBTTagList names = tag.getTagList("names", Constants.NBT.TAG_STRING);

		boolean isAlreadySaved = false;
		for(int i = 0; i < names.tagCount(); i++) {
			if(names.getStringTagAt(i).equals(this.getRegistryName().toString())) {
				isAlreadySaved = true;
				break;
			}
		}
		if(!isAlreadySaved) {
			names.appendTag(new NBTTagString(this.getRegistryName().toString()));
		}
		tag.setTag("names", names);

		tag.setInteger(this.getRegistryName().toString(), Math.min(this.getLevelFromNBT(tag)+1, this.maxLevel+1));

		itemstack.setTagInfo("upgrades", tag);

		return itemstack;
	}

	public boolean canCraftUpgrade(ItemStack itemstack) {
		return this.getLevelFromNBT(itemstack.getOrCreateSubCompound("upgrades"))<this.maxLevel;
	}
	
	public boolean canApplyUpgrade(EntityBasicDrone drone) {
		return true;
	}

	@Nullable
	public static DroneUpgrade getByName(String id)
	{
		return getByName(new ResourceLocation(id));
	}

	@Nullable
	public static DroneUpgrade getByName(ResourceLocation id)
	{
		if(REGISTRY == null) {
			IForgeRegistry<DroneUpgrade> registry = GameRegistry.findRegistry(DroneUpgrade.class);

			if(registry==null) {
				return null;
			}

			REGISTRY = registry;
		}

		return REGISTRY.getValue(id);
	}

	public int getLevelFromNBT(NBTTagCompound compound) {
		if(compound.hasKey("upgrades")) {
			compound = compound.getCompoundTag("upgrades");
		} 

		if(compound.hasKey(this.getRegistryName().toString())) {
			return compound.getInteger(this.getRegistryName().toString());
		}

		return 0;
	}

	public static DroneUpgrade[] getUpgradesFromNBT(NBTTagCompound compound) {
		List<DroneUpgrade> upgrades = new ArrayList<DroneUpgrade>();

		if(compound.hasKey("upgrades")) {
			NBTTagCompound upgradesTag = compound.getCompoundTag("upgrades");

			NBTTagList list = upgradesTag.getTagList("names", Constants.NBT.TAG_STRING);
			for(int i = 0; i < list.tagCount(); i++) {
				DroneUpgrade upgrade = getByName(list.getStringTagAt(i));

				if(upgrade==null) {
					DroneMod.logger.warn("Invalid upgrade type while applying upgrades: %s!", list.getStringTagAt(i));
					continue;
				}

				upgrades.add(upgrade);
			}
		}

		return upgrades.toArray(new DroneUpgrade[upgrades.size()]);
	}

	public static void applyUpgradesFromStack(ItemStack itemstack, EntityBasicDrone entityDrone) {
		NBTTagCompound compound = itemstack.hasTagCompound()?itemstack.getTagCompound():new NBTTagCompound();

		DroneUpgrade[] upgrades = getUpgradesFromNBT(compound);

		for(DroneUpgrade upgrade : upgrades) {
			upgrade.applyUpgrade(entityDrone, upgrade.getLevelFromNBT(compound));
		}
		//		if(compound.hasKey("upgrades")) {
		//			NBTTagCompound upgradesTag = compound.getCompoundTag("upgrades");
		//			
		//			NBTTagList list = upgradesTag.getTagList("names", Constants.NBT.TAG_STRING);
		//			for(int i = 0; i < list.tagCount(); i++) {
		//				DroneUpgrade upgrade = getByName(list.getStringTagAt(i));
		//				
		//				if(upgrade==null) {
		//					DroneMod.logger.warn("Invalid upgrade type while applying upgrades: %s!", list.getStringTagAt(i));
		//					continue;
		//				}
		//				
		//				upgrade.applyUpgrade(entityDrone, upgrade.getLevelFromNBT(compound));
		//			}
		//		}
	}

	public String getDisplayName() {
		return I18n.format(this.getRegistryName().toString().replaceAll(":", "_"));
	}

}
