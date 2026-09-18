package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRiverMix164 extends GenLayer {

    private final GenLayer biomePatternGeneratorChain;
    private final GenLayer riverPatternGeneratorChain;

    public GenLayerRiverMix164(long seed, GenLayer biomeChain, GenLayer riverChain) {
        super(seed);
        this.biomePatternGeneratorChain = biomeChain;
        this.riverPatternGeneratorChain = riverChain;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        this.biomePatternGeneratorChain.initWorldGenSeed(seed);
        this.riverPatternGeneratorChain.initWorldGenSeed(seed);
        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] biomes = this.biomePatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] rivers = this.riverPatternGeneratorChain.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int i = 0; i < areaWidth * areaHeight; ++i) {
            if (biomes[i] == BiomeGenBase.ocean.biomeID) {
                out[i] = biomes[i];
            } else if (rivers[i] >= 0) {
                if (biomes[i] == BiomeGenBase.icePlains.biomeID) {
                    out[i] = BiomeGenBase.frozenRiver.biomeID;
                } else if (biomes[i] != BiomeGenBase.mushroomIsland.biomeID
                    && biomes[i] != BiomeGenBase.mushroomIslandShore.biomeID) {
                        out[i] = rivers[i];
                    } else {
                        out[i] = BiomeGenBase.mushroomIslandShore.biomeID;
                    }
            } else {
                out[i] = biomes[i];
            }
        }

        return out;
    }
}
