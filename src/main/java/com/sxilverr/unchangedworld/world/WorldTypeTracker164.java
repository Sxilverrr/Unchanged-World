package com.sxilverr.unchangedworld.world;

import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldTypeTracker164 {

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.provider.dimensionId == 0) {
            WorldTypeDefault164.setCurrent(
                WorldTypeDefault164.isClimate164(
                    event.world.getWorldInfo()
                        .getTerrainType()));
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world.provider.dimensionId == 0) {
            WorldTypeDefault164.setCurrent(false);
        }
    }
}
