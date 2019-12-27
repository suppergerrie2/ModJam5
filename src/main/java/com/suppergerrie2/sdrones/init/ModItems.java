package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.DroneMod;
import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, Reference.MODID);

    public static final RegistryObject<Item> DRONE_STICK = ITEMS.register("drone_stick", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SPAWN_HAULER_DRONE = ITEMS.register("hauler_drone", () -> new ItemSpawnDrone<>(EntityHaulerDrone::new));
    public static final RegistryObject<Item> SPAWN_FIGHTER_DRONE = ITEMS.register("fighter_drone", () -> new ItemSpawnDrone<>(EntityFighterDrone::new));
    public static final RegistryObject<Item> SPAWN_TREE_FARM_DRONE = ITEMS.register("tree_farm_drone", () -> new ItemSpawnDrone<>(EntityTreeFarmDrone::new));
//    private static final Logger LOGGER = LogManager.getLogger();

//    @SubscribeEvent
//    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
//        LOGGER.info("Registering items...");
//        event.getRegistry().registerAll(
//                new Item(new Item.Properties()).setRegistryName(new ResourceLocation(Reference.MODID, "drone_stick")),
//                new ItemSpawnDrone<>(EntityHaulerDrone::new),
//                new ItemSpawnDrone<>(EntityFighterDrone::new),
//                new ItemSpawnDrone<>(EntityTreeFarmDrone::new)
//        );
//    }

}