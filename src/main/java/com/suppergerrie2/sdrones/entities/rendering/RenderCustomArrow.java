package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.entities.EntityDroneArrow;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCustomArrow extends RenderArrow<EntityDroneArrow> {

	public RenderCustomArrow(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDroneArrow entity) {
		return new ResourceLocation("textures/entity/arrow.png");
	}

}
