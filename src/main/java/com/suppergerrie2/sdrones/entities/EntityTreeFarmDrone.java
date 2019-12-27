package com.suppergerrie2.sdrones.entities;

import com.suppergerrie2.sdrones.entities.ai.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.ai.treefarm.EntityAICutTree;
import com.suppergerrie2.sdrones.entities.ai.treefarm.EntityAIPlantSapling;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SaplingBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.item.*;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityTreeFarmDrone extends EntityAbstractDrone {

    public EntityTreeFarmDrone(ItemSpawnDrone.DroneSpawnData spawnData) {
        super(ModEntities.tree_farm_drone, spawnData.world, spawnData.x, spawnData.y, spawnData.z, spawnData.spawnItem, spawnData.spawnFacing, 5);
    }

    public EntityTreeFarmDrone(World world) {
        this(ModEntities.tree_farm_drone, world);
    }

    public EntityTreeFarmDrone(EntityType<EntityAbstractDrone> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new EntityAIPlantSapling(this));
        this.goalSelector.addGoal(0, new EntityAICutTree(this));
        this.goalSelector.addGoal(1, new EntityAIGoHome(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomWalkingGoal(this, 1.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isRemote) {
            BlockPos home = this.getHomePosition();
            if (!this.hasSapling() && this.getDistanceSq(home.getX(), home.getY(), home.getZ()) < 4) {
                this.tryGetItem(null, this.getHomePosition(), item -> ItemTags.SAPLINGS.contains(item.getItem()));
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    if (BlockTags.LEAVES.contains(this.world.getBlockState(this.getPosition().add(x, 0, y)).getBlock())) {
                        this.world.destroyBlock(this.getPosition().add(x, 0, y), true);
                    }
                }
            }
        }
    }


    @Override
    @Nonnull
    public ItemStack getHeldItemMainhand() {
        return new ItemStack(Items.DIAMOND_AXE);
    }

    /**
     * Check whether the drone has a sapling. An item is a sapling if: {@link Block#getBlockFromItem(Item)} returns a block that is an instance of {@link SaplingBlock};
     *
     * @return true if at least 1 slot has a sapling, else false.
     */
    private boolean hasSapling() {
        for (ItemStack stack : this.getDroneInventory()) {
            if (stack != null && !stack.isEmpty()) {
                if (ItemTags.SAPLINGS.contains(stack.getItem())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get the first sapling in the drone's inventory. For more information about when an item is seen as a sapling see {@link EntityTreeFarmDrone#hasSapling()}
     *
     * @return the first itemstack that is a sapling. If no sapling is found it returns {@link ItemStack#EMPTY}
     */
    public ItemStack getSapling() {
        List<ItemStack> stacks = this.getDroneInventory();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) {
                continue;
            }

            if (ItemTags.SAPLINGS.contains(stack.getItem())) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    /**
     * Place a sapling at the given position.
     *
     * @param destination The place where the sapling should be placed
     * @return true if placed, else false.
     */
    public boolean placeSapling(BlockPos destination) {
        ItemStack sapling = this.getSapling();

        if (sapling.isEmpty()) {
            return false;
        }

        Block blockToPlace = Block.getBlockFromItem(sapling.getItem());
        BlockItemUseContext useContext = new DirectionalPlaceContext(this.world, destination, Direction.DOWN, sapling, Direction.UP);
        BlockState blockstate = blockToPlace.getStateForPlacement(useContext);

        if(blockstate == null) return false;

        this.world.setBlockState(destination, blockstate);

        sapling.shrink(1);

        if (!world.isRemote) {
            sendInventoryPacket();
        }

        return true;
    }

    /**
     * Cuts the tree at the given location by walking over it. Each block of the tree will then be destroyed and dropped. Leaves are not destroyed.
     *
     * @param destination the root of the tree
     */
    public void cutTree(BlockPos destination) {
        int MAX_TREE_SIZE = 256;
        List<BlockPos> treePositions = new ArrayList<>();

        treePositions.add(destination);

        //noinspection StatementWithEmptyBody
        while (this.walkTree(treePositions) && treePositions.size() < MAX_TREE_SIZE) {
        }

        for (BlockPos pos : treePositions) {
            this.world.destroyBlock(pos, true);
        }
    }

    /**
     *
     */
    private boolean walkTree(List<BlockPos> positions) {

        boolean modified = false;
        List<BlockPos> copy = new ArrayList<>(positions);
        for (BlockPos pos : copy) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = 0; y <= 1; y++) {
                        BlockPos candidatePosition = pos.add(x, y, z);

                        if (!positions.contains(candidatePosition) && BlockTags.LOGS.contains(world.getBlockState(candidatePosition).getBlock())) {
                            positions.add(candidatePosition);
                            modified = true;
                        }
                    }
                }
            }
        }

        return modified;
    }
}
