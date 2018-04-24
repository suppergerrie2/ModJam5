package com.suppergerrie2.sdrones.entities;

import org.apache.commons.lang3.tuple.Pair;

import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIFarmCrop;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPlantCrop;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPrepareFarmland;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPrepareLand;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import com.suppergerrie2.sdrones.networking.ItemsInDroneMessage;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class EntityCropFarmDrone extends EntityBasicDrone {

	public EntityCropFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing,
			int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
		((PathNavigateGround)this.getNavigator()).setCanSwim(true);
	}

	public EntityCropFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	public EntityCropFarmDrone(World worldIn) {
		this(worldIn, -1, -1, -1, ItemStack.EMPTY, EnumFacing.UP);
	}
	
	public EntityAIPrepareFarmland aiPrepareFarmLand;
	
	@Override
	protected void initEntityAI() {
		//13
		int range = 4;
		EntityAIPrepareLand aiPrepareLand = new EntityAIPrepareLand(this, 1.0f, range);
		this.tasks.addTask(0, aiPrepareLand);
		aiPrepareFarmLand =  new EntityAIPrepareFarmland(this, 1.0f, range, aiPrepareLand);
		this.tasks.addTask(1, aiPrepareFarmLand);
		this.tasks.addTask(2, new EntityAIPlantCrop(this, 1.0f, range));
		this.tasks.addTask(3, new EntityAIFarmCrop(this, 1.0f, range));
		this.tasks.addTask(4, new EntityAIGoHome(this, 1.0f));
		this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0f));
	}

	public ItemStack getTool() {
		return new ItemStack(Items.DIAMOND_HOE);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.world.isRemote) {
			if(aiPrepareFarmLand.isPrepared()) {
				if(this.hasDirt()) {
					if(this.getDistanceSq(this.getHomePosition())<4) {
						this.insertItems(getHomePosition());
					}
				} else if(!this.hasSeeds()&&this.getDistanceSq(this.getHomePosition())<4) {
					this.tryGetSeeds(this.getHomePosition());
				}
			}  else {
				if(!this.hasDirt()&&this.getDistanceSq(this.getHomePosition())<4) {
					this.tryGetDirt(this.getHomePosition());
				}
			}
		}
		
		this.setEntityInvulnerable(this.isEntityInsideOpaqueBlock());
		
		if ((this.getHealth() < this.getMaxHealth() && this.ticksExisted % 50 == 0))
        {
            this.heal(1.0F);
        }
	}

	public boolean hasSeeds() {
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack!=null&&!stack.isEmpty()) {
				if(stack.getItem() instanceof IPlantable) {
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean hasDirt() {
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack!=null&&!stack.isEmpty()) {
				if(stack.getItem().equals(Item.getItemFromBlock(Blocks.DIRT))) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean tryGetSeeds(BlockPos pos) {
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

			ItemStack pickedUp = this.tryGetSeedsFromInventory(itemHandler);
			
			for(int i = 0; i < getItemStacksInDrone().length; i++) {
				if(getItemStacksInDrone()[i]==null||getItemStacksInDrone()[i].isEmpty()) {
					getItemStacksInDrone()[i] = pickedUp;
					DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(getItemStacksInDrone(), this.getEntityId()));
					return true;
				} else if(getItemStacksInDrone()[i]!=null&&this.couldFitItem(pickedUp, getItemStacksInDrone()[i])) {
					int count = getItemStacksInDrone()[i].getCount();
					count+=pickedUp.getCount();
					if(count>getItemStacksInDrone()[i].getMaxStackSize()) {
						pickedUp.setCount(count-getItemStacksInDrone()[i].getMaxStackSize());
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
	
	public boolean tryGetDirt(BlockPos pos) {
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

			ItemStack pickedUp = this.tryGetDirtFromInventory(itemHandler);

			for(int i = 0; i < getItemStacksInDrone().length; i++) {
				if(getItemStacksInDrone()[i]==null||getItemStacksInDrone()[i].isEmpty()) {
					getItemStacksInDrone()[i] = pickedUp;
					DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(getItemStacksInDrone(), this.getEntityId()));
					return true;
				} else if(getItemStacksInDrone()[i]!=null&&this.couldFitItem(pickedUp, getItemStacksInDrone()[i])) {
					int count = getItemStacksInDrone()[i].getCount();
					count+=pickedUp.getCount();
					if(count>getItemStacksInDrone()[i].getMaxStackSize()) {
						pickedUp.setCount(count-getItemStacksInDrone()[i].getMaxStackSize());
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

	private ItemStack tryGetSeedsFromInventory(IItemHandler dest) {
		ItemStack result = ItemStack.EMPTY;
		for(int slot = 0; slot < dest.getSlots() && result.isEmpty(); slot++) {
			if(dest.extractItem(slot, 1, true).getItem() instanceof IPlantable && this.canPickupItem(dest.extractItem(slot, 1, true))) {
				result = dest.extractItem(slot, dest.getSlotLimit(slot), false);
			};
		}
		return result;
	}
	
	private ItemStack tryGetDirtFromInventory(IItemHandler dest) {
		ItemStack result = ItemStack.EMPTY;
		for(int slot = 0; slot < dest.getSlots() && result.isEmpty(); slot++) {
			if(dest.extractItem(slot, 1, true).getItem().equals(Item.getItemFromBlock(Blocks.DIRT))) {
				result = dest.extractItem(slot, dest.getSlotLimit(slot), false);
			};
		}
		return result;
	}

	public ItemStack getSeeds() {
		ItemStack[] stacks = this.getItemStacksInDrone();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i].isEmpty()) {
				continue;
			}

			if(stacks[i].getItem() instanceof IPlantable){
				return stacks[i];
			}
		}
		return ItemStack.EMPTY;
	}

	public void useSeeds() {
		ItemStack[] stacks = this.getItemStacksInDrone();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i].isEmpty()) {
				continue;
			}

			if(stacks[i].getItem() instanceof IPlantable){
				stacks[i].shrink(1);
			}
		}
		this.setItemStacksInDrone(stacks);
	}

	public boolean plantSeeds(BlockPos destination) {
		ItemStack seeds = this.getSeeds();

		if(seeds.isEmpty()) {
			return false;
		}

		IBlockState blockstate = ((IPlantable)seeds.getItem()).getPlant(world, destination);

		world.setBlockState(destination, blockstate);

		this.useSeeds();

		return true;
	}

	public void farmCrop(BlockPos destination) {
		world.destroyBlock(destination, true);
	}

	public void useDirt() {
		ItemStack[] stacks = this.getItemStacksInDrone();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i].isEmpty()) {
				continue;
			}

			if(stacks[i].getItem().equals(Item.getItemFromBlock(Blocks.DIRT))){
				stacks[i].shrink(1);
				break;
			}
		}
		this.setItemStacksInDrone(stacks);
	}
}
