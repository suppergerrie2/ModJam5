package com.suppergerrie2.drones.entities;

import org.apache.commons.lang3.tuple.Pair;

import com.suppergerrie2.drones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.drones.entities.AI.treefarm.EntityAIPlantSapling;
import com.suppergerrie2.drones.networking.DronesPacketHandler;
import com.suppergerrie2.drones.networking.ItemsInDroneMessage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class EntityTreeFarmDrone extends EntityBasicDrone {

	public EntityTreeFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing,
			int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
	}

	public EntityTreeFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	public EntityTreeFarmDrone(World worldIn) {
		this(worldIn, -1, -1, -1, ItemStack.EMPTY, EnumFacing.UP);
	}

	@Override
	void setupAI() {
		this.tasks.addTask(0, new EntityAIPlantSapling(this, 1.0f, 16));
		this.tasks.addTask(1, new EntityAIGoHome(this, 1.0f));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
	}

	public ItemStack getWeapon() {
		return new ItemStack(Items.DIAMOND_AXE);
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		
		if(!this.world.isRemote&&!this.hasSapling()) {
			this.tryGetSapling(this.getHomePosition());
		}
	}
	
	boolean hasSapling() {
		for(ItemStack stack : itemStacksInDrone) {
			if(stack!=null&&!stack.isEmpty()) {
				if(Block.getBlockFromItem(stack.getItem()) instanceof BlockSapling) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean tryGetSapling(BlockPos pos) {
		
		if(!this.canPickupItem()) {
			return false;
		}
				
		IBlockState iblockstate = world.getBlockState(pos);
		if(iblockstate.getBlock() instanceof BlockContainer) {
			Pair<IItemHandler, Object> destinationResult = VanillaInventoryCodeHooks.getItemHandler(world, pos.getX(), pos.getY(), pos.getZ(), EnumFacing.DOWN);
			if(destinationResult==null) {
				return false;
			} 

			IItemHandler itemHandler = destinationResult.getKey();
			
			ItemStack pickedUp = this.tryGetSaplingFromInventory(itemHandler);
			
			for(int i = 0; i < itemStacksInDrone.length; i++) {
				if(itemStacksInDrone[i]==null||itemStacksInDrone[i].isEmpty()) {
					itemStacksInDrone[i] = pickedUp;
					DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(itemStacksInDrone, this.getEntityId()));
					return true;
				} else if(itemStacksInDrone[i]!=null&&this.couldFitItem(pickedUp, itemStacksInDrone[i])) {
					int count = itemStacksInDrone[i].getCount();
					count+=pickedUp.getCount();
					if(count>itemStacksInDrone[i].getMaxStackSize()) {
						pickedUp.setCount(count-itemStacksInDrone[i].getMaxStackSize());
					}
				}
			}
			ItemStack rest = this.tryPutInInventory(pickedUp, itemHandler);
			
			EntityItem item = new EntityItem(world, this.posX, this.posY, this.posZ, rest);
			world.spawnEntity(item);
			
		} else {
			return false;
		}
		
		return false;
	}
	
	boolean couldFitItem(ItemStack a, ItemStack b) {
		return (ItemStack.areItemsEqual(a, b)&&b.getMaxStackSize()>b.getCount());
	}
	
	private ItemStack tryGetSaplingFromInventory(IItemHandler dest) {
		ItemStack result = ItemStack.EMPTY;
		for(int slot = 0; slot < dest.getSlots() && result.isEmpty(); slot++) {
			if(Block.getBlockFromItem(dest.extractItem(slot, 1, true).getItem()) instanceof BlockSapling) {
				result = dest.extractItem(slot, dest.getSlotLimit(slot), false);
			};
		}
		return result;
	}
}
