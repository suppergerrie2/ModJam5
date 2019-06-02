package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.init.ModSoundEvents;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import com.suppergerrie2.sdrones.networking.UpdateDroneInventoryMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

@SuppressWarnings("EntityConstructor")
public abstract class EntityAbstractDrone extends EntityCreature implements IEntityAdditionalSpawnData {

    //The item the drone was spawned with.
    //We save this so we can drop it later.
    //We save it so we dont have to reproduce an item based on upgrades.
    private ItemStack spawnItem = ItemStack.EMPTY;

    //Save the face of the block its home is set to.
    //Needed to be able to handle blocks like furnaces correctly.
    //The face of the block determines which slot to input to.
    private EnumFacing homeFacing = EnumFacing.DOWN;

    private NonNullList<ItemStack> droneInventory = NonNullList.withSize(0, ItemStack.EMPTY);
    private List<ItemStack> filter = new ArrayList<>();

    public EntityAbstractDrone(EntityType<? extends EntityAbstractDrone> type, World world) {
        super(type, world);
        this.setSize(0.3f, 0.3f);
        this.enablePersistence();
        this.setPathPriority(PathNodeType.WATER, 0f);
        this.setAIMoveSpeed(1);
    }

    public EntityAbstractDrone(EntityType<? extends EntityAbstractDrone> type, World world, double x, double y, double z, ItemStack spawnItem, EnumFacing facing, int inventorySize) {
        this(type, world);

        this.setPosition(x, y, z);
        this.setHomePosAndDistance(new BlockPos(x, y, z), 64);

        this.spawnItem = spawnItem;
        this.homeFacing = facing;

        this.setupItemStacksInDrone(inventorySize);
    }

    /**
     * Try and insert the given itemstack in the given item handler. It will try and keep inserting the item until the stack is fully inserted, or all slots have been tried. If we ran out of slots to insert in before we fully inserted the stack the items not inserted are returned.
     *
     * @param stack The stack to insert
     * @param dest The item handler to insert to
     * @return The items that couldn't be inserted.
     */
    private static ItemStack tryPutInInventory(ItemStack stack, IItemHandler dest) {
        for (int slot = 0; slot < dest.getSlots() && !stack.isEmpty(); slot++) {
            stack = dest.insertItem(slot, stack, false);
        }
        return stack;
    }

    /**
     * Returns whether the itemHandler is full or can accept more items. This method does NOT take into account what you want to insert, so it may be possible that the item you want to insert doesn't fit. This method just returns if there is space for an item.
     *
     * @param itemHandler The item handler to check
     * @return true if the itemHandler if full, else false.
     */
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
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(5.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(160.0D);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
    }

    /**
     * Write drone inventory to the PacketBuffer so it can be synced. The client needs to know of the inventory to display it.
     *
     * @param buffer The buffer to write it to
     */
    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        buffer.writeInt(this.droneInventory.size());
        for (ItemStack stack : this.droneInventory) {
            buffer.writeItemStack(stack);
        }
    }

    /**
     * Read the drone inventory from the received PacketBuffer. The client needs to know the inventory to be able to display it.
     *
     * @param buffer The received buffer.
     */
    @Override
    public void readSpawnData(PacketBuffer buffer) {
        int size = buffer.readInt();
        ItemStack[] stacks = new ItemStack[size];
        for (int i = 0; i < size; i++) {
            stacks[i] = buffer.readItemStack();
        }
        this.setDroneInventory(stacks);
    }

    //region Inventory helper methods

    /**
     * Make sure everything that needs to be saved is saved. This contains stuff like the inventory, upgrades, filter, etc. Stuff like health is handled by the super call.
     *
     * @param compound The compound to save to.
     */
    @Override
    public void writeAdditional(NBTTagCompound compound) {
        super.writeAdditional(compound);

        //Save the carry size of the bot. TODO: Possibly let this be handled by the upgrade system instead of manually saving it.
        compound.putInt("CarrySize", this.getCarrySize());

        //Save the inventory
        NBTTagList itemsInDroneTagList = new NBTTagList();

        for (ItemStack itemstack : this.droneInventory) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (itemstack != null && !itemstack.isEmpty()) {
                itemstack.write(nbttagcompound);
            }

            itemsInDroneTagList.add(nbttagcompound);
        }

        compound.put("ItemsInDrone", itemsInDroneTagList);

        //Save the filter
        NBTTagList filterTagList = new NBTTagList();

        for (ItemStack itemstack : this.filter) {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (itemstack != null && !itemstack.isEmpty()) {
                itemstack.write(nbttagcompound);
            }

            filterTagList.add(nbttagcompound);
        }

        compound.put("Filter", filterTagList);

        //Save the item the entity is spawned with.
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.spawnItem.write(nbttagcompound);
        compound.put("SpawnItem", nbttagcompound);

        //Save the home position of the bot, together with the side of the block its home is set to.
        BlockPos pos = this.getHomePosition();
        compound.putIntArray("HomePos", new int[]{pos.getX(), pos.getY(), pos.getZ()});

        compound.putString("HomeFacing", this.homeFacing.getName());

