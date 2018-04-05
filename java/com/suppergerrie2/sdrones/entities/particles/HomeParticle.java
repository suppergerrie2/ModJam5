package com.suppergerrie2.sdrones.entities.particles;

import org.lwjgl.opengl.GL11;

import com.suppergerrie2.sdrones.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class HomeParticle extends Particle {

	private static final ResourceLocation PARTICLES_TEXTURE = new ResourceLocation(Reference.MODID, "textures/entity/particles.png");
	private static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);

	public HomeParticle(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn,
			double ySpeedIn, double zSpeedIn) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.particleMaxAge=1;
	}

    public int getFXLayer()
    {
        return 3;
    }
    
    @Override
	public void renderParticle(BufferBuilder worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
		GL11.glPushMatrix();
		GL11.glDepthFunc(GL11.GL_ALWAYS);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, this.particleAlpha);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();

		Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLES_TEXTURE);
		int i = (int)(((float)this.particleAge + partialTicks) * 15.0F / (float)this.particleMaxAge);

		if (i <= 15)
		{
			float uv_x_0 = 0;//(float)(i % 16) / 16.0F;
			float uv_x_1 = 1;//f + 0.0625f;
			float uv_y_0 = 0;//(float)(i / 16) / 16.0F;
			float uv_y_1 = 1;//f2 + 0.0625f;
			float size = .5f;//2.0F * this.size;
			float offsetFromPlayerX = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
			float offsetFromPlayerY = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
			float offsetFromPlayerZ = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);
			
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			RenderHelper.disableStandardItemLighting();
			worldRendererIn.begin(7, VERTEX_FORMAT);
			rotationX = 0;
			rotationZ = 0;
			rotationXZ = 0;
			rotationXY = 1;
			rotationYZ = 1;
			
			worldRendererIn.pos(
					(double)(offsetFromPlayerX - rotationX * size - rotationXY * size),
					(double)(offsetFromPlayerY - rotationZ * size),
					(double)(offsetFromPlayerZ - rotationYZ * size - rotationXZ * size))
					.tex((double)uv_x_1, (double)uv_y_1)
					.color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F)
					.lightmap(0, 240)
					.normal(0.0F, 1.0F, 0.0F)
					.endVertex();
			
			worldRendererIn.pos((double)(offsetFromPlayerX - rotationX * size + rotationXY * size), (double)(offsetFromPlayerY + rotationZ * size), (double)(offsetFromPlayerZ - rotationYZ * size + rotationXZ * size)).tex((double)uv_x_1, (double)uv_y_0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			worldRendererIn.pos((double)(offsetFromPlayerX + rotationX * size + rotationXY * size), (double)(offsetFromPlayerY + rotationZ * size), (double)(offsetFromPlayerZ + rotationYZ * size + rotationXZ * size)).tex((double)uv_x_0, (double)uv_y_0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			worldRendererIn.pos((double)(offsetFromPlayerX + rotationX * size - rotationXY * size), (double)(offsetFromPlayerY - rotationZ * size), (double)(offsetFromPlayerZ + rotationYZ * size - rotationXZ * size)).tex((double)uv_x_0, (double)uv_y_1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).normal(0.0F, 1.0F, 0.0F).endVertex();
			
			Tessellator.getInstance().draw();
			GlStateManager.enableLighting();
		}

		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glPopMatrix();
		GlStateManager.enableLighting();
	}
    
    @Override
    public void onUpdate()
    {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge)
        {
            this.setExpired();
        }
    }
    
    
}
