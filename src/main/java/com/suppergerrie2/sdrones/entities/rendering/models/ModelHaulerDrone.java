package com.suppergerrie2.sdrones.entities.rendering.models;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * ModelHaulerDroneHaze33E - Haze33E Created using Tabula 7.0.0
 */
public class ModelHaulerDrone extends ModelDrone {

    public ModelRenderer bodyMain;
    public ModelRenderer trackLeft;
    public ModelRenderer trackRight;
    public ModelRenderer bodyDetailMain;
    public ModelRenderer bodyDetailL;
    public ModelRenderer bodyDetailR;
    public ModelRenderer antenna;
    public ModelRenderer light;

    public ModelHaulerDrone() {
        this.textureWidth = 128;
        this.textureHeight = 47;
        this.trackRight = new ModelRenderer(this, 4, 0);
        this.trackRight.setRotationPoint(-8.0F, 4.0F, 0.0F);
        this.trackRight.addBox(-2.0F, -2.0F, -12.0F, 4, 4, 24, 0.0F);
        this.bodyMain = new ModelRenderer(this, 63, 8);
        this.bodyMain.setRotationPoint(0.0F, 22.100000000000005F, 0.0F);
        this.bodyMain.addBox(-8.0F, -2.0F, -8.0F, 16, 4, 16, 0.0F);
        this.trackLeft = new ModelRenderer(this, 4, 0);
        this.trackLeft.setRotationPoint(8.0F, 4.0F, 0.0F);
        this.trackLeft.addBox(-2.0F, -2.0F, -12.0F, 4, 4, 24, 0.0F);
        this.bodyDetailR = new ModelRenderer(this, 0, 0);
        this.bodyDetailR.setRotationPoint(-7.0F, 0.0F, 0.0F);
        this.bodyDetailR.addBox(-1.0F, -1.0F, -6.0F, 2, 3, 12, 0.0F);
        this.antenna = new ModelRenderer(this, 0, 0);
        this.antenna.setRotationPoint(0.0F, -3.5F, 0.0F);
        this.antenna.addBox(-1.0F, -2.5F, -1.0F, 2, 5, 2, 0.0F);
        this.bodyDetailMain = new ModelRenderer(this, 0, 28);
        this.bodyDetailMain.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.bodyDetailMain.addBox(-6.0F, -1.0F, -8.0F, 12, 3, 16, 0.0F);
        this.bodyDetailL = new ModelRenderer(this, 0, 0);
        this.bodyDetailL.setRotationPoint(7.0F, 0.0F, 0.0F);
        this.bodyDetailL.addBox(-1.0F, -1.0F, -6.0F, 2, 3, 12, 0.0F);
        this.light = new ModelRenderer(this, 0, 15);
        this.light.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        this.bodyMain.addChild(this.trackRight);
        this.bodyMain.addChild(this.trackLeft);
        this.bodyDetailMain.addChild(this.bodyDetailR);
        this.bodyDetailMain.addChild(this.antenna);
        this.bodyMain.addChild(this.bodyDetailMain);
        this.bodyDetailMain.addChild(this.bodyDetailL);
        this.antenna.addChild(this.light);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        GlStateManager.pushMatrix();
        GlStateManager.translated(this.bodyMain.offsetX, this.bodyMain.offsetY, this.bodyMain.offsetZ);
        GlStateManager.translated(this.bodyMain.rotationPointX * f5, this.bodyMain.rotationPointY * f5, this.bodyMain.rotationPointZ * f5);
        GlStateManager.scaled(0.3D, 0.3D, 0.3D);
        GlStateManager.translated(-this.bodyMain.offsetX, -this.bodyMain.offsetY, -this.bodyMain.offsetZ);
        GlStateManager.translated(-this.bodyMain.rotationPointX * f5, -this.bodyMain.rotationPointY * f5, -this.bodyMain.rotationPointZ * f5);
        this.bodyMain.render(f5);
        GlStateManager.popMatrix();

        if (entity instanceof EntityAbstractDrone) {
            this.renderInventory((EntityAbstractDrone) entity);
        }
    }
}
