package com.suppergerrie2.sdrones.entities;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.suppergerrie2.sdrones.entities.AI.EntityAIGoHome;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIFarmCrop;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPlantCrop;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPrepareFarmland;
import com.suppergerrie2.sdrones.entities.AI.cropfarm.EntityAIPrepareLand;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class EntityCropFarmDrone extends EntityBasicDrone {
	
	public EntityCropFarmDrone(World worldIn) {
		this(worldIn, 0, 0, 0, ItemStack.EMPTY, EnumFacing.UP);
	}

	public EntityCropFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing) {
		this(worldIn, x, y, z, spawnedWith, facing, 1);
	}

	public EntityCropFarmDrone(World worldIn, double x, double y, double z, ItemStack spawnedWith, EnumFacing facing, int carrySize) {
		super(worldIn, x, y, z, spawnedWith, facing, carrySize);
		this.setRange(4);
		((PathNavigateGround)this.getNavigator()).setCanSwim(true);
	}
	
	public EntityAIPrepareFarmland aiPrepareFarmLand;
	
	@Override
	protected void initEntityAI() {
		//13
		EntityAIPrepareLand aiPrepareLand = new EntityAIPrepareLand(this);
		this.tasks.addTask(0, aiPrepareLand);
		aiPrepareFarmLand =  new EntityAIPrepareFarmland(this, aiPrepareLand);
		this.tasks.addTask(1, aiPrepareFarmLand);
		this.tasks.addTask(2, new EntityAIPlantCrop(this));
		this.tasks.addTask(3, new EntityAIFarmCrop(this));
		this.tasks.addTask(4, new EntityAIGoHome(this));
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
						this.insertItemsInBlock(getHomePosition());
					}
				} else if(!this.hasSeeds()&&this.getDistanceSq(this.getHomePosition())<4) {
					this.tryGetItem(null, this.getHomePosition(), new Predicate<Item>()
			        {
			            public boolean apply(@Nullable Item item)
			            {
			                return item instanceof IPlantable;
			            }
			        });
				}
			}  else {
				if(!this.hasDirt()&&this.getDistanceSq(this.getHomePosition())<4) {
					this.tryGetItem(Item.getItemFromBlock(Blocks.DIRT), this.getHomePosition(), null);
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
