package com.suppergerrie2.sdrones.entities.rendering;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelDrone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderDrone extends MobRenderer<EntityAbstractDrone, ModelDrone> {

    private final String texture;

    public RenderDrone(EntityRendererManager renderManagerIn, ModelDrone model, String texture) {
        super(renderManagerIn, model, 0.25f);
        this.texture = texture;
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityAbstractDrone entity) {
        return new ResourceLocation(Reference.MODID + ":textures/entity/" + texture + ".png");
    }

}