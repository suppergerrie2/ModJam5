package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityArcherDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelArcherDrone;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderArcherDrone extends RenderLiving<EntityArcherDrone> {

	public RenderArcherDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelArcherDrone(), 0.25f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityArcherDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/archer_drone.png");
	}
}