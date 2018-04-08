package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCropFarmDrone extends RenderLiving<EntityCropFarmDrone> {

	public RenderCropFarmDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelCropFarmDrone(), 0.25f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCropFarmDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/crop_farm_drone.png");
	}

}
