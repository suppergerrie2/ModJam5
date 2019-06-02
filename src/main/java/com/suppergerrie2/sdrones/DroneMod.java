package com.suppergerrie2.sdrones;

import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.rendering.RenderDrone;
import com.suppergerrie2.sdrones.entities.rendering.models.ModelHaulerDrone;
import com.suppergerrie2.sdrones.networking.DronesPacketHandler;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MODID)
public class DroneMod {

    private static final Logger LOGGER = LogManager.getLogger();

    public DroneMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerRenders);
        DronesPacketHandler.registerChannel();
    }

    private void registerRenders(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityHaulerDrone.class, new IRenderFactory<EntityHaulerDrone>() {

            @Override
            public Render<? super EntityAbstractDrone> createRenderFor(RenderManager manager) {
                return new RenderDrone(manager, new ModelHaulerDrone(), "hauler_drone");
            }
        });
    }
}