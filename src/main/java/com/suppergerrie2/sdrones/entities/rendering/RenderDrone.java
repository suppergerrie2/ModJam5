package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.util.ResourceLocation;

public class RenderDrone extends RenderLiving<EntityAbstractDrone> {

    private final String texture;

    public RenderDrone(RenderManager renderManagerIn, ModelBase model, String texture) {
        super(renderManagerIn, model, 0.25f);
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAbstractDrone entity) {
        return new ResourceLocation(Reference.MODID + ":textures/entity/" + texture + ".png");
    }
}