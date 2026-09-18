package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRiver164 extends GenLayer {

    public GenLayerRiver164(long seed, GenLayer parent) {
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
                int west = parentInts[x + 0 + (y + 1) * parentWidth];
                int east = parentInts[x + 2 + (y + 1) * parentWidth];
                int north = parentInts[x + 1 + (y + 0) * parentWidth];
                int south = parentInts[x + 1 + (y + 2) * parentWidth];
                int center = parentInts[x + 1 + (y + 1) * parentWidth];

                if (center != 0 && west != 0
                    && east != 0
                    && north != 0
                    && south != 0
                    && center == west
                    && center == north
                    && center == east
                    && center == south) {
                    out[x + y * areaWidth] = -1;
                } else {
                    out[x + y * areaWidth] = BiomeGenBase.river.biomeID;
                }
            }
        }

        return out;
    }
}
