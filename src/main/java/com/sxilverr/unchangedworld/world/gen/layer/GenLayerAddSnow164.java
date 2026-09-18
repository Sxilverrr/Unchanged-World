package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerAddSnow164 extends GenLayer {

    public GenLayerAddSnow164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentX = areaX - 1;
        int parentY = areaY - 1;
        int parentWidth = areaWidth + 2;
        int parentHeight = areaHeight + 2;
        int[] parentInts = this.parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int center = parentInts[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed(x + areaX, y + areaY);

                if (center == 0) {
                    out[x + y * areaWidth] = 0;
                } else {
                    int value = this.nextInt(5);

                    if (value == 0) {
                        value = BiomeGenBase.icePlains.biomeID;
                    } else {
                        value = 1;
                    }

                    out[x + y * areaWidth] = value;
                }
            }
        }

        return out;
    }
}
