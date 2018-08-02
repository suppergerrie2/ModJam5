package com.suppergerrie2.sdrones.entities;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Predicate;
import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.AI.treefarm.EntityAICutTree;
import com.suppergerrie2.sdrones.entities.AI.treefarm.EntityAIPlantSapling;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityTreeFarmDrone extends EntityBasicDrone {

	public EntityTreeFarmDrone(World worldIn) {
//		this(worldIn, -1, -1, -1, ItemStack.EMPTY, EnumFacing.UP);
		super(worldIn);
		this.setRange(16);
	}


	/*public EntityTreeFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	@Deprecated
	public EntityTreeFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing,
			int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
	}*/

	@Override
	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAIPlantSapling(this));
		this.tasks.addTask(0, new EntityAICutTree(this));
		this.tasks.addTask(1, new EntityAIGoHome(this));
		this.tasks.addTask(2, new EntityAIWanderAvoidWater(this, 1.0f));
	}

	public ItemStack getTool() {
		return new ItemStack(Items.DIAMOND_AXE);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.world.isRemote) {
			if(!this.hasSapling()&&this.getDistanceSq(this.getHomePosition())<4) {
				this.tryGetItem(null, this.getHomePosition(), new Predicate<Item>() {
					@Override
					public boolean apply(Item input) {
						return Block.getBlockFromItem(input) instanceof BlockSapling;
					}
				});
			}

			for(int x = -1; x <= 1; x++) {
				for(int y = -1; y <= 1; y++) {
					if(this.world.getBlockState(getPosition().add(x,0,y)).getBlock() instanceof BlockLeaves) {
						this.world.destroyBlock(getPosition().add(x,0,y), true);
					}
				}
			}
		}
	}

	boolean hasSapling() {
		for(ItemStack stack : getItemStacksInDrone()) {
			if(stack!=null&&!stack.isEmpty()) {
				if(Block.getBlockFromItem(stack.getItem()) instanceof BlockSapling) {
					return true;
				}
			}
		}

		return false;
	}

	public ItemStack getSapling() {
		ItemStack[] stacks = this.getItemStacksInDrone();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i].isEmpty()) {
				continue;
			}

			if(Block.getBlockFromItem(stacks[i].getItem()) instanceof BlockSapling){
				return stacks[i];
			}
		}
		return ItemStack.EMPTY;
	}

	public void useSapling() {
		ItemStack[] stacks = this.getItemStacksInDrone();
		for(int i = 0; i < stacks.length; i++) {
			if(stacks[i].isEmpty()) {
				continue;
			}

			if(Block.getBlockFromItem(stacks[i].getItem()) instanceof BlockSapling){
				stacks[i].shrink(1);
			}
		}
		this.setItemStacksInDrone(stacks);;
	}

	public boolean placeSapling(BlockPos destination) {
		ItemStack sapling = this.getSapling();

		if(sapling.isEmpty()) {
			return false;
		}

		Block blockToPlace = Block.getBlockFromItem(sapling.getItem());
		IBlockState blockstate = blockToPlace.getStateForPlacement(world, destination, EnumFacing.DOWN, 8, 8, 8, sapling.getMetadata(), this, EnumHand.MAIN_HAND);

		world.setBlockState(destination, blockstate);

		this.useSapling();

		return true;
	}

	public void cutTree(BlockPos destination) {
		List<BlockPos> treePositions = new ArrayList<BlockPos>();

		treePositions.add(destination);

		while(walkTree(treePositions));

		for(BlockPos pos : treePositions) {
			world.destroyBlock(pos, true);
		}
	}

	public boolean walkTree(List<BlockPos> positions) {

		boolean modified = false;
		List<BlockPos> copy = new ArrayList<BlockPos>(positions);
		for(BlockPos pos : copy) {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					for(int y = 0; y <= 1; y++) {
						BlockPos pos2 = pos.add(x,y,z);

						if(!positions.contains(pos2)&&this.world.getBlockState(pos2).getBlock().isWood(world, pos2)) {
							positions.add(pos2);
							modified = true;
						}
					}
				}
			}
		}

		return modified;
	}
}