//        compound.setInt("Range", this.getRange());
//
//        compound.setDouble("Speed", this.speed);
    }

    @Override
    public void readAdditional(NBTTagCompound compound) {
        super.readAdditional(compound);

        //Load the carry size of the bot TODO: This should probably not be loaded like this but instead be read from the saved inventory size
        //Needs to be read before ItemsInDrone else the items will be reset
        if (compound.contains("CarrySize")) {
            this.setupItemStacksInDrone(compound.getInt("CarrySize"));
        }

        //Load the inventory
        if (compound.contains("ItemsInDrone")) {
            NBTTagList nbttaglist = compound.getList("ItemsInDrone", 10);
            droneInventory.clear();
            for (int i = 0; i < droneInventory.size(); ++i) {
                droneInventory.set(i, ItemStack.read(nbttaglist.getCompound(i)));
            }
        }

        //Load the filter
        if (compound.contains("Filter")) {
            NBTTagList nbttaglist = compound.getList("Filter", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.filter.add(ItemStack.read(nbttaglist.getCompound(i)));
            }
        }

        //Load the item the drone is spawned with.
        if (compound.contains("SpawnItem")) {
            this.spawnItem = ItemStack.read(compound.getCompound("SpawnItem"));
        }

        //Load the home position and the side of the block its home is set to.
        if (compound.contains("HomePos")) {
            int[] homePosCoords = compound.getIntArray("HomePos");
            this.setHomePosAndDistance(new BlockPos(homePosCoords[0], homePosCoords[1], homePosCoords[2]), 64);
        }

        if (compound.contains("HomeFacing")) {
            this.homeFacing = EnumFacing.byName(compound.getString("HomeFacing"));
        }

//        if (compound.hasKey("Range")) {
//            this.setRange(compound.getInt("Range"));
//        }
//
//        if (compound.hasKey("Speed")) {
//            this.setSpeed(compound.getDouble("Speed"));
//        }
    }

    /**
     * Initialize the drone's inventory to the given size. It's guaranteed that the inventory will not contain any null values, instead empty spaces are {@link ItemStack#EMPTY} This method WILL reset the entire inventory and will NOT copy the old inventory over to the new one!
     *
     * @param size The size of the inventory.
     */
    private void setupItemStacksInDrone(int size) {
        droneInventory = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    /**
     * Tries to pickup the item entity, makes sure the drone can pickup the item.
     *
     * @param item The item entity
     */
    public void pickupEntityItem(EntityItem item) {
        if (!item.cannotPickup() && this.canPickupItem(item.getItem())) {

            for (int i = 0; i < this.getDroneInventory().size(); i++) {
                ItemStack stack = this.getDroneInventory().get(i);
                if (stack.isEmpty()) {
                    ItemStack newStack = item.getItem().split(1);
                    this.setItemStackInDrone(i, newStack);

                    return;
                }
            }
        }
    }

    /**
     * Try and insert the drones inventory into the block at the given position.
     *
     * @param pos The block to try and insert to.
     */
    public void insertInventoryInBlock(BlockPos pos) {
        //TODO: Check if this offset nonsense is still needed or if this should be moved to somewhere else. Seems kinda strange for this method and stops this method from being a general helper method
        if (this.world.isAirBlock(pos)) {
            pos = pos.offset(this.homeFacing.getOpposite());
        }

        TileEntity tileentity = this.world.getTileEntity(pos);
        if (tileentity != null) {

            //Get the item handler from the tile entity.
            LazyOptional<IItemHandler> itemHandlerOptional = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);

            //Only run the code to actually insert if there is an item handler.
            itemHandlerOptional.ifPresent((itemHandler) -> {
                List<ItemStack> droneInventory = this.getDroneInventory();

                for (int i = 0; i < droneInventory.size(); i++) {
                    if (droneInventory.get(i).isEmpty()) {
                        continue;
                    }

                    //If the item handler is full we give up trying.
                    if (isItemHandlerFull(itemHandler)) {
                        return;
                    }

                    //Update the inventory slot to make sure no items are lost or duped.
                    //tryPutInventory returns items that could not fit so if the item could'nt be inserted we keep it.
                    this.setItemStackInDrone(i, tryPutInInventory(droneInventory.get(i), itemHandler));
                }
            });
        }
    }

    /**
     * Try and get a specific item from the item handler
     *
     * @param itemType The item type to get, for example {@link Items#BREAD}
     * @param itemHandler The item handler to get the item from
     * @param itemCheck A predicate if more specific checks are needed. (Eg. nbt)
     * @return The ItemStack extracted from the inventory, or {@link ItemStack#EMPTY} if no item was found.
     */
    private ItemStack tryGetFromInventory(Item itemType, IItemHandler itemHandler, @Nullable Predicate<ItemStack> itemCheck) {
        ItemStack result = ItemStack.EMPTY;
        for (int slot = 0; slot < itemHandler.getSlots() && result.isEmpty(); slot++) {
            ItemStack stack = itemHandler.extractItem(slot, 1, true);
            Item i = stack.getItem();

            if (((itemCheck != null && itemCheck.test(stack)) || i.equals(itemType)) && this.canPickupItem(stack)) {
                result = itemHandler.extractItem(slot, itemHandler.getSlotLimit(slot), false);
            }
        }
        return result;
    }

    /**
     * Try and get an item from a specific block and insert it into the drone's inventory.
     *
     * @param itemType The item to extract from the block
     * @param pos The position of the block to extract from
     * @param itemCheck A predicate if more specific checks are needed (Eg. Nbt)
     */
    public void tryGetItem(Item itemType, BlockPos pos, @Nullable Predicate<ItemStack> itemCheck) {
        if (!this.hasSpaceInInventory()) {
            return;
        }

        TileEntity tileentity = this.world.getTileEntity(pos);

        if (tileentity != null) {
            LazyOptional<IItemHandler> itemHandlerOptional = tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, this.homeFacing);

            itemHandlerOptional.ifPresent((itemHandler) -> {
                ItemStack pickedUp = this.tryGetFromInventory(itemType, itemHandler, itemCheck);

                List<ItemStack> stacksInDrone = this.getDroneInventory();
                for (int i = 0; i < stacksInDrone.size(); i++) {
                    if (stacksInDrone.get(i) == null || stacksInDrone.get(i).isEmpty()) {
                        this.setItemStackInDrone(i, pickedUp);
                    }
                }

                ItemStack rest = tryPutInInventory(pickedUp, itemHandler);

                EntityItem item = new EntityItem(this.world, this.posX, this.posY, this.posZ, rest);
                this.world.spawnEntity(item);
            });
        }
    }

    /**
     * Check if the drone has enough space to pickup the item, also checks the filter.
     *
     * @param item The item to check
     * @return true if the drone can pickup the item, else false.
     */
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

        return this.hasSpaceInInventory();
    }

    /**
     * Checks whether the drone has space in the inventory. The drone has space if at least 1 of the inventory slots has an empty stack.
     *
     * @return true if the drone has space, else false.
     */
    public boolean hasSpaceInInventory() {
        for (ItemStack stack : this.getDroneInventory()) {
            if (stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the drone has at least 1 non empty item stack in its inventory.
     *
     * @return true if it has at least 1 non empty item stack, else false
     */
    public boolean hasItems() {
        for (ItemStack stack : this.getDroneInventory()) {
            if (!stack.isEmpty()) {
                return true;
            }
        }

        return false;
    }
    //endregion

    //region Drone specific getters and setters

    /**
     * Set an item in the drone's inventory. This WILL destroy the old item and it WILL send a message to the client to update the visuals.
     *
     * @param index The index to set the item in
     * @param stack The item to set
     */
    private void setItemStackInDrone(int index, ItemStack stack) {
        this.droneInventory.set(index, stack);

        if (!world.isRemote) {
            sendInventoryPacket();
        }
    }

    /**
     * Set the drone's inventory to a specific ItemStack array. The drone's inventory is reset to the size of the stacks and all old stacks WILL be destroyed. Depending on the arguments a packet will also be send to the client to notify of a changed inventory. No matter the argument it will NEVER send a packet from the client to the client.
     *
     * @param stacks The new inventory
     * @param sendUpdateMessage Whether to send an update packet to the client.
     */
    public void setDroneInventory(ItemStack[] stacks, boolean sendUpdateMessage) {
        if (/*world.isRemote && TODO: Check if removing this breaks something*/ this.droneInventory.size() != stacks.length) {
            this.setupItemStacksInDrone(stacks.length);
        }

        for (int i = 0; i < stacks.length; i++) {
            this.droneInventory.set(i, stacks[i]);
        }

        if (!world.isRemote && sendUpdateMessage) {
            sendInventoryPacket();
        }
    }

    /**
     * Returns an unmodifiable list containing the drone inventory. Empty slots will be represented by {@link ItemStack#EMPTY} and is guaranteed to not contain null values.
     *
     * @return An unmodifiable list containing the drone inventory
     */
    public List<ItemStack> getDroneInventory() {
        return Collections.unmodifiableList(droneInventory);
    }

    /**
     * Set the drone's inventory to a specific ItemStack array. For more info see {@link EntityAbstractDrone::setDroneInventory}
     *
     * This method will always send a packet to the client if called from the server.
     *
     * @param stacks the stacks to set the inventory to
     */
    private void setDroneInventory(ItemStack[] stacks) {
        setDroneInventory(stacks, !world.isRemote);
    }

    /**
     * Return the current carry size of the drone. This is equal to the size of the inventory.
     *
     * @return The current carry size of the drone.
     */
    public int getCarrySize() {
        return droneInventory.size();
    }

    /**
     * Returns the amount of empty slots the drone has.
     * An empty slot is a slot where {@link ItemStack#isEmpty()} returns true
     *
     * @return the amount of empty slots
     */
    public int getEmptySpace() {
        int count = 0;

        List<ItemStack> droneInventory = this.getDroneInventory();
        for (int i = 0; i < this.getCarrySize(); i++) {
            if (droneInventory.get(i).isEmpty()) {
                count++;
            }
        }

        return count;
    }

    /**
     * Send a packet to the client to tell it of the current inventory. Without this the client wouldn't know what to display. This method should always be called when the inventory has changed on the server. The packet gets send to all players currently tracking the entity.
     *
     * If {@link World#isRemote} is true nothing will happen.
     */
    private void sendInventoryPacket() {
        if (world.isRemote) {
            return;
        }

        //Get all players currently tracking this entity.
        Set<? extends EntityPlayer> players = ((WorldServer) world).getEntityTracker().getTrackingPlayers(this);

        //Send the packet to all players.
        for (EntityPlayer player : players) {
            if (player instanceof EntityPlayerMP) {

                //Send the packet over the mod channel from the server to the client.
                DronesPacketHandler.channel.sendTo(
                    new UpdateDroneInventoryMessage(this.getDroneInventory(), this.getEntityId()),
                    ((EntityPlayerMP) player).connection.getNetworkManager(),
                    NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
    }

    /**
     * Get the speed of the drone. The speed can be based on the distance to the target to make precise movement possible, but the default implementation doesn't do this.
     *
     * @param distance The distance to the target
     * @return The speed of the drone
     */
    public double getSpeed(float distance) {
        return 1; //TODO: Make this use the attribute system
    }

    /**
     * Get the drone's range in which the drone will search for jobs.
     *
     * @return The drone's range
     */
    public double getRange() {
        return 16; //TODO: Make this upgradable
    }
    //endregion

    //region Minecraft overrides

    @Override
    public boolean canTrample(IBlockState state, BlockPos pos, float fallDistance) {
        return false; //Drone's are too light to trample blocks.
    }

    //TODO: Sounds crash the game atm.
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
    protected void playStepSound(BlockPos pos, IBlockState blockIn) {
        //"Step" sound may be the wrong word for a drone :P

        this.playSound(ModSoundEvents.droneDrivingSound, 0.15F, 1.0F);
    }

    @Override
    public void playAmbientSound() {
        //Make sure the sound doesn't play to often
        if (this.rand.nextInt(5) > 1) {
            return;
        }

        super.playAmbientSound();
    }

    @Override
    protected float getJumpUpwardsMotion() {
        return 0.5F; //Give the drone's some extra jump power to make sure they always get on top of the block
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        //Makes the rendering slightly better, it now cuts of later. TODO: Find out if there is a way to stop the rendering stopping to soon
        return super.getRenderBoundingBox().expand(0, 1 + 0.5 * getCarrySize(), 0);
    }

    //endregion

}
