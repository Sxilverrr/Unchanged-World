package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerSwampRivers164 extends GenLayer {

    public GenLayerSwampRivers164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX - 1, areaY - 1, areaWidth + 2, areaHeight + 2);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);
        int parentWidth = areaWidth + 2;

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(x + areaX, y + areaY);
                int center = parentInts[x + 1 + (y + 1) * parentWidth];

                if ((center != BiomeGenBase.swampland.biomeID || this.nextInt(6) != 0)
                    && (center != BiomeGenBase.jungle.biomeID && center != BiomeGenBase.jungleHills.biomeID
                        || this.nextInt(8) != 0)) {
                    out[x + y * areaWidth] = center;
                } else {
                    out[x + y * areaWidth] = BiomeGenBase.river.biomeID;
                }
            }
        }

        return out;
    }
}
