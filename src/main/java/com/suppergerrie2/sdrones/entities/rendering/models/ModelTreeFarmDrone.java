package com.suppergerrie2.sdrones.entities.rendering.models;

import com.mojang.blaze3d.platform.GlStateManager;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.entity.Entity;

/**
 * ModelTreeFarmDroneHaze33E - Haze33E Created using Tabula 7.0.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class ModelTreeFarmDrone extends ModelDrone {

    private RendererModel bodyMain;
    private RendererModel bodyDetail;
    private RendererModel trackR;
    private RendererModel trackL;
    private RendererModel bodyDetailL;
    private RendererModel bodyDetailR;
    private RendererModel antenna;
    private RendererModel light;

    public ModelTreeFarmDrone() {
        this.textureWidth = 120;
        this.textureHeight = 48;
        this.bodyMain = new RendererModel(this, 0, 0);
        this.bodyMain.setRotationPoint(0.0F, 21.900000000000002F, 0.0F);
        this.bodyMain.addBox(-8.0F, -3.0F, -8.0F, 16, 6, 16, 0.0F);
        this.trackL = new RendererModel(this, 0, 24);
        this.trackL.setRotationPoint(8.0F, 5.0F, 0.0F);
        this.trackL.addBox(-3.0F, -2.0F, -10.0F, 6, 4, 20, 0.0F);
        this.bodyDetailR = new RendererModel(this, 48, 0);
        this.bodyDetailR.setRotationPoint(-7.0F, 0.0F, 0.0F);
        this.bodyDetailR.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.antenna = new RendererModel(this, 0, 0);
        this.antenna.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.antenna.addBox(-1.0F, -2.0F, -1.0F, 2, 4, 2, 0.0F);
        this.trackR = new RendererModel(this, 0, 24);
        this.trackR.mirror = true;
        this.trackR.setRotationPoint(-8.0F, 5.0F, 0.0F);
        this.trackR.addBox(-3.0F, -2.0F, -10.0F, 6, 4, 20, 0.0F);
        this.bodyDetail = new RendererModel(this, 64, 0);
        this.bodyDetail.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.bodyDetail.addBox(-6.0F, -1.0F, -8.0F, 12, 2, 16, 0.0F);
        this.light = new RendererModel(this, 0, 6);
        this.light.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        this.bodyDetailL = new RendererModel(this, 48, 0);
        this.bodyDetailL.setRotationPoint(7.0F, 0.0F, 0.0F);
        this.bodyDetailL.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.bodyMain.addChild(this.trackL);
        this.bodyDetail.addChild(this.bodyDetailR);
        this.bodyDetail.addChild(this.antenna);
        this.bodyMain.addChild(this.trackR);
        this.bodyMain.addChild(this.bodyDetail);
        this.antenna.addChild(this.light);
        this.bodyDetail.addChild(this.bodyDetailL);
    }

    @Override
    public void render(EntityAbstractDrone entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef(this.bodyMain.offsetX, this.bodyMain.offsetY, this.bodyMain.offsetZ);
        GlStateManager.translatef(this.bodyMain.rotationPointX * f5, this.bodyMain.rotationPointY * f5, this.bodyMain.rotationPointZ * f5);
        GlStateManager.scaled(0.3D, 0.3D, 0.3D);
        GlStateManager.translatef(-this.bodyMain.offsetX, -this.bodyMain.offsetY, -this.bodyMain.offsetZ);
        GlStateManager.translatef(-this.bodyMain.rotationPointX * f5, -this.bodyMain.rotationPointY * f5, -this.bodyMain.rotationPointZ * f5);
        this.bodyMain.render(f5);
        GlStateManager.popMatrix();

        this.renderInventory(entity);
    }
}
