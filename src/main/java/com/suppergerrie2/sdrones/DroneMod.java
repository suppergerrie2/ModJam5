package com.suppergerrie2.sdrones;

import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelFighterDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelHaulerDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelTreeFarmDrone;
import com.suppergerrie2.sdrones.init.ModItems;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class DroneMod {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, Reference.MODID);
    public static final DeferredRegister<TileEntityType<?>> TILES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, Reference.MODID);

    private static final Logger LOGGER = LogManager.getLogger();

    public DroneMod() {
        LOGGER.trace("Initializing {}", Reference.MODID);
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenders);
        DronesPacketHandler.registerChannel();
    }

    private void registerRenders(final FMLClientSetupEvent event) {
        LOGGER.info("Registering rendering handlers for drones");

        RenderingRegistry.registerEntityRenderingHandler(EntityHaulerDrone.class, manager -> new RenderDrone(manager, new ModelHaulerDrone(), "hauler_drone"));
        RenderingRegistry.registerEntityRenderingHandler(EntityFighterDrone.class, manager -> new RenderDrone(manager, new ModelFighterDrone(), "fighter_drone"));
        RenderingRegistry.registerEntityRenderingHandler(EntityTreeFarmDrone.class, manager -> new RenderDrone(manager, new ModelTreeFarmDrone(), "tree_farm_drone"));
    }
}