package com.suppergerrie2.sdrones;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.sdrones.entities.EntityArcherDrone;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;
import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import com.suppergerrie2.sdrones.proxies.IProxy;

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
	
	@SidedProxy(modId=Reference.MODID,clientSide="com.suppergerrie2.sdrones.proxies.ClientProxy", serverSide="com.suppergerrie2.sdrones.proxies.ServerProxy")
	public static IProxy proxy;
	
	public static Logger logger;
	
	static int entityID = 0;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();

		registerDrone(EntityHaulerDrone.class, "hauler_drone");
		registerDrone(EntityFighterDrone.class, "fighter_drone");
		registerDrone(EntityTreeFarmDrone.class, "tree_farm_drone");
		registerDrone(EntityCropFarmDrone.class, "crop_farm_drone");
		registerDrone(EntityArcherDrone.class, "archer_drone");

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
	
	private void registerDrone(Class<? extends EntityBasicDrone> droneClass, String name) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, name), droneClass, name, entityID++, this, 80, 1, true);
	}
	
}
