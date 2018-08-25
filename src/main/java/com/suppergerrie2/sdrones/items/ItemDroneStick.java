package com.suppergerrie2.sdrones.items;

import java.util.List;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class ItemDroneStick extends ItemBasic {

	public ItemDroneStick(String name) {
		super(name);
	}

	public static void addSelected(EntityBasicDrone entityBasicDrone, ItemStack stack) {
		NBTTagCompound tag = stack.getOrCreateSubCompound(Reference.MODID);

		NBTTagList list = new NBTTagList();
		if (tag.hasKey("Selected")) {
			list = tag.getTagList("Selected", Constants.NBT.TAG_INT);

			for (int i = 0; i < list.tagCount(); i++) {
				if (list.getIntAt(i) == entityBasicDrone.getEntityId()) {
					return;
				}
			}
		}

		list.appendTag(new NBTTagInt(entityBasicDrone.getEntityId()));

		tag.setTag("Selected", list);
		stack.setTagInfo(Reference.MODID, tag);
	}

	public void clearSelected(ItemStack stack, World w) {
		NBTTagCompound tag = stack.getOrCreateSubCompound(Reference.MODID);

		if (tag.hasKey("Selected")) {

			NBTTagList list = tag.getTagList("Selected", Constants.NBT.TAG_INT);

			for (int i = 0; i < list.tagCount(); i++) {
				Entity e = w.getEntityByID(list.getIntAt(i));

				if (e instanceof EntityBasicDrone) {
					((EntityBasicDrone) e).setGlowing(false);
					((EntityBasicDrone) e).setSelected(false);
				}
			}

			tag.setTag("Selected", new NBTTagList());
		}

		stack.removeSubCompound(Reference.MODID);
	}

	public void setHomePos(ItemStack stack, World w, BlockPos pos, EnumFacing facing) {
		NBTTagCompound tag = stack.getOrCreateSubCompound(Reference.MODID);

		if (tag.hasKey("Selected")) {

			NBTTagList list = tag.getTagList("Selected", Constants.NBT.TAG_INT);

			for (int i = 0; i < list.tagCount(); i++) {
				Entity e = w.getEntityByID(list.getIntAt(i));

				if (e != null && e instanceof EntityBasicDrone) {
					((EntityBasicDrone) e).setHomePosAndDistance(pos, 64);
					((EntityBasicDrone) e).setHomeFacing(facing);
				} else {
					list.removeTag(i);
				}
			}

		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (playerIn.isSneaking()) {
			this.clearSelected(playerIn.getHeldItem(handIn), worldIn);
		}

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!worldIn.isRemote) {
			this.setHomePos(player.getHeldItem(hand), worldIn, pos, facing);
		}

		return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
	}

	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, worldIn, tooltip, flagIn);

		if (worldIn != null) {
			if (worldIn.getTotalWorldTime() % 200 < 50) {
				tooltip.add(I18n.format("dronestick.message.select"));
			} else if (worldIn.getTotalWorldTime() % 200 < 100) {
				tooltip.add(I18n.format("dronestick.message.deselect"));
			} else if (worldIn.getTotalWorldTime() % 200 < 150) {
				tooltip.add(I18n.format("dronestick.message.kill"));
			} else if (worldIn.getTotalWorldTime() % 200 < 200) {
				tooltip.add(I18n.format("dronestick.message.sethome"));
			}
		}
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
}
