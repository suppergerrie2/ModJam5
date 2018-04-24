package com.suppergerrie2.sdrones.proxies;

import com.suppergerrie2.sdrones.entities.EntityArcherDrone;
import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;
import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderArcherDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderDrone;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(EntityHaulerDrone.class, new IRenderFactory<EntityHaulerDrone>() {

			@Override
			public Render<? super EntityHaulerDrone> createRenderFor(RenderManager manager) {
				return new RenderDrone(manager, "hauler_drone");
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFighterDrone.class, new IRenderFactory<EntityFighterDrone>() {

			@Override
			public Render<? super EntityFighterDrone> createRenderFor(RenderManager manager) {
				return new RenderDrone(manager, "fighter_drone");
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTreeFarmDrone.class, new IRenderFactory<EntityTreeFarmDrone>() {

			@Override
			public Render<? super EntityTreeFarmDrone> createRenderFor(RenderManager manager) {
				return new RenderDrone(manager, "tree_farm_drone");
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityCropFarmDrone.class, new IRenderFactory<EntityCropFarmDrone>() {

			@Override
			public Render<? super EntityCropFarmDrone> createRenderFor(RenderManager manager) {
				return new RenderDrone(manager, "crop_farm_drone");
			}
		});
		
		//TODO: Fix bow rendering
		RenderingRegistry.registerEntityRenderingHandler(EntityArcherDrone.class, new IRenderFactory<EntityArcherDrone>() {

			@Override
			public Render<? super EntityArcherDrone> createRenderFor(RenderManager manager) {
				return new RenderArcherDrone(manager);
			}
		});
	}

	@Override
	public void init(FMLInitializationEvent event) {
		
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		
	}

}
