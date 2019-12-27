package com.suppergerrie2.sdrones.entities.rendering.models;

import com.mojang.blaze3d.platform.GlStateManager;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

public abstract class ModelDrone extends EntityModel<EntityAbstractDrone> {

    /**
     * Render the inventory of the drone
     *
     * @param entity The drone whose inventory has to be rendered
     */
    void renderInventory(EntityAbstractDrone entity) {

        //Render the weapon
        ItemStack weapon = entity.getHeldItemMainhand();

        if (!weapon.isEmpty()) { //No need to render the weapon if there is no weapon
            GlStateManager.pushMatrix();

            GlStateManager.scaled(0.75D, 0.75D, 0.75D);
            GlStateManager.translated(0.2, 1.7, -0.1);

            //Make the weapon rotate like it is being used
            GlStateManager.rotatef(180, 0, 0, 1);
            GlStateManager.rotatef((float) (Math.sin(entity.ticksExisted % 20 / 20f * Math.PI * 2) * 10f) - 90, 1, 0, 0);
            GlStateManager.rotatef(270, 0, 1, 0);

            renderItemStack(weapon, entity.world, entity);

            GlStateManager.popMatrix();
        }

        //Render the drone's inventory hovering over it
        GlStateManager.pushMatrix();

        GlStateManager.scaled(0.75D, 0.75D, 0.75D);
        GlStateManager.rotatef(180, 0, 0, 1);

        //This makes the items hover up and down instead of staying still
        GlStateManager.translated(0, -1.25 + Math.sin(entity.ticksExisted % 50 / 50f * Math.PI * 2) * 0.1f, 0);

        //The distance between each item
        float itemOffset = 0.5f;

        List<ItemStack> stacks = entity.getDroneInventory();
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) { //Dont render empty slots
                continue;
            }

            //Originally we only render max 5 items per stack. Now we render every item
            for (int j = 0; j < /*Math.max(1, Math.min(5, stack.getCount()))*/ stack.getCount(); j++) {
                renderItemStack(stack, entity.world, entity);

                //Translate up so the next item will render above this one
                GlStateManager.translated(0, itemOffset, 0);
            }
        }

        GlStateManager.popMatrix();
    }

    /**
     * Render an {@link ItemStack} in the world. It'll be rendered at the current render origin
     *
     * @param stack The {@link ItemStack} to render
     * @param world The world
     * @param entity The entity that has this stack
     */
    private void renderItemStack(ItemStack stack, World world, @Nullable LivingEntity entity) {
        GlStateManager.pushMatrix();

        IBakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, world, entity);

        model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GROUND, false);

        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        Minecraft.getInstance().getItemRenderer().renderItem(stack, model);

        GlStateManager.popMatrix();
    }

}
