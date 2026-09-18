package com.sxilverr.unchangedworld;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sxilverr.unchangedworld.world.WorldTypeDefault164;
import com.sxilverr.unchangedworld.world.WorldTypeTracker164;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(
    modid = UnchangedWorld.MODID,
    version = Tags.VERSION,
    name = UnchangedWorld.NAME,
    acceptedMinecraftVersions = "[1.7.10]")
public class UnchangedWorld {

    public static final String MODID = "unchangedworld";
    public static final String NAME = "Unchanged World";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static WorldTypeDefault164 worldTypeDefault164;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        worldTypeDefault164 = new WorldTypeDefault164();
        MinecraftForge.EVENT_BUS.register(new WorldTypeTracker164());
        LOG.info(
            "Registered world type '{}' with id {}",
            worldTypeDefault164.getWorldTypeName(),
            worldTypeDefault164.getWorldTypeID());
    }
}
