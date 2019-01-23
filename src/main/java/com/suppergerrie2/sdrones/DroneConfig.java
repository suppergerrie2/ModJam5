package com.suppergerrie2.sdrones;

import net.minecraftforge.common.config.Config;

@Config(modid=Reference.MODID)
public class DroneConfig {

    @Config.LangKey("sdrones.config.enable_archer")
    public static boolean enableArcherDrone = true;

    @Config.LangKey("sdrones.config.enable_crop_farm")
    public static boolean enableCropFarmDrone = true;

    @Config.LangKey("sdrones.config.enable_fighter")
    public static boolean enableFighterDrone = true;

    @Config.LangKey("sdrones.config.enable_tree_farm")
    public static boolean enableTreeFarmrone = true;

    @Config.LangKey("sdrones.config.enable_hauler")
    public static boolean enableHaulerDrone = true;

}
