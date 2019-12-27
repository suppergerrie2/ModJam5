package com.suppergerrie2.sdrones.entities.rendering.models;

import com.mojang.blaze3d.platform.GlStateManager;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.client.renderer.entity.model.RendererModel;

/**
 * ModelFighterDroneHaze33E - Haze33E Created using Tabula 7.0.0
 */
@SuppressWarnings("FieldCanBeLocal")
public class ModelFighterDrone extends ModelDrone {

    private final RendererModel bodyMain;
    private final RendererModel bodyDetailMain;
    private final RendererModel trackL;
    private final RendererModel trackR;
    private final RendererModel bodyDetailR;
    private final RendererModel bodyDetailL;
    private final RendererModel bodyDetailTop;

    public ModelFighterDrone() {
        this.textureWidth = 120;
        this.textureHeight = 48;
        this.bodyDetailL = new RendererModel(this, 50, 1);
        this.bodyDetailL.setRotationPoint(7.0F, 0.0F, 0.0F);
        this.bodyDetailL.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.bodyDetailTop = new RendererModel(this, 0, 0);
        this.bodyDetailTop.setRotationPoint(0.0F, -2.0F, 0.0F);
        this.bodyDetailTop.addBox(-2.0F, -1.0F, -2.0F, 4, 2, 4, 0.0F);
        this.trackL = new RendererModel(this, 0, 22);
        this.trackL.setRotationPoint(8.0F, 6.0F, 0.0F);
        this.trackL.addBox(-3.0F, -3.0F, -10.0F, 6, 6, 20, 0.0F);
        this.bodyDetailMain = new RendererModel(this, 64, 0);
        this.bodyDetailMain.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.bodyDetailMain.addBox(-6.0F, -1.0F, -8.0F, 12, 2, 16, 0.0F);
        this.bodyMain = new RendererModel(this, 0, 0);
        this.bodyMain.setRotationPoint(0.0F, 21.3F, 0.0F);
        this.bodyMain.addBox(-8.0F, -3.0F, -8.0F, 16, 6, 16, 0.0F);
        this.trackR = new RendererModel(this, 0, 22);
        this.trackR.setRotationPoint(-8.0F, 6.0F, 0.0F);
        this.trackR.addBox(-3.0F, -3.0F, -10.0F, 6, 6, 20, 0.0F);
        this.bodyDetailR = new RendererModel(this, 50, 1);
        this.bodyDetailR.setRotationPoint(-7.0F, 0.0F, 0.0F);
        this.bodyDetailR.addBox(-1.0F, -1.0F, -6.0F, 2, 2, 12, 0.0F);
        this.bodyDetailMain.addChild(this.bodyDetailL);
        this.bodyDetailMain.addChild(this.bodyDetailTop);
        this.bodyMain.addChild(this.trackL);
        this.bodyMain.addChild(this.bodyDetailMain);
        this.bodyMain.addChild(this.trackR);
        this.bodyDetailMain.addChild(this.bodyDetailR);
    }

    @Override
    public void render(EntityAbstractDrone entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(this.bodyMain.offsetX, this.bodyMain.offsetY, this.bodyMain.offsetZ);
        GlStateManager.translated(this.bodyMain.rotationPointX * f5, this.bodyMain.rotationPointY * f5, this.bodyMain.rotationPointZ * f5);
        GlStateManager.scaled(0.3D, 0.3D, 0.3D);
        GlStateManager.translated(-this.bodyMain.offsetX, -this.bodyMain.offsetY, -this.bodyMain.offsetZ);
        GlStateManager.translated(-this.bodyMain.rotationPointX * f5, -this.bodyMain.rotationPointY * f5, -this.bodyMain.rotationPointZ * f5);
        this.bodyMain.render(f5);
        GlStateManager.popMatrix();

        this.renderInventory(entity);
    }
}
