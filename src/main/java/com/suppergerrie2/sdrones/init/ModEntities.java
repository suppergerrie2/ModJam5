package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MODID)
public class ModEntities {

    public static final EntityType<EntityHaulerDrone> hauler_drone = null;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        EntityType<?> haulerDrone = EntityType.Builder.create(EntityHaulerDrone.class, EntityHaulerDrone::new)
            .tracker(64, 1, false)
            .build("hauler_drone")
            .setRegistryName(Reference.MODID, "hauler_drone");

        event.getRegistry().registerAll(
            haulerDrone
        );
    }
}
