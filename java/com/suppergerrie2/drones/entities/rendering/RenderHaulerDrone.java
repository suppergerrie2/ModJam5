package com.suppergerrie2.drones.entities.rendering;

import com.suppergerrie2.drones.Reference;
import com.suppergerrie2.drones.entities.EntityHaulerDrone;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderHaulerDrone extends RenderLiving<EntityHaulerDrone> {

	public RenderHaulerDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelHaulerDrone(), 0.25f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityHaulerDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/hauler_drone.png");
	}

}
