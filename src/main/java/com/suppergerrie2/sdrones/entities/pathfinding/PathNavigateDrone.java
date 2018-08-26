package com.suppergerrie2.sdrones.entities.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.world.World;

public class PathNavigateDrone extends PathNavigateGround {

	public PathNavigateDrone(EntityLiving entitylivingIn, World worldIn) {
		super(entitylivingIn, worldIn);
	}

	@Override
	protected PathFinder getPathFinder() {
		this.nodeProcessor = new WalkNodeProcessorDrone();
		this.nodeProcessor.setCanEnterDoors(true);
		this.nodeProcessor.setCanOpenDoors(true);
		return new PathFinder(this.nodeProcessor);
	}

}
