package com.suppergerrie2.sdrones.entities.rendering.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

/**
 * ModelCropfarmDroneHaze33E - Haze33E
 * Created using Tabula 7.0.0
 */
public class ModelCropFarmDrone extends ModelDrone {
    public ModelRenderer bodyMain;
    public ModelRenderer bodyDetail;
    public ModelRenderer trackR;
    public ModelRenderer trackL;
    public ModelRenderer bodyDetailL;
    public ModelRenderer bodyDetailR;
    public ModelRenderer antenna;
    public ModelRenderer light;

    public ModelCropFarmDrone() {
        this.textureWidth = 120;
        this.textureHeight = 42;
        this.bodyMain = new ModelRenderer(this, 0, 0);
        this.bodyMain.setRotationPoint(0.0F, 22.500000000000014F, 0.0F);
        this.bodyMain.addBox(-8.0F, -1.0F, -8.0F, 16, 2, 16, 0.0F);
        this.trackR = new ModelRenderer(this, 0, 18);
        this.trackR.setRotationPoint(-7.0F, 3.0F, 0.0F);
        this.trackR.addBox(-1.0F, -2.0F, -10.0F, 2, 4, 20, 0.0F);
        this.bodyDetailL = new ModelRenderer(this, 48, 0);
        this.bodyDetailL.setRotationPoint(7.0F, 0.0F, 0.0F);
        this.bodyDetailL.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.antenna = new ModelRenderer(this, 0, 0);
        this.antenna.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.antenna.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.bodyDetail = new ModelRenderer(this, 64, 0);
        this.bodyDetail.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.bodyDetail.addBox(-6.0F, -1.0F, -8.0F, 12, 2, 16, 0.0F);
        this.trackL = new ModelRenderer(this, 0, 18);
        this.trackL.setRotationPoint(7.0F, 3.0F, 0.0F);
        this.trackL.addBox(-1.0F, -2.0F, -10.0F, 2, 4, 20, 0.0F);
        this.light = new ModelRenderer(this, 0, 6);
        this.light.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        this.bodyDetailR = new ModelRenderer(this, 48, 0);
        this.bodyDetailR.setRotationPoint(-7.0F, 0.0F, 0.0F);
        this.bodyDetailR.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.bodyMain.addChild(this.trackR);
        this.bodyDetail.addChild(this.bodyDetailL);
        this.bodyDetail.addChild(this.antenna);
        this.bodyMain.addChild(this.bodyDetail);
        this.bodyMain.addChild(this.trackL);
        this.antenna.addChild(this.light);
        this.bodyDetail.addChild(this.bodyDetailR);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
        GlStateManager.pushMatrix();
        GlStateManager.translate(this.bodyMain.offsetX, this.bodyMain.offsetY, this.bodyMain.offsetZ);
        GlStateManager.translate(this.bodyMain.rotationPointX * f5, this.bodyMain.rotationPointY * f5, this.bodyMain.rotationPointZ * f5);
        GlStateManager.scale(0.3D, 0.3D, 0.3D);
        GlStateManager.translate(-this.bodyMain.offsetX, -this.bodyMain.offsetY, -this.bodyMain.offsetZ);
        GlStateManager.translate(-this.bodyMain.rotationPointX * f5, -this.bodyMain.rotationPointY * f5, -this.bodyMain.rotationPointZ * f5);
        this.bodyMain.render(f5);
        GlStateManager.popMatrix();
        
        this.renderInventory(entity);
    }
}
