package com.suppergerrie2.sdrones.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.suppergerrie2.sdrones.init.ModSoundEvents;
import com.suppergerrie2.sdrones.items.ItemDroneStick;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import com.suppergerrie2.sdrones.networking.ItemsInDroneMessage;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class EntityBasicDrone extends EntityCreature implements IEntityAdditionalSpawnData {

	private ItemStack[] itemStacksInDrone;
	ItemStack spawnedWith;

	List<ItemStack> filter = new ArrayList<ItemStack>();

	int carryLevel = 1;
	EnumFacing homeFacing;
	boolean selected = false;
	int range = 16;
	ItemStack tool = ItemStack.EMPTY;

	public EntityBasicDrone(World worldIn) {
		super(worldIn);
		this.setSize(0.3f, 0.3f);
		this.enablePersistence();
		this.setupItemStacksInDrone();
		this.spawnedWith = ItemStack.EMPTY;
		homeFacing = EnumFacing.UP;
	}

	public EntityBasicDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn);		
		this.setPosition(x,y,z);
		this.setHomePosAndDistance(new BlockPos(x,y,z), 64);
		this.spawnedWith = spawnedWith;
		homeFacing = facing;
	}

	public EntityBasicDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carryLevel) {
		this(worldIn, x, y, z, spawnedWith, facing);
		this.carryLevel = carryLevel;
		this.setupItemStacksInDrone();
	}

	@Override
	protected abstract void initEntityAI(); 

	void setupItemStacksInDrone() {
		if(this.getCarrySize()>0) {
			ItemStack[] itemsInDrone = new ItemStack[this.getCarrySize()];

			for(int i = 0; i <itemsInDrone.length; i++) {
				itemsInDrone[i] = ItemStack.EMPTY;
			}

			this.setItemStacksInDrone(itemsInDrone);
		}
	}

	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(160.0D);
	}

	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.itemStacksInDrone.length);
		for(ItemStack stack : this.itemStacksInDrone) {
			ByteBufUtils.writeItemStack(buffer, stack);
		}
	}

	public void readSpawnData(ByteBuf buf) {
		int size = buf.readInt();
		ItemStack[] stacks = new ItemStack[size];
		for(int i = 0; i < size; i++) {
			stacks[i] = ByteBufUtils.readItemStack(buf);
		}
		this.setItemStacksInDrone(stacks);
	}

	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);

		NBTTagList nbttaglist = new NBTTagList();

		for (ItemStack itemstack : this.getItemStacksInDrone())
		{
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			if (itemstack!=null&&!itemstack.isEmpty())
			{
				itemstack.writeToNBT(nbttagcompound);
			}

			nbttaglist.appendTag(nbttagcompound);
		}

		compound.setTag("ItemsInDrone", nbttaglist);

		NBTTagList nbttaglistfilter = new NBTTagList();

		for (ItemStack itemstack : this.filter)
		{
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			if (itemstack!=null&&!itemstack.isEmpty())
			{
				itemstack.writeToNBT(nbttagcompound);
			}

			nbttaglistfilter.appendTag(nbttagcompound);
		}

		compound.setTag("Filter", nbttaglistfilter);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		spawnedWith.writeToNBT(nbttagcompound);
		compound.setTag("SpawnedWith", nbttagcompound);

		compound.setInteger("CarrySize", this.getCarrySize());
		compound.setInteger("CarryLevel", carryLevel);

		BlockPos pos = this.getHomePosition();
		compound.setIntArray("HomePos", new int[] {pos.getX(), pos.getY(), pos.getZ()});
	}

	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);

		if(compound.hasKey("CarryLevel")) {
			carryLevel = compound.getInteger("CarryLevel");
			this.setItemStacksInDrone(new ItemStack[this.getCarrySize()]);
		}

		if (compound.hasKey("ItemsInDrone", 9))
		{
			NBTTagList nbttaglist = compound.getTagList("ItemsInDrone", 10);

			for (int i = 0; i < this.getItemStacksInDrone().length; ++i)
			{
				this.setItemStacksInDrone(i, new ItemStack(nbttaglist.getCompoundTagAt(i)));
			}
		}

		if (compound.hasKey("Filter", 9))
		{
			NBTTagList nbttaglist = compound.getTagList("Filter", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i)
			{
				filter.add(new ItemStack(nbttaglist.getCompoundTagAt(i)));
			}
		}

		if(compound.hasKey("SpawnedWith")) {
			spawnedWith = new ItemStack(compound.getCompoundTag("SpawnedWith"));
		}

		if(compound.hasKey("HomePos")) {
			int[] homePosCoords = compound.getIntArray("HomePos");
			this.setHomePosAndDistance(new BlockPos(homePosCoords[0], homePosCoords[1], homePosCoords[2]), 64);
		}
	}

	public void onUpdate() {
		super.onUpdate();
		this.setGlowing(selected);
		
		this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
		
		if(this.selected) {
			//			BlockPos home = this.getHomePosition();
			//			Minecraft.getMinecraft().effectRenderer.addEffect(new HomeParticle(world, home.getX()+0.5, home.getY(), home.getZ()+0.5, 1, 1, 1));
		}
	}
	
	public boolean attackEntityFrom(DamageSource source, float amount)
    {
		if(source==DamageSource.IN_WALL&&this.ticksExisted<40) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
    }

	public boolean pickupItem(EntityItem item) {
		if(!item.cannotPickup()&&canPickupItem(item.getItem())) {
			for(int i = 0; i < getItemStacksInDrone().length; i++) {
				if(getItemStacksInDrone()[i]==null||getItemStacksInDrone()[i].isEmpty()) {
					ItemStack stack = item.getItem().splitStack(1);
					this.setItemStacksInDrone(i, stack);

					return true;
				}
			}
		}
		return false;
	}

	public boolean insertItems(BlockPos pos) {		
		if(world.isAirBlock(pos)) {
			pos = pos.offset(homeFacing.getOpposite());
		}

		IItemHandler itemHandler = null;

		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity != null)
		{
			itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);
			
			if(itemHandler!=null) {
				for(int i = 0; i < getItemStacksInDrone().length; i++) {
					if(isItemHandlerFull(itemHandler)) {
						continue;
					}
	
					if(getItemStacksInDrone()[i]!=null&&!getItemStacksInDrone()[i].isEmpty()) {
						this.setItemStacksInDrone(i, tryPutInInventory(getItemStacksInDrone()[i], itemHandler));
					}
				}
			}
		}

		return true;
	}

	public boolean tryGetItem(Item itemType, BlockPos pos, @Nullable Predicate<Item> itemCheck) {

		//TODO: filter intergration.
		if(!this.canPickupItem()) {
			return false;
		}

		IItemHandler itemHandler = null;

		TileEntity tileentity = world.getTileEntity(pos);
		if (tileentity != null)
		{
			itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);
			
			if(itemHandler!=null) {
				ItemStack pickedUp = this.tryGetFromInventory(itemType, itemHandler, itemCheck);

				ItemStack[] stacksInDrone = this.getItemStacksInDrone();
				for(int i = 0; i < stacksInDrone.length; i++) {
					if(stacksInDrone[i]==null||stacksInDrone[i].isEmpty()) {
						this.setItemStacksInDrone(i, pickedUp);
						return true;
					}
				}
				
				ItemStack rest = this.tryPutInInventory(pickedUp, itemHandler);

				EntityItem item = new EntityItem(world, this.posX, this.posY, this.posZ, rest);
				world.spawnEntity(item);
			}
		}

		return false;
	}
	
	protected ItemStack tryPutInInventory(ItemStack stack, IItemHandler dest) {
		for(int slot = 0; slot < dest.getSlots() && !stack.isEmpty(); slot++) {
			stack = dest.insertItem(slot, stack, false);
		}
		return stack;
	}

	private ItemStack tryGetFromInventory(Item itemType, IItemHandler dest, @Nullable Predicate<Item> itemCheck) {
		ItemStack result = ItemStack.EMPTY;
		for(int slot = 0; slot < dest.getSlots() && result.isEmpty(); slot++) {
			Item i = dest.extractItem(slot, 1, true).getItem();
			if((itemCheck!=null&&itemCheck.test(i)||i.equals(itemType))) {
				result = dest.extractItem(slot, dest.getSlotLimit(slot), false);
			};
		}
		return result;
	}

	private static boolean isItemHandlerFull(IItemHandler itemHandler)
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

	public boolean processInteract(EntityPlayer player, EnumHand hand)
	{
		if(!world.isRemote&&player.getHeldItem(hand).getItem() instanceof ItemDroneStick) {
			if(player.isSneaking()) {				
				this.setDead();
				this.onDeath(DamageSource.causePlayerDamage(player));
			} else {
				ItemDroneStick droneStick = (ItemDroneStick)player.getHeldItem(hand).getItem();
				droneStick.addSelected(this);
				selected = true;
			}
		}
		return super.processInteract(player, hand);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source)
	{
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack==null) continue;
			this.entityDropItem(stack, 0.1f);
		}
		this.entityDropItem(spawnedWith, 0.1f);
	}

	protected SoundEvent getAmbientSound()
	{
		return ModSoundEvents.droneBleepSound;
	}

	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return null;
	}

	protected SoundEvent getDeathSound()
	{
		return null;
	}

	protected float getSoundVolume()
	{
		return 0.4F;
	}

	protected void playStepSound(BlockPos pos, Block blockIn)
	{
		this.playSound(ModSoundEvents.droneDrivingSound, 0.15F, 1.0F);
	}

	public void playLivingSound()
	{
		if(this.rand.nextInt(5)>1) {
			return;
		}

		SoundEvent soundevent = this.getAmbientSound();

		if (soundevent != null)
		{
			this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	public boolean canPickupItem(ItemStack item) {
		if(filter.size()>0) {
			boolean isInFilter = false;
			for(ItemStack itemStack : filter) {
				if(item.isItemEqual(itemStack)) {
					isInFilter = true;
				}
			}
			if(!isInFilter) {
				return false;
			}
		}

		return this.canPickupItem();
	}

	public boolean canPickupItem() {
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack==null||stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasItems() {
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack!=null&&!stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void setFilter(List<ItemStack> list) {
		filter = list;
	}

	public void setSelected(boolean b) {
		this.selected = b;
		this.setGlowing(b);
	}

	public void setHomeFacing(EnumFacing facing) {
		this.homeFacing = facing;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getRange() {
		return this.range;
	}

	public ItemStack[] getItemStacksInDrone() {
		return itemStacksInDrone;
	}

	public void setItemStacksInDrone(ItemStack[] stacks, boolean sendMessage) {	
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i]==null) {
				stacks[i]=ItemStack.EMPTY;
			}
		}

		itemStacksInDrone = stacks;	

		if(!this.world.isRemote&&sendMessage) DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(getItemStacksInDrone(), this.getEntityId()));
	}

	public void setItemStacksInDrone(int slot, ItemStack stack) {	
		if(stack==null) stack = ItemStack.EMPTY;

		itemStacksInDrone[slot] = stack;
		if(!this.world.isRemote)DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(getItemStacksInDrone(), this.getEntityId()));
	}

	public void setItemStacksInDrone(ItemStack[] stacks) {
		this.setItemStacksInDrone(stacks, true);
	}

	public int getCarrySize() {
		return 2+(this.carryLevel-1);
	}

	public ItemStack getTool() {
		return tool;
	}

	public void setTool(ItemStack tool) {
		this.tool = tool;
	}
}
