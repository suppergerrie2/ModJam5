package com.suppergerrie2.drones;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.drones.entities.EntityFighterDrone;
import com.suppergerrie2.drones.entities.EntityHaulerDrone;
import com.suppergerrie2.drones.networking.DronesPacketHandler;
import com.suppergerrie2.drones.proxies.IProxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = Reference.MODID, name=Reference.MODNAME, version=Reference.VERSION, acceptedMinecraftVersions=Reference.ACCEPTED_MINECRAFT_VERSIONS)
public class DroneMod {
	
	@Instance
	public static DroneMod instance;
	
	@SidedProxy(modId=Reference.MODID,clientSide="com.suppergerrie2.drones.proxies.ClientProxy", serverSide="com.suppergerrie2.drones.proxies.ServerProxy")
	public static IProxy proxy;
	
	public static Logger logger;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		
		
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, "fighter_drone"), EntityFighterDrone.class, "fighter_drone", 0, this, 80, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, "hauler_drone"), EntityHaulerDrone.class, "hauler_drone", 1, this, 80, 1, true);
		
		proxy.preInit(event);
		logger.info("preInit");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		DronesPacketHandler.register();
		logger.info("init");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		logger.info("postInit");
	}
	
}
