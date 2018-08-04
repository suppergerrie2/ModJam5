package com.suppergerrie2.sdrones.init;

import com.suppergerrie2.sdrones.Reference;
import com.suppergerrie2.sdrones.entities.EntityBasicDrone;
import com.suppergerrie2.sdrones.upgrades.DroneUpgrade;

import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid=Reference.MODID)
public class ModUpgrades {

	@SubscribeEvent
	public static void registerTest(RegistryEvent.Register<DroneUpgrade> event) {
		event.getRegistry().registerAll(
				new DroneUpgrade(15) {

					@Override
					public EnumActionResult applyUpgrade(EntityBasicDrone drone, int level) {

						if(this.canApplyUpgrade(drone)) {
							drone.setCarrySize(2+(level*2));
							return EnumActionResult.SUCCESS;
						}

						return EnumActionResult.FAIL;
					}

				}.setRegistryName("storage"),
				
				new DroneUpgrade(10) {
					
					@Override
					public EnumActionResult applyUpgrade(EntityBasicDrone drone, int level) {
						
						if(this.canApplyUpgrade(drone)) {
							drone.setSpeed(Math.pow(1.08, level));
							return EnumActionResult.SUCCESS;
						}
						
						return EnumActionResult.FAIL;
						
					}
					
				}.setRegistryName("speed"));
	}

}
