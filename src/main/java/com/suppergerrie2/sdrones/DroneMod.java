package com.suppergerrie2.sdrones;

import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.suppergerrie2.sdrones.entities.EntityArcherDrone;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.entities.EntityCropFarmDrone;
import com.suppergerrie2.sdrones.entities.EntityDroneArrow;
import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.init.ModItems;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import com.suppergerrie2.sdrones.proxies.IProxy;
import com.suppergerrie2.sdrones.upgrades.DroneUpgrade;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(modid = Reference.MODID, name=Reference.MODNAME, version=Reference.VERSION, updateJSON = Reference.UPDATE_URL, acceptedMinecraftVersions=Reference.ACCEPTED_MINECRAFT_VERSIONS)
@Mod.EventBusSubscriber(modid=Reference.MODID)
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

		registerDrone(EntityHaulerDrone.class, "hauler_drone", EntityHaulerDrone::new);
		registerDrone(EntityFighterDrone.class, "fighter_drone", EntityFighterDrone::new);
		registerDrone(EntityTreeFarmDrone.class, "tree_farm_drone", EntityTreeFarmDrone::new);
		registerDrone(EntityCropFarmDrone.class, "crop_farm_drone", EntityCropFarmDrone::new);
		registerDrone(EntityArcherDrone.class, "archer_drone", EntityArcherDrone::new);

		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, "drone_arrow"), EntityDroneArrow.class, "drone_arrow", entityID++, this, 80, 1, true);

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

	@SubscribeEvent
	public static void registerRegistry(RegistryEvent.NewRegistry event) {
		new RegistryBuilder<DroneUpgrade>()
			.setName(new ResourceLocation(Reference.MODID, Reference.UPGRADE_REGISTRY))
			.setType(DroneUpgrade.class)
			.create();
	}

	private <T extends EntityBasicDrone> void registerDrone(Class<T> droneClass, String name, Function<World, T> droneCreator) {
		EntityRegistry.registerModEntity(new ResourceLocation(Reference.MODID, name), droneClass, name, entityID++, this, 80, 1, true);

		ModItems.registerItem(new ItemSpawnDrone<T>("item_"+name, new ResourceLocation(Reference.MODID, name), droneCreator));
	}

}
