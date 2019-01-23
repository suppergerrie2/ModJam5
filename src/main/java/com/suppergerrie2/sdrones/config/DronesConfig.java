package com.suppergerrie2.sdrones.config;

import com.suppergerrie2.sdrones.Reference;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DronesConfig
{
    //Register config
    private static Configuration configuration;

    //Config category
    public static final String CATEGORY_DRONES = "drones";

    //Config elements
    public static boolean isRecipeHaulerDrone;
    public static boolean isRecipeFighterDrone;
    public static boolean isRecipeTreeFarmDrone;
    public static boolean isRecipeCropFarmDrone;
    public static boolean isRecipeArcherDrone;

    //Pre init the config file and config
    public static void preInit()
    {
        File configFile = new File(Loader.instance().getConfigDir(), "Drones.cfg");
        configuration = new Configuration(configFile);
        syncFromFiles();
    }

    public static Configuration getConfiguration()
    {
        return configuration;
    }

    public static void clientPreInit()
    {
        MinecraftForge.EVENT_BUS.register(new ConfigEventHandler());
    }

    public static void syncFromFiles()
    {
        syncConfig(true, true);
    }


    public static void syncFromGui()
    {
        syncConfig(false, true);
    }

    public static void syncFromFields()
    {
        syncConfig(false, false);
    }

    public static void syncConfig(boolean loadFromFile, boolean setFromConfig)
    {
        if(loadFromFile) configuration.load();

        //Property config elements
        Property propertyIsRecipeHaulerDrone;
        Property propertyIsRecipeFighterDrone;
        Property propertyIsRecipeTreeFarmDrone;
        Property propertyIsRecipeCropFarmDrone;
        Property propertyIsRecipeArcherDrone;

        //Set config elements
        propertyIsRecipeHaulerDrone = configuration.get(CATEGORY_DRONES, "is_recipe_hauler_drone", Reference.RECIPE_HAULER_DRONE_DEFAULT, I18n.format("gui.config.drones.is_recipe_hauler_drone"));
        propertyIsRecipeHaulerDrone.setLanguageKey("gui.config.drones.is_recipe_hauler_drone");
        propertyIsRecipeFighterDrone = configuration.get(CATEGORY_DRONES, "is_recipe_fighter_drone", Reference.RECIPE_FIGHTER_DRONE_DEFAULT, I18n.format("gui.config.drones.is_recipe_fighter_drone"));
        propertyIsRecipeFighterDrone.setLanguageKey("gui.config.drones.is_recipe_fighter_drone");
        propertyIsRecipeTreeFarmDrone = configuration.get(CATEGORY_DRONES, "is_recipe_tree_farm_drone", Reference.RECIPE_TREE_FARM_DRONE_DEFAULT, I18n.format("gui.config.drones.is_recipe_tree_farm_drone"));
        propertyIsRecipeTreeFarmDrone.setLanguageKey("gui.config.drones.is_recipe_tree_farm_drone");
        propertyIsRecipeCropFarmDrone = configuration.get(CATEGORY_DRONES, "is_recipe_crop_farm_drone", Reference.RECIPE_CROP_FARM_DRONE_DEFAULT, I18n.format("gui.config.drones.is_recipe_crop_farm_drone"));
        propertyIsRecipeCropFarmDrone.setLanguageKey("gui.config.drones.is_recipe_crop_farm_drone");
        propertyIsRecipeArcherDrone = configuration.get(CATEGORY_DRONES, "is_recipe_archer_drone", Reference.RECIPE_ARCHER_DRONE_DEFAULT, I18n.format("gui.config.drones.is_recipe_archer_drone"));
        propertyIsRecipeArcherDrone.setLanguageKey("gui.config.drones.is_recipe_archer_drone");

        //Set category
        List<String> propertyOrderDrones = new ArrayList<String>();
        propertyOrderDrones.add(propertyIsRecipeHaulerDrone.getName());
        propertyOrderDrones.add(propertyIsRecipeFighterDrone.getName());
        propertyOrderDrones.add(propertyIsRecipeTreeFarmDrone.getName());
        propertyOrderDrones.add(propertyIsRecipeCropFarmDrone.getName());
        propertyOrderDrones.add(propertyIsRecipeArcherDrone.getName());
        configuration.setCategoryPropertyOrder(CATEGORY_DRONES, propertyOrderDrones);

        if(setFromConfig)
        {
            isRecipeHaulerDrone = propertyIsRecipeHaulerDrone.getBoolean();
            isRecipeFighterDrone = propertyIsRecipeFighterDrone.getBoolean();
            isRecipeTreeFarmDrone = propertyIsRecipeTreeFarmDrone.getBoolean();
            isRecipeCropFarmDrone = propertyIsRecipeCropFarmDrone.getBoolean();
            isRecipeArcherDrone = propertyIsRecipeArcherDrone.getBoolean();
        }

        propertyIsRecipeHaulerDrone.set(isRecipeHaulerDrone);
        propertyIsRecipeFighterDrone.set(isRecipeFighterDrone);
        propertyIsRecipeTreeFarmDrone.set(isRecipeTreeFarmDrone);
        propertyIsRecipeCropFarmDrone.set(isRecipeCropFarmDrone);
        propertyIsRecipeArcherDrone.set(isRecipeArcherDrone);

        //save when has change
        if(configuration.hasChanged())
        {
            configuration.save();
        }
    }

    public static class ConfigEventHandler
    {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public void onEvent(ConfigChangedEvent.OnConfigChangedEvent event)
        {
            if (event.getModID().equals(Reference.MODID))
            {
                syncFromGui();
            }
        }
    }
}
