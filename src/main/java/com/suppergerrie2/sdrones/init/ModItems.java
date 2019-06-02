package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.items.ItemSpawnDrone;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void registerItemBlocks(final RegistryEvent.Register<Item> event) {
        LOGGER.info("Registering items...");
        event.getRegistry().registerAll(
            new Item(new Item.Properties()).setRegistryName(new ResourceLocation(Reference.MODID, "drone_stick")),
            new ItemSpawnDrone<>("spawn_hauler", EntityHaulerDrone::new)
        );
    }

}