package com.suppergerrie2.sdrones.entities.pathfinding;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessorDrone extends WalkNodeProcessor {

	@Override
	protected PathNodeType getPathNodeTypeRaw(IBlockAccess blockAccess, int x, int y, int z) {

		BlockPos blockpos = new BlockPos(x, y, z);
		IBlockState iblockstate = blockAccess.getBlockState(blockpos);
		Material material = iblockstate.getMaterial();

		if (material == Material.WATER) {
			return PathNodeType.OPEN;
		}

		return super.getPathNodeTypeRaw(blockAccess, x, y, z);
	}

}
