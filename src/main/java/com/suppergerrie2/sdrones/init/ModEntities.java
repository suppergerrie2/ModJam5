package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityAbstractDrone;
import com.suppergerrie2.sdrones.entities.EntityFighterDrone;
import com.suppergerrie2.sdrones.entities.EntityHaulerDrone;
import com.suppergerrie2.sdrones.entities.EntityTreeFarmDrone;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = Reference.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
@ObjectHolder(Reference.MODID)
public class ModEntities {

    public static final EntityType<EntityAbstractDrone> hauler_drone = null;
    public static final EntityType<EntityAbstractDrone> fighter_drone = null;
    public static final EntityType<EntityAbstractDrone> tree_farm_drone = null;

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
        //TODO: drone entity classification maybe?
        EntityType<?> haulerDrone = Builder.<EntityAbstractDrone>create(EntityHaulerDrone::new, EntityClassification.MISC)
            .size(0.3f, 0.3f)
            .setCustomClientFactory((packet, world) -> new EntityHaulerDrone(world))
            .build("hauler_drone")
            .setRegistryName(Reference.MODID, "hauler_drone");

        EntityType<?> fighterDrone = EntityType.Builder.<EntityAbstractDrone>create(EntityFighterDrone::new, EntityClassification.MISC)
            .size(0.3f, 0.3f)
            .setCustomClientFactory((packet, world) -> new EntityFighterDrone(world))
            .build("fighter_drone")
            .setRegistryName(Reference.MODID, "fighter_drone");

        EntityType<?> treeFarmDrone = EntityType.Builder.<EntityAbstractDrone>create(EntityTreeFarmDrone::new, EntityClassification.MISC)
            .size(0.3f, 0.3f)
            .setCustomClientFactory((packet, world) -> new EntityTreeFarmDrone(world))
            .build("tree_farm_drone")
            .setRegistryName(Reference.MODID, "tree_farm_drone");

        event.getRegistry().registerAll(
            haulerDrone,
            fighterDrone,
            treeFarmDrone
        );
    }
}
