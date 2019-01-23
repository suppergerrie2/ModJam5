package com.suppergerrie2.sdrones.config;

import com.suppergerrie2.sdrones.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.GuiConfigEntries;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DronesConfigGuiFactory implements IModGuiFactory
{
    @Override
    public void initialize(Minecraft minecraftInstance) { }

    @Override
    public boolean hasConfigGui()
    {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen)
    {
        return new DronesConfigGui(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
    {
        return null;
    }

    public static class DronesConfigGui extends GuiConfig
    {
        public DronesConfigGui(GuiScreen parentScreen)
        {
            super(parentScreen, getConfigElements(), Reference.MODID, false, false, I18n.format("gui.config.main_title"));
        }

        private static List<IConfigElement> getConfigElements()
        {
            List<IConfigElement> list = new ArrayList<IConfigElement>();
            list.add(new DummyCategoryElement(I18n.format("gui.config.category.drones"), "gui.config.category.drones", CategoryEntryDrones.class));
            return list;
        }

        public static class CategoryEntryDrones extends GuiConfigEntries.CategoryEntry
        {
            public CategoryEntryDrones(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement)
            {
                super(owningScreen, owningEntryList, configElement);
            }

            @Override
            protected GuiScreen buildChildScreen()
            {
                Configuration configuration = DronesConfig.getConfiguration();
                ConfigElement categorySpawns = new ConfigElement(configuration.getCategory(DronesConfig.CATEGORY_DRONES));
                List<IConfigElement> propertiesOnScreen = categorySpawns.getChildElements();
                String windowTitle = I18n.format("gui.config.category.drones");
                return new GuiConfig(owningScreen, propertiesOnScreen, owningScreen.modID, this.configElement.requiresWorldRestart() || this.owningScreen.allRequireWorldRestart, this.configElement.requiresMcRestart() || this.owningScreen.allRequireMcRestart, windowTitle);
            }
        }
    }
}
