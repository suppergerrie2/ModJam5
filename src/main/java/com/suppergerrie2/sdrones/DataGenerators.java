package com.suppergerrie2.sdrones;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.suppergerrie2.sdrones.init.ModEntities;
import com.suppergerrie2.sdrones.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.suppergerrie2.sdrones.Reference.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if (event.includeServer()) {
            gen.addProvider(new Recipes(gen));
            gen.addProvider(new Loots(gen));
        }
        if (event.includeClient()) {
            gen.addProvider(new Language(gen));
            gen.addProvider(new ItemModels(gen, helper));
        }
    }

    private static class Recipes extends RecipeProvider {
        Recipes(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        }
    }

    private static class Loots extends LootTableProvider {
        Loots(DataGenerator gen) {
            super(gen);
        }

        @Override
        @Nonnull
        protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
            return ImmutableList.of(
                    Pair.of(Blocks::new, LootParameterSets.BLOCK)
            );
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, ValidationResults validationresults) {
            map.forEach((name, table) -> LootTableManager.func_215302_a(validationresults, name, table, map::get));
        }

        private class Blocks extends BlockLootTables {
            @Override
            protected void addTables() {
            }

            @Override
            @Nonnull
            protected Iterable<Block> getKnownBlocks() {
                return DroneMod.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
            }
        }
    }

    private static class Language extends LanguageProvider {
        Language(DataGenerator gen) {
            super(gen, MODID, "en_us");
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void addTranslations() {

            add(ModItems.DRONE_STICK.get(), "Drone Stick");
            add(ModItems.SPAWN_HAULER_DRONE.get(), "Spawn Hauler Drone");
            add(ModItems.SPAWN_FIGHTER_DRONE.get(), "Spawn Fighter Drone");
            add(ModItems.SPAWN_TREE_FARM_DRONE.get(), "Spawn Tree Farm Drone");

            add(ModEntities.hauler_drone, "Hauler Drone");
            add(ModEntities.fighter_drone, "Fighter Drone");
            add(ModEntities.tree_farm_drone, "Tree Farm Drone");
        }
    }

    private static class ItemModels extends ItemModelProvider {
        ItemModels(DataGenerator gen, ExistingFileHelper helper) {
            super(gen, MODID, helper);
        }

        @Override
        @SuppressWarnings("ConstantConditions")
        protected void registerModels() {
            registerModel(ModItems.DRONE_STICK.get());
            registerModel(ModItems.SPAWN_HAULER_DRONE.get());
            registerModel(ModItems.SPAWN_FIGHTER_DRONE.get());
            registerModel(ModItems.SPAWN_TREE_FARM_DRONE.get());
        }

        private void registerModel(Item item) {
            String path = Objects.requireNonNull(item.getRegistryName()).getPath();
            getBuilder(path).texture("layer0", new ResourceLocation(item.getRegistryName().getNamespace(), "items/"+item.getRegistryName().getPath())).parent(getExistingFile(new ResourceLocation("item/generated")));
        }

        @Override
        @Nonnull
        public String getName() {
            return "Item Models";
        }
    }
}
