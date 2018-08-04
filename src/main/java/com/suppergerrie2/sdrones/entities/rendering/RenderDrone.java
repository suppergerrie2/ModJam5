package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelDrone;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderDrone extends RenderLiving<EntityBasicDrone> {

	String texture;
	
	public RenderDrone(RenderManager rendermanagerIn, String texture) {
		super(rendermanagerIn, new ModelDrone(), 0.25f);
		this.texture = texture;
	}
	
	public RenderDrone(RenderManager rendermanagerIn, ModelBase model, String texture) {
		super(rendermanagerIn, model, 0.25f);
		this.texture = texture;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBasicDrone entity) {
		return new ResourceLocation(Reference.MODID+":textures/entity/"+texture+".png");
	}
}