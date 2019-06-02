package com.suppergerrie2.sdrones.entities.rendering.models;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * HaulerDrone - suppergerrie2 Created using Tabula 7.0.0 Fallback if I don't have a custom model
 */
@SuppressWarnings("unused")
public class ModelBasicDrone extends ModelDrone {

    private ModelRenderer body;

    public ModelBasicDrone() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        ModelRenderer trackL = new ModelRenderer(this, 0, 22);
        trackL.setRotationPoint(7.0F, 6.0F, 0.0F);
        trackL.addBox(-2.0F, -2.0F, -8.0F, 4, 4, 16, 0.0F);
        ModelRenderer light = new ModelRenderer(this, 42, 0);
        light.setRotationPoint(0.0F, -5.0F, 0.0F);
        light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 21.6F, 0.0F);
        this.body.addBox(-7.0F, -4.0F, -7.0F, 14, 8, 14, 0.0F);
        ModelRenderer trackR = new ModelRenderer(this, 0, 42);
        trackR.setRotationPoint(-7.0F, 6.0F, 0.0F);
        trackR.addBox(-2.0F, -2.0F, -8.0F, 4, 4, 16, 0.0F);
        ModelRenderer antennaTorch = new ModelRenderer(this, 0, 0);
        antennaTorch.setRotationPoint(0.0F, -6.9F, 0.0F);
        antennaTorch.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 2, 0.0F);
        this.body.addChild(trackL);
        antennaTorch.addChild(light);
        this.body.addChild(trackR);
        this.body.addChild(antennaTorch);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(this.body.offsetX, this.body.offsetY, this.body.offsetZ);
        GlStateManager.translated(this.body.rotationPointX * f5, this.body.rotationPointY * f5, this.body.rotationPointZ * f5);
        GlStateManager.scaled(0.3D, 0.3D, 0.3D);
        GlStateManager.translated(-this.body.offsetX, -this.body.offsetY, -this.body.offsetZ);
        GlStateManager.translated(-this.body.rotationPointX * f5, -this.body.rotationPointY * f5, -this.body.rotationPointZ * f5);
        this.body.render(f5);
        GlStateManager.popMatrix();

        if (entity instanceof EntityAbstractDrone) {
            this.renderInventory((EntityAbstractDrone) entity);
        }
    }

}