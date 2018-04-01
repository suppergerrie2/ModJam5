package com.suppergerrie2.drones.entities.rendering;

import com.suppergerrie2.drones.Reference;
import com.suppergerrie2.drones.entities.EntityTreeFarmDrone;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderTreeFarmDrone extends RenderLiving<EntityTreeFarmDrone> {

	public RenderTreeFarmDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelTreeFarmDrone(), 0.25f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityTreeFarmDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/tree_farm_drone.png");
	}

}
