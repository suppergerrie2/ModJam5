package com.suppergerrie2.drones.entities.rendering;

import com.suppergerrie2.drones.Reference;
import com.suppergerrie2.drones.entities.EntityFighterDrone;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderFighterDrone extends RenderLiving<EntityFighterDrone> {

	public RenderFighterDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelFighterDrone(), 0.25f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFighterDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/fighter_drone.png");
	}

}
