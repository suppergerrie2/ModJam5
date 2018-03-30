package com.suppergerrie2.drones;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.drones.entities.EntityBasicDrone;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = Reference.MODID, name=Reference.MODNAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.ACCEPTED_MINECRAFT_VERSIONS)
public class DroneMod {
	
	@Instance
	public static DroneMod instance;
	
	public static Logger logger;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, "basic_drone"), EntityBasicDrone.class, "basic_drone", 0, this, 80, 1, true);
		
		logger.info("preInit");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		logger.info("init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("postInit");
	}
	
}
