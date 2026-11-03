package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.sxilverr.unchangedworld.world.Settings164;

public class GenLayer164 extends GenLayer {

    private final Rule rule;

    private GenLayer164(long seed, GenLayer parent, Rule rule) {
        super(seed);
        this.parent = parent;
        this.rule = rule;
    }

    public static GenLayer[] initializeAllBiomeGenerators(long seed, Settings164 settings) {
        GenLayer layer = new GenLayerIsland164(1L);
        layer = new GenLayerZoom164.Fuzzy(2000L, layer);
        layer = new GenLayer164(1L, layer, GenLayer164::addIsland);
        layer = new GenLayerZoom164(2001L, layer);
        layer = new GenLayer164(2L, layer, GenLayer164::addIsland);
        layer = new GenLayer164(2L, layer, GenLayer164::addSnow);
        layer = new GenLayerZoom164(2002L, layer);
        layer = new GenLayer164(3L, layer, GenLayer164::addIsland);
        layer = new GenLayerZoom164(2003L, layer);
        layer = new GenLayer164(4L, layer, GenLayer164::addIsland);
        GenLayer continents = new GenLayer164(5L, layer, GenLayer164::addMushroomIsland);
        int biomeSize = settings.biomeSize;

        GenLayer rivers = new GenLayerRiverInit164(100L, continents);
        rivers = GenLayerZoom164.magnify(1000L, rivers, biomeSize + 2);
        rivers = new GenLayer164(1L, rivers, GenLayer164::river);
        rivers = new GenLayer164(1000L, rivers, GenLayer164::smooth);

        GenLayer zones = null;
        GenLayer dryness = null;

        if (settings.climateZones) {
            zones = GenLayerZoom164.magnify(2100L, new GenLayerRoll164(3L, 3, 8), 2);
            zones = new GenLayer164(2L, zones, GenLayer164::climateZone);
            dryness = GenLayerZoom164.magnify(2200L, new GenLayerRoll164(7L, 1, 2), 2);
        }

        GenLayer biomes = new GenLayerBiome164(
            200L,
            continents,
            zones,
            dryness,
            settings.moddedBiomes,
            settings.vanillaBiomeWeight);
        biomes = GenLayerZoom164.magnify(1000L, biomes, 2);
        biomes = new GenLayer164(1000L, biomes, GenLayer164::hills);
        biomes = new GenLayerSubBiome164(1500L, biomes, settings.moddedBiomes);

        for (int i = 0; i < biomeSize; ++i) {
            biomes = new GenLayerZoom164(1000L + i, biomes);
            if (i == 0) {
                biomes = new GenLayer164(3L, biomes, GenLayer164::addIsland);
            }
            if (i == 1) {
                biomes = new GenLayer164(1000L, biomes, GenLayer164::shore);
                biomes = new GenLayer164(1000L, biomes, GenLayer164::swampRivers);
            }
        }

        biomes = new GenLayer164(1000L, biomes, GenLayer164::smooth);
        GenLayerRiverMix164 riverMix = new GenLayerRiverMix164(100L, biomes, rivers, settings.moddedBiomes);
        GenLayerVoronoiZoom164 voronoi = new GenLayerVoronoiZoom164(10L, riverMix);
        riverMix.initWorldGenSeed(seed);
        voronoi.initWorldGenSeed(seed);
        return new GenLayer[] { riverMix, voronoi, riverMix };
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int width = areaWidth + 2;
        int[] parentInts = this.parent.getInts(areaX - 1, areaY - 1, width, areaHeight + 2);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                out[x + y * areaWidth] = this.rule.pick(this, parentInts, x + 1 + (y + 1) * width, width);
            }
        }

        return out;
    }

    private interface Rule {

        int pick(GenLayer164 layer, int[] parent, int center, int width);
    }

    private static int addIsland(GenLayer164 layer, int[] p, int i, int w) {
        int topLeft = p[i - w - 1];
        int topRight = p[i - w + 1];
        int bottomLeft = p[i + w - 1];
        int bottomRight = p[i + w + 1];
        int center = p[i];

        if (center == 0 && (topLeft != 0 || topRight != 0 || bottomLeft != 0 || bottomRight != 0)) {
            int chance = 1;
            int picked = 1;

            if (topLeft != 0 && layer.nextInt(chance++) == 0) {
                picked = topLeft;
            }

            if (topRight != 0 && layer.nextInt(chance++) == 0) {
                picked = topRight;
            }

            if (bottomLeft != 0 && layer.nextInt(chance++) == 0) {
                picked = bottomLeft;
            }

            if (bottomRight != 0 && layer.nextInt(chance++) == 0) {
                picked = bottomRight;
            }

            return layer.nextInt(3) == 0 ? picked : frozenOrZero(picked);
        }

        if (center > 0 && (topLeft == 0 || topRight == 0 || bottomLeft == 0 || bottomRight == 0)) {
            return layer.nextInt(5) == 0 ? frozenOrZero(center) : center;
        }

        return center;
    }

    private static int frozenOrZero(int biome) {
        return biome == BiomeGenBase.icePlains.biomeID ? BiomeGenBase.frozenOcean.biomeID : 0;
    }

    private static int addMushroomIsland(GenLayer164 layer, int[] p, int i, int w) {
        return p[i] == 0 && p[i - w - 1] == 0
            && p[i - w + 1] == 0
            && p[i + w - 1] == 0
            && p[i + w + 1] == 0
            && layer.nextInt(100) == 0 ? BiomeGenBase.mushroomIsland.biomeID : p[i];
    }

    private static int addSnow(GenLayer164 layer, int[] p, int i, int w) {
        if (p[i] == 0) {
            return 0;
        }

        return layer.nextInt(5) == 0 ? BiomeGenBase.icePlains.biomeID : 1;
    }

    private static int climateZone(GenLayer164 layer, int[] p, int i, int w) {
        if (p[i] != 0) {
            return p[i - w] == 0 || p[i + 1] == 0 || p[i - 1] == 0 || p[i + w] == 0 ? GenLayerBiome164.COOL
                : GenLayerBiome164.COLD;
        }

        for (int row = i - w; row <= i + w; row += w) {
            if (p[row - 1] != 0 || p[row] != 0 || p[row + 1] != 0) {
                return GenLayerBiome164.TEMPERATE;
            }
        }

        return GenLayerBiome164.WARM;
    }

    private static int hills(GenLayer164 layer, int[] p, int i, int w) {
        int center = p[i];
        boolean roll = layer.nextInt(3) == 0;
        int hills = center;

        if (center == BiomeGenBase.desert.biomeID) {
            hills = BiomeGenBase.desertHills.biomeID;
        } else if (center == BiomeGenBase.forest.biomeID) {
            hills = BiomeGenBase.forestHills.biomeID;
        } else if (center == BiomeGenBase.taiga.biomeID) {
            hills = BiomeGenBase.taigaHills.biomeID;
        } else if (center == BiomeGenBase.plains.biomeID) {
            hills = BiomeGenBase.forest.biomeID;
        } else if (center == BiomeGenBase.icePlains.biomeID) {
            hills = BiomeGenBase.iceMountains.biomeID;
        } else if (center == BiomeGenBase.jungle.biomeID) {
            hills = BiomeGenBase.jungleHills.biomeID;
        }

        return roll && p[i - w] == center && p[i + 1] == center && p[i - 1] == center && p[i + w] == center ? hills
            : center;
    }

    private static int river(GenLayer164 layer, int[] p, int i, int w) {
        int center = p[i];
        return center != 0 && p[i - 1] == center && p[i + 1] == center && p[i - w] == center && p[i + w] == center ? -1
            : BiomeGenBase.river.biomeID;
    }

    private static int smooth(GenLayer164 layer, int[] p, int i, int w) {
        int west = p[i - 1];
        int east = p[i + 1];
        int north = p[i - w];
        int south = p[i + w];

        if (west == east && north == south) {
            return layer.nextInt(2) == 0 ? west : north;
        }

        return west == east ? west : (north == south ? north : p[i]);
    }

    private static int shore(GenLayer164 layer, int[] p, int i, int w) {
        int center = p[i];
        int ocean = BiomeGenBase.ocean.biomeID;
        int hills = BiomeGenBase.extremeHills.biomeID;
        boolean nearOcean = p[i - w] == ocean || p[i + 1] == ocean || p[i - 1] == ocean || p[i + w] == ocean;

        if (center == BiomeGenBase.mushroomIsland.biomeID) {
            return nearOcean ? BiomeGenBase.mushroomIslandShore.biomeID : center;
        }

        if (center != ocean && center != BiomeGenBase.river.biomeID
            && center != BiomeGenBase.swampland.biomeID
            && center != hills) {
            return nearOcean ? BiomeGenBase.beach.biomeID : center;
        }

        if (center == hills && (p[i - w] != hills || p[i + 1] != hills || p[i - 1] != hills || p[i + w] != hills)) {
            return BiomeGenBase.extremeHillsEdge.biomeID;
        }

        return center;
    }

    private static int swampRivers(GenLayer164 layer, int[] p, int i, int w) {
        int center = p[i];
        return (center != BiomeGenBase.swampland.biomeID || layer.nextInt(6) != 0)
            && (center != BiomeGenBase.jungle.biomeID && center != BiomeGenBase.jungleHills.biomeID
                || layer.nextInt(8) != 0) ? center : BiomeGenBase.river.biomeID;
    }
}
