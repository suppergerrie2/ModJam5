package com.suppergerrie2.sdrones.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;

public class DronesConfig
{
    //Register config
    private static Configuration configuration;

    //Config category
    public static final String CATEGORY_DRONES = "drones";

    //Config elements
    public static boolean recipeHaulerDrone;
    public static boolean recipeFighterDrone;
    public static boolean recipeTreeFarmDrone;
    public static boolean CropFarmDrone;
    public static boolean ArcherDrone;

    //Pre init the config file and config
    public static void preInit()
    {
        File configFile = new File(Loader.instance().getConfigDir(), "Drones.cfg");
        configuration = new Configuration(configFile);
        //syncFromFiles();
    }

    public static Configuration getConfiguration()
    {
        return configuration;
    }

    public static void clientPreInit()
    {
        //MinecraftForge.EVENT_BUS.register(new ConfigEventHandler);
    }
}
