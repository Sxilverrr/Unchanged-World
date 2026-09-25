package com.sxilverr.unchangedworld.client;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import com.sxilverr.unchangedworld.UnchangedWorld;
import com.sxilverr.unchangedworld.world.IntegratedConfig164;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;

public class ConfigGui164 extends GuiConfig {

    public ConfigGui164(GuiScreen parent) {
        super(
            parent,
            elements(),
            UnchangedWorld.MODID,
            true,
            false,
            GuiConfig.getAbridgedConfigPath(
                IntegratedConfig164.config()
                    .toString()));
    }

    @SuppressWarnings("rawtypes")
    private static List<IConfigElement> elements() {
        return new ConfigElement(
            IntegratedConfig164.config()
                .getCategory(IntegratedConfig164.CATEGORY)).getChildElements();
    }
}
