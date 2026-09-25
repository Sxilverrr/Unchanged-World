package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;

import com.sxilverr.unchangedworld.world.Settings164;

public final class GenLayer164 {

    private GenLayer164() {}

    public static GenLayer[] initializeAllBiomeGenerators(long seed, Settings164 settings) {
        GenLayer layer = new GenLayerIsland164(1L);
        layer = new GenLayerFuzzyZoom164(2000L, layer);
        layer = new GenLayerAddIsland164(1L, layer);
        layer = new GenLayerZoom164(2001L, layer);
        layer = new GenLayerAddIsland164(2L, layer);
        layer = new GenLayerAddSnow164(2L, layer);
        layer = new GenLayerZoom164(2002L, layer);
        layer = new GenLayerAddIsland164(3L, layer);
        layer = new GenLayerZoom164(2003L, layer);
        layer = new GenLayerAddIsland164(4L, layer);
        GenLayer continents = new GenLayerAddMushroomIsland164(5L, layer);
        int biomeSize = settings.largeBiomes ? 6 : 4;

        GenLayer rivers = GenLayerZoom164.magnify(1000L, continents, 0);
        rivers = new GenLayerRiverInit164(100L, rivers);
        rivers = GenLayerZoom164.magnify(1000L, rivers, biomeSize + 2);
        rivers = new GenLayerRiver164(1L, rivers);
        rivers = new GenLayerSmooth164(1000L, rivers);

        GenLayer biomes = GenLayerZoom164.magnify(1000L, continents, 0);
        biomes = new GenLayerBiome164(200L, biomes, settings.moddedBiomes, settings.vanillaBiomeWeight);
        biomes = GenLayerZoom164.magnify(1000L, biomes, 2);
        biomes = new GenLayerHills164(1000L, biomes);
        biomes = new GenLayerSubBiome164(1500L, biomes, settings.moddedBiomes);

        for (int i = 0; i < biomeSize; ++i) {
            biomes = new GenLayerZoom164(1000L + i, biomes);
            if (i == 0) {
                biomes = new GenLayerAddIsland164(3L, biomes);
            }
            if (i == 1) {
                biomes = new GenLayerShore164(1000L, biomes);
                biomes = new GenLayerSwampRivers164(1000L, biomes);
            }
        }

        biomes = new GenLayerSmooth164(1000L, biomes);
        GenLayerRiverMix164 riverMix = new GenLayerRiverMix164(100L, biomes, rivers, settings.moddedBiomes);
        GenLayerVoronoiZoom164 voronoi = new GenLayerVoronoiZoom164(10L, riverMix);
        riverMix.initWorldGenSeed(seed);
        voronoi.initWorldGenSeed(seed);
        return new GenLayer[] { riverMix, voronoi, riverMix };
    }
}
