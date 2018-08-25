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
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public abstract class EntityBasicDrone extends EntityCreature implements IEntityAdditionalSpawnData {

	private ItemStack[] itemStacksInDrone;
	ItemStack spawnedWith;

	List<ItemStack> filter = new ArrayList<ItemStack>();

	EnumFacing homeFacing;
	boolean selected = false;
	int range = 16;
	double speed = 1.0;
	ItemStack tool = ItemStack.EMPTY;

	public EntityBasicDrone(World worldIn) {
		super(worldIn);
		this.setSize(0.3f, 0.3f);
		this.enablePersistence();
		this.setPathPriority(PathNodeType.WATER, -1.0f);
	}

	public void init(double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this.setPosition(x, y, z);
		this.setHomePosAndDistance(new BlockPos(x, y, z), 64);
		this.spawnedWith = spawnedWith;
		this.homeFacing = facing;
		this.setupItemStacksInDrone(2);
	}

	@Override
	protected abstract void initEntityAI();

	void setupItemStacksInDrone(int size) {
		if (size > 0) {
			ItemStack[] itemsInDrone = new ItemStack[size];

			for (int i = 0; i < itemsInDrone.length; i++) {
				itemsInDrone[i] = ItemStack.EMPTY;
			}

			this.setItemStacksInDrone(itemsInDrone);
		}
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(160.0D);
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeInt(this.itemStacksInDrone.length);
		for (ItemStack stack : this.itemStacksInDrone) {
			ByteBufUtils.writeItemStack(buffer, stack);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buf) {
		int size = buf.readInt();
		ItemStack[] stacks = new ItemStack[size];
		for (int i = 0; i < size; i++) {
			stacks[i] = ByteBufUtils.readItemStack(buf);
		}
		this.setItemStacksInDrone(stacks);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);

		NBTTagList nbttaglist = new NBTTagList();

		for (ItemStack itemstack : this.getItemStacksInDrone()) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			if (itemstack != null && !itemstack.isEmpty()) {
				itemstack.writeToNBT(nbttagcompound);
			}

			nbttaglist.appendTag(nbttagcompound);
		}

		compound.setTag("ItemsInDrone", nbttaglist);

		NBTTagList nbttaglistfilter = new NBTTagList();

		for (ItemStack itemstack : this.filter) {
			NBTTagCompound nbttagcompound = new NBTTagCompound();

			if (itemstack != null && !itemstack.isEmpty()) {
				itemstack.writeToNBT(nbttagcompound);
			}

			nbttaglistfilter.appendTag(nbttagcompound);
		}

		compound.setTag("Filter", nbttaglistfilter);

		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.spawnedWith.writeToNBT(nbttagcompound);
		compound.setTag("SpawnedWith", nbttagcompound);

		compound.setInteger("CarrySize", this.getCarrySize());

		BlockPos pos = this.getHomePosition();
		compound.setIntArray("HomePos", new int[] { pos.getX(), pos.getY(), pos.getZ() });

		compound.setString("HomeFacing", this.homeFacing.getName());

		compound.setInteger("Range", this.getRange());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);

		if (compound.hasKey("CarrySize")) {
			this.setupItemStacksInDrone(compound.getInteger("CarrySize"));
		}

		if (compound.hasKey("ItemsInDrone", 9)) {
			NBTTagList nbttaglist = compound.getTagList("ItemsInDrone", 10);

			for (int i = 0; i < this.getItemStacksInDrone().length; ++i) {
				this.setItemStacksInDrone(i, new ItemStack(nbttaglist.getCompoundTagAt(i)));
			}
		}

		if (compound.hasKey("Filter", 9)) {
			NBTTagList nbttaglist = compound.getTagList("Filter", 10);

			for (int i = 0; i < nbttaglist.tagCount(); ++i) {
				this.filter.add(new ItemStack(nbttaglist.getCompoundTagAt(i)));
			}
		}

		if (compound.hasKey("SpawnedWith")) {
			this.spawnedWith = new ItemStack(compound.getCompoundTag("SpawnedWith"));
		}

		if (compound.hasKey("HomePos")) {
			int[] homePosCoords = compound.getIntArray("HomePos");
			this.setHomePosAndDistance(new BlockPos(homePosCoords[0], homePosCoords[1], homePosCoords[2]), 64);
		}

		if (compound.hasKey("HomeFacing")) {
			this.homeFacing = EnumFacing.byName(compound.getString("HomeFacing"));
		} else {
			this.homeFacing = EnumFacing.DOWN;
		}

		if (compound.hasKey("Range")) {
			this.setRange(compound.getInteger("Range"));
		} else {
			if (this instanceof EntityCropFarmDrone) {
				this.setRange(4);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		int stacksDisplayed = 0;
		for (ItemStack stack : this.getItemStacksInDrone()) {
			if (!stack.isEmpty()) {
				stacksDisplayed++;
			}
		}

		return super.getRenderBoundingBox().grow(0, 0.5 * stacksDisplayed, 0);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		this.setGlowing(this.selected);

		this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source == DamageSource.IN_WALL && this.ticksExisted < 40) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}

	@Override
	public boolean canTrample(World world, Block block, BlockPos pos, float fallDistance) {
		return false;
	}

	public boolean pickupEntityItem(EntityItem item) {
		if (!item.cannotPickup() && this.canPickupItem(item.getItem())) {
			for (int i = 0; i < this.getItemStacksInDrone().length; i++) {
				if (this.getItemStacksInDrone()[i] == null || this.getItemStacksInDrone()[i].isEmpty()) {
					ItemStack stack = item.getItem().splitStack(1);
					this.setItemStacksInDrone(i, stack);

					return true;
				}
			}
		}
		return false;
	}

	public boolean insertItemsInBlock(BlockPos pos) {
		if (this.world.isAirBlock(pos)) {
			pos = pos.offset(this.homeFacing.getOpposite());
		}

		IItemHandler itemHandler = null;

		TileEntity tileentity = this.world.getTileEntity(pos);
		if (tileentity != null) {
			itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);

			if (itemHandler != null) {
				for (int i = 0; i < this.getItemStacksInDrone().length; i++) {
					if (isItemHandlerFull(itemHandler)) {
						continue;
					}

					if (this.getItemStacksInDrone()[i] != null && !this.getItemStacksInDrone()[i].isEmpty()) {
						this.setItemStacksInDrone(i, this.tryPutInInventory(this.getItemStacksInDrone()[i], itemHandler));
					}
				}
			}
		}

		return true;
	}

	public boolean tryGetItem(Item itemType, BlockPos pos, @Nullable Predicate<Item> itemCheck) {

		if (!this.canPickupItem()) {
			return false;
		}

		IItemHandler itemHandler = null;

		TileEntity tileentity = this.world.getTileEntity(pos);
		if (tileentity != null) {
			itemHandler = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);

			if (itemHandler != null) {
				ItemStack pickedUp = this.tryGetFromInventory(itemType, itemHandler, itemCheck);

				ItemStack[] stacksInDrone = this.getItemStacksInDrone();
				for (int i = 0; i < stacksInDrone.length; i++) {
					if (stacksInDrone[i] == null || stacksInDrone[i].isEmpty()) {
						this.setItemStacksInDrone(i, pickedUp);
						return true;
					}
				}

				ItemStack rest = this.tryPutInInventory(pickedUp, itemHandler);

				EntityItem item = new EntityItem(this.world, this.posX, this.posY, this.posZ, rest);
				this.world.spawnEntity(item);
			}
		}

		return false;
	}

	protected ItemStack tryPutInInventory(ItemStack stack, IItemHandler dest) {
		for (int slot = 0; slot < dest.getSlots() && !stack.isEmpty(); slot++) {
			stack = dest.insertItem(slot, stack, false);
		}
		return stack;
	}

	private ItemStack tryGetFromInventory(Item itemType, IItemHandler dest, @Nullable Predicate<Item> itemCheck) {
		ItemStack result = ItemStack.EMPTY;
		for (int slot = 0; slot < dest.getSlots() && result.isEmpty(); slot++) {
			ItemStack stack = dest.extractItem(slot, 1, true);
			Item i = stack.getItem();

			if (((itemCheck != null && itemCheck.test(i)) || i.equals(itemType)) && this.canPickupItem(stack)) {
				result = dest.extractItem(slot, dest.getSlotLimit(slot), false);
			}
			;
		}
		return result;
	}

	private static boolean isItemHandlerFull(IItemHandler itemHandler) {
		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
			if (stackInSlot.isEmpty() || stackInSlot.getCount() != stackInSlot.getMaxStackSize()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (!this.world.isRemote && player.getHeldItem(hand).getItem() instanceof ItemDroneStick) {
			if (player.isSneaking()) {
				this.setDead();
				this.onDeath(DamageSource.causePlayerDamage(player));
			} else {
				ItemDroneStick.addSelected(this, player.getHeldItem(hand));
				this.selected = true;
			}
		}
		return super.processInteract(player, hand);
	}

	@Override
	protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
		for (ItemStack stack : this.getItemStacksInDrone()) {
			if (stack == null) {
				continue;
			}
			this.entityDropItem(stack, 0.1f);
		}
		this.entityDropItem(this.spawnedWith, 0.1f);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSoundEvents.droneBleepSound;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
		return null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	protected void playStepSound(BlockPos pos, Block blockIn) {
		this.playSound(ModSoundEvents.droneDrivingSound, 0.15F, 1.0F);
	}

	@Override
	public void playLivingSound() {
		if (this.rand.nextInt(5) > 1) {
			return;
		}

		SoundEvent soundevent = this.getAmbientSound();

		if (soundevent != null) {
			this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
		}
	}

	@Override
	protected float getJumpUpwardsMotion() {
		return 0.5F;
	}

	public boolean canPickupItem(ItemStack item) {
		if (this.filter.size() > 0) {
			boolean isInFilter = false;
			for (ItemStack itemStack : this.filter) {
				if (item.isItemEqual(itemStack)) {
					isInFilter = true;
				}
			}
			if (!isInFilter) {
				return false;
			}
		}

		return this.canPickupItem();
	}

	public boolean canPickupItem() {
		for (ItemStack stack : this.getItemStacksInDrone()) {
			if (stack == null || stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean hasItems() {
		for (ItemStack stack : this.getItemStacksInDrone()) {
			if (stack != null && !stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public void setFilter(List<ItemStack> list) {
		this.filter = list;
	}

	public void setSelected(boolean b) {
		this.selected = b;
		this.setGlowing(b);
	}

	public void setHomeFacing(EnumFacing facing) {
		this.homeFacing = facing;
	}

	public int getRange() {
		return this.range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public float getSpeed(float distanceToTarget) {
		return (float) (distanceToTarget > 5 ? this.speed : Math.max(this.speed - 1 / distanceToTarget, 1.0f));
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public ItemStack[] getItemStacksInDrone() {
		return this.itemStacksInDrone;
	}

	public void setItemStacksInDrone(ItemStack[] stacks, boolean sendMessage) {
		for (int i = 0; i < stacks.length; i++) {
			if (stacks[i] == null) {
				stacks[i] = ItemStack.EMPTY;
			}
		}

		this.itemStacksInDrone = stacks;

		if (!this.world.isRemote && sendMessage) {
			DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(this.getItemStacksInDrone(), this.getEntityId()));
		}
	}

	public void setItemStacksInDrone(int slot, ItemStack stack) {
		if (stack == null) {
			stack = ItemStack.EMPTY;
		}

		this.itemStacksInDrone[slot] = stack;
		if (!this.world.isRemote) {
			DronesPacketHandler.INSTANCE.sendToAll(new ItemsInDroneMessage(this.getItemStacksInDrone(), this.getEntityId()));
		}
	}

	public void setItemStacksInDrone(ItemStack[] stacks) {
		this.setItemStacksInDrone(stacks, true);
	}

	public void setCarrySize(int newSize) {
		this.setupItemStacksInDrone(newSize);
	}

	public int getCarrySize() {
		return this.getItemStacksInDrone().length;
	}

	public ItemStack getTool() {
		return this.tool;
	}

	public void setTool(ItemStack tool) {
		this.tool = tool;
	}
}
