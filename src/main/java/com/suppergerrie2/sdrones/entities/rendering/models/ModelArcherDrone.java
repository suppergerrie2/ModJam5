package com.suppergerrie2.sdrones.entities.rendering.models;

import com.suppergerrie2.sdrones.entities.EntityArcherDrone;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * ModelArcherDroneHaze33E - Haze33E
 * Created using Tabula 7.0.0
 */
public class ModelArcherDrone extends ModelDrone {
    public ModelRenderer bodyMain;
    public ModelRenderer trackR;
    public ModelRenderer trackL;
    public ModelRenderer bodyDetailMain;
    public ModelRenderer bodyDetailL;
    public ModelRenderer bodyDetailR;
    public ModelRenderer Antenna;
    public ModelRenderer light;

    public ModelArcherDrone() {
        this.textureWidth = 110;
        this.textureHeight = 28;
        this.Antenna = new ModelRenderer(this, 0, 0);
        this.Antenna.setRotationPoint(0.0F, -3.5F, 0.0F);
        this.Antenna.addBox(-1.0F, -2.5F, -1.0F, 2, 5, 2, 0.0F);
        this.bodyDetailR = new ModelRenderer(this, 2, 0);
        this.bodyDetailR.setRotationPoint(-5.0F, 0.0F, -2.0F);
        this.bodyDetailR.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 8, 0.0F);
        this.bodyDetailL = new ModelRenderer(this, 2, 0);
        this.bodyDetailL.setRotationPoint(5.0F, 0.0F, -2.0F);
        this.bodyDetailL.addBox(-1.0F, -1.0F, -4.0F, 2, 2, 8, 0.0F);
        this.bodyDetailMain = new ModelRenderer(this, 33, 0);
        this.bodyDetailMain.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.bodyDetailMain.addBox(-4.0F, -1.0F, -6.0F, 8, 2, 12, 0.0F);
        this.trackL = new ModelRenderer(this, 0, 0);
        this.trackL.setRotationPoint(6.0F, 4.0F, 0.0F);
        this.trackL.addBox(-2.0F, -2.0F, -12.0F, 4, 4, 24, 0.0F);
        this.trackR = new ModelRenderer(this, 0, 0);
        this.trackR.setRotationPoint(-6.0F, 4.0F, 0.0F);
        this.trackR.addBox(-2.0F, -2.0F, -12.0F, 4, 4, 24, 0.0F);
        this.light = new ModelRenderer(this, 0, 12);
        this.light.setRotationPoint(0.0F, -4.0F, 0.0F);
        this.light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
        this.bodyMain = new ModelRenderer(this, 62, 12);
        this.bodyMain.setRotationPoint(0.0F, 22.2F, 0.0F);
        this.bodyMain.addBox(-6.0F, -2.0F, -6.0F, 12, 4, 12, 0.0F);
        this.bodyDetailMain.addChild(this.Antenna);
        this.bodyDetailMain.addChild(this.bodyDetailR);
        this.bodyDetailMain.addChild(this.bodyDetailL);
        this.bodyMain.addChild(this.bodyDetailMain);
        this.bodyMain.addChild(this.trackL);
        this.bodyMain.addChild(this.trackR);
        this.Antenna.addChild(this.light);
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
        
        //Weapon
		GlStateManager.pushMatrix();

		GlStateManager.scale(0.75D, 0.75D, 0.75D);
		GlStateManager.translate(0.2, 1.7, -0.1);
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.rotate(-45, 1, 0, 0);
		GlStateManager.rotate(270, 0, 1, 0);
		
		ItemStack weapon = ((EntityArcherDrone)entity).getTool();

		renderItemStack(weapon, entity.world, (EntityLivingBase) entity);
		
		GlStateManager.popMatrix();
    }
}
