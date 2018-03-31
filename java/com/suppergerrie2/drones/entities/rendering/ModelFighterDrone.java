package com.suppergerrie2.drones.entities.rendering;

import com.suppergerrie2.drones.entities.EntityFighterDrone;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * HaulerDrone - suppergerrie2
 * Created using Tabula 7.0.0
 */
public class ModelFighterDrone extends ModelBase {
	public ModelRenderer body;
	public ModelRenderer trackL;
	public ModelRenderer trackR;
	public ModelRenderer antennatorch;
	public ModelRenderer light;

	public ModelFighterDrone() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.trackL = new ModelRenderer(this, 0, 22);
		this.trackL.setRotationPoint(7.0F, 6.0F, 0.0F);
		this.trackL.addBox(-2.0F, -2.0F, -8.0F, 4, 4, 16, 0.0F);
		this.light = new ModelRenderer(this, 42, 0);
		this.light.setRotationPoint(0.0F, -5.0F, 0.0F);
		this.light.addBox(-2.0F, -2.0F, -2.0F, 4, 4, 4, 0.0F);
		this.body = new ModelRenderer(this, 0, 0);
		this.body.setRotationPoint(0.0F, 21.6F, 0.0F);
		this.body.addBox(-7.0F, -4.0F, -7.0F, 14, 8, 14, 0.0F);
		this.trackR = new ModelRenderer(this, 0, 42);
		this.trackR.setRotationPoint(-7.0F, 6.0F, 0.0F);
		this.trackR.addBox(-2.0F, -2.0F, -8.0F, 4, 4, 16, 0.0F);
		this.antennatorch = new ModelRenderer(this, 0, 0);
		this.antennatorch.setRotationPoint(0.0F, -6.9F, 0.0F);
		this.antennatorch.addBox(-1.0F, -3.0F, -1.0F, 2, 6, 2, 0.0F);
		this.body.addChild(this.trackL);
		this.antennatorch.addChild(this.light);
		this.body.addChild(this.trackR);
		this.body.addChild(this.antennatorch);
	}

	float itemOffset = 0.5f;

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) { 
		GlStateManager.pushMatrix();
		GlStateManager.translate(this.body.offsetX, this.body.offsetY, this.body.offsetZ);
		GlStateManager.translate(this.body.rotationPointX * f5, this.body.rotationPointY * f5, this.body.rotationPointZ * f5);
		GlStateManager.scale(0.3D, 0.3D, 0.3D);
		GlStateManager.translate(-this.body.offsetX, -this.body.offsetY, -this.body.offsetZ);
		GlStateManager.translate(-this.body.rotationPointX * f5, -this.body.rotationPointY * f5, -this.body.rotationPointZ * f5);
		this.body.render(f5);
		GlStateManager.popMatrix();

		GlStateManager.pushMatrix();

		GlStateManager.scale(0.75D, 0.75D, 0.75D);
		GlStateManager.translate(0.2, 1.8, -0.2);
		GlStateManager.rotate(180, 0, 0, 1);
		GlStateManager.rotate((float) (Math.sin(entity.ticksExisted%20/20f*Math.PI*2)*10f), 1, 0, 0);
		GlStateManager.rotate(90, 0, 1, 0);
		
		ItemStack weapon = ((EntityFighterDrone)entity).getWeapon();

		renderItemStack(weapon, entity.world);
		
		GlStateManager.popMatrix();

	}

	public void renderItemStack(ItemStack stack, World world) {
		GlStateManager.pushMatrix();

		IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, world, null);
		model = ForgeHooksClient.handleCameraTransforms(model, ItemCameraTransforms.TransformType.GROUND, false);

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);


		GlStateManager.popMatrix();
	}

	/**
	 * This is a helper function from Tabula to set the rotation of model parts
	 */
	public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}
