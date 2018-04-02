package com.suppergerrie2.sdrones.proxies;

import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderFighterDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderHaulerDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderTreeFarmDrone;

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
//		IRenderFactory<EntityHaulerDrone> test = new IRenderFactory<EntityHaulerDrone>() {
//
//			@Override
//			public Render<? super EntityHaulerDrone> createRenderFor(RenderManager manager) {
//				return new RenderHaulerDrone(manager);
//			}
//		};
//		
		RenderingRegistry.registerEntityRenderingHandler(EntityHaulerDrone.class, new IRenderFactory<EntityHaulerDrone>() {

			@Override
			public Render<? super EntityHaulerDrone> createRenderFor(RenderManager manager) {
				return new RenderHaulerDrone(manager);
			}
		});
		
//		IRenderFactory<EntityFighterDrone> test2 = new IRenderFactory<EntityFighterDrone>() {
//
//			@Override
//			public Render<? super EntityFighterDrone> createRenderFor(RenderManager manager) {
//				return new RenderFighterDrone(manager);
//			}
//		};
		
		RenderingRegistry.registerEntityRenderingHandler(EntityFighterDrone.class, new IRenderFactory<EntityFighterDrone>() {

			@Override
			public Render<? super EntityFighterDrone> createRenderFor(RenderManager manager) {
				return new RenderFighterDrone(manager);
			}
		});
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTreeFarmDrone.class, new IRenderFactory<EntityTreeFarmDrone>() {

			@Override
			public Render<? super EntityTreeFarmDrone> createRenderFor(RenderManager manager) {
				return new RenderTreeFarmDrone(manager);
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
