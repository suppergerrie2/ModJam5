package com.suppergerrie2.drones.entities;

import org.apache.commons.lang3.tuple.Pair;

import com.suppergerrie2.drones.entities.AI.EntityAIBringItemHome;
import com.suppergerrie2.drones.entities.AI.EntityAISearchItems;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.VanillaInventoryCodeHooks;

public class EntityBasicDrone extends EntityCreature  {

	ItemStack[] itemStacksInDrone;
	ItemStack spawnedWith;

	int carrySize;

	public EntityBasicDrone(World worldIn) {
		super(worldIn);
		this.setSize(0.3f, 0.3f);
		this.setupAI();
		itemStacksInDrone = new ItemStack[1];
		this.carrySize = 1;
		this.spawnedWith = ItemStack.EMPTY;
	}

	public EntityBasicDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith) {
		this(worldIn);		
		this.setPosition(x,y,z);
		this.setHomePosAndDistance(new BlockPos(x,y,z), 64);
		this.spawnedWith = spawnedWith;
	}

	public EntityBasicDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, int carrySize) {
		this(worldIn, x, y, z, spawnedWith);
		if(carrySize>0) {
			itemStacksInDrone = new ItemStack[carrySize];
			this.carrySize = carrySize;
		}
	}

	void setupAI() {
		this.tasks.addTask(0, new EntityAISearchItems(this, 1.0f));
		this.tasks.addTask(0, new EntityAIBringItemHome(this, 1.0f));
		this.tasks.addTask(1, new EntityAIWanderAvoidWater(this, 1.0f));
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
	}

	public boolean canPickupItem() {
		for(ItemStack stack : itemStacksInDrone) {
			if(stack==null||stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean pickupItem(EntityItem item) {
		if(!item.cannotPickup()&&canPickupItem()) {
			for(int i = 0; i < itemStacksInDrone.length; i++) {
				if(itemStacksInDrone[i]==null||itemStacksInDrone[i].isEmpty()) {
					ItemStack stack = item.getItem().splitStack(1);
					itemStacksInDrone[i] = stack;
					return true;
				}
			}
		}
		return false;
	}

	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		NBTTagList nbttaglist = new NBTTagList();

		for (ItemStack itemstack : this.itemStacksInDrone)
		{
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			if (itemstack!=null&&!itemstack.isEmpty())
			{
				itemstack.writeToNBT(nbttagcompound);
			}

			nbttaglist.appendTag(nbttagcompound);
		}

		compound.setTag("ItemsInDrone", nbttaglist);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		spawnedWith.writeToNBT(nbttagcompound);
		compound.setTag("SpawnedWith", nbttagcompound);

		compound.setInteger("CarrySize", carrySize);
	}

	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		if (compound.hasKey("ItemsInDrone", 9))
		{
			NBTTagList nbttaglist = compound.getTagList("ItemsInDrone", 10);

			for (int i = 0; i < this.itemStacksInDrone.length; ++i)
			{
				this.itemStacksInDrone[i] =  new ItemStack(nbttaglist.getCompoundTagAt(i));
			}
		}

		if(compound.hasKey("SpawnedWith")) {
			spawnedWith = new ItemStack(compound.getCompoundTag("SpawnedWith"));
		}

		if(compound.hasKey("CarrySize")) {
			carrySize = compound.getInteger("CarrySize");
		}
	}

	public boolean hasItems() {
		for(ItemStack stack : itemStacksInDrone) {
			if(stack!=null&&!stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		for(ItemStack stack : itemStacksInDrone) {
			if(stack==null) continue;
			this.entityDropItem(stack, 0.1f);
		}
		this.entityDropItem(spawnedWith, 0.1f);
	}

	//TODO: Remove item from inventory after inserting
	public boolean insertItems(BlockPos pos) {
		IBlockState iblockstate = world.getBlockState(pos);
		if(iblockstate.getBlock() instanceof BlockContainer) {
			Pair<IItemHandler, Object> destinationResult = VanillaInventoryCodeHooks.getItemHandler(world, pos.getX(), pos.getY(), pos.getZ(), EnumFacing.DOWN);
			if(destinationResult==null) {
				return false;
			} 

			IItemHandler itemHandler = destinationResult.getKey();
			
			for(int i = 0; i < itemStacksInDrone.length; i++) {
				if(isFull(itemHandler)) {
					continue;
				}
				
				if(itemStacksInDrone[i]!=null&&!itemStacksInDrone[i].isEmpty()) {
					tryPutInInventory(itemStacksInDrone[i], itemHandler);
				}
			}
		}	
		
		return true;
	}

	private static boolean isFull(IItemHandler itemHandler)
	{
		for (int slot = 0; slot < itemHandler.getSlots(); slot++)
		{
			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
			if (stackInSlot.isEmpty() || stackInSlot.getCount() != stackInSlot.getMaxStackSize())
			{
				return false;
			}
		}
		return true;
	}

	private ItemStack tryPutInInventory(ItemStack stack, IItemHandler dest) {
		for(int slot = 0; slot < dest.getSlots() && !stack.isEmpty(); slot++) {
			stack = dest.insertItem(slot, stack, false);
		}
		return stack;
	}
}
