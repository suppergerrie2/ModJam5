package com.suppergerrie2.sdrones.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.suppergerrie2.sdrones.DroneMod;
import com.suppergerrie2.sdrones.upgrades.DroneUpgrade;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class ItemDrone extends ItemBasic {

	static Map<ResourceLocation, ItemDrone> entityNameToItem = new HashMap<ResourceLocation, ItemDrone>();

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

			//			int upgrade = nbt.getInteger("storageupgrade");
			//			tooltip.add("Storage upgrade: " + upgrade);

			DroneUpgrade[] upgrades = DroneUpgrade.getUpgradesFromNBT(nbt);

			for(DroneUpgrade upgrade : upgrades) {
				tooltip.add(I18n.format("upgrade_tooltip", upgrade.getDisplayName(), upgrade.getLevelFromNBT(nbt)));
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

	/**
	 * @Deprecated use {@link #getUpgradeLevel(ItemStack, ResourceLocation) getUpgradeLevel}
	 */
	/*@Deprecated
	public int getStorageUpgrade(ItemStack stack) {
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt!=null) {
			return nbt.getInteger("storageupgrade");
		}
		return 0;
	}*/

	public int getUpgradeLevel(ItemStack stack, ResourceLocation upgradeType) {
		if(stack.hasTagCompound()) {
			NBTTagCompound compound = stack.getTagCompound();

			DroneUpgrade upgrade = DroneUpgrade.getByName(upgradeType);

			if(upgrade==null) {
				DroneMod.logger.warn("Trying to get level for unknown upgradeType %s!", upgradeType);
				return 0;
			}

			return upgrade.getLevelFromNBT(compound);
		}

		return 0;
	}

	@Nullable
	public static ItemDrone getSpawnItemForEntity(ResourceLocation rl) {
		return entityNameToItem.get(rl);
	}

	public static List<ItemDrone> getAllSpawnItems() {
		return new ArrayList<ItemDrone>(entityNameToItem.values());
	}
}