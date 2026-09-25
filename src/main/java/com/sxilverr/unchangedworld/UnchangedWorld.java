package com.sxilverr.unchangedworld;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sxilverr.unchangedworld.world.IntegratedConfig164;
import com.sxilverr.unchangedworld.world.WorldTypeDefault164;
import com.sxilverr.unchangedworld.world.WorldTypeIntegrated164;
import com.sxilverr.unchangedworld.world.WorldTypeTracker164;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(
    modid = UnchangedWorld.MODID,
    version = Tags.VERSION,
    name = UnchangedWorld.NAME,
    acceptedMinecraftVersions = "[1.7.10]",
    guiFactory = "com.sxilverr.unchangedworld.client.ConfigGuiFactory164")
public class UnchangedWorld {

    public static final String MODID = "unchangedworld";
    public static final String NAME = "Unchanged World";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static WorldTypeDefault164 worldTypeDefault164;
    public static WorldTypeIntegrated164 worldTypeIntegrated164;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        IntegratedConfig164.load(event.getSuggestedConfigurationFile());
        worldTypeDefault164 = new WorldTypeDefault164();
        worldTypeIntegrated164 = new WorldTypeIntegrated164();
        MinecraftForge.EVENT_BUS.register(new WorldTypeTracker164());
        FMLCommonHandler.instance()
            .bus()
            .register(this);
        LOG.info(
            "Registered world types '{}' with id {} and '{}' with id {}",
            worldTypeDefault164.getWorldTypeName(),
            worldTypeDefault164.getWorldTypeID(),
            worldTypeIntegrated164.getWorldTypeName(),
            worldTypeIntegrated164.getWorldTypeID());
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (MODID.equals(event.modID)) {
            IntegratedConfig164.reload();
        }
    }
}
