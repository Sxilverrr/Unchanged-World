package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerBiome164 extends GenLayer {

    private final BiomeGenBase[] allowedBiomes = { BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle };

    public GenLayerBiome164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                int value = parentInts[x + y * areaWidth];

                if (value == 0) {
                    out[x + y * areaWidth] = 0;
                } else if (value == BiomeGenBase.mushroomIsland.biomeID) {
                    out[x + y * areaWidth] = value;
                } else if (value == 1) {
                    out[x + y * areaWidth] = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
                } else {
                    int biome = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;

                    if (biome == BiomeGenBase.taiga.biomeID) {
                        out[x + y * areaWidth] = biome;
                    } else {
                        out[x + y * areaWidth] = BiomeGenBase.icePlains.biomeID;
                    }
                }
            }
        }

        return out;
    }
}
