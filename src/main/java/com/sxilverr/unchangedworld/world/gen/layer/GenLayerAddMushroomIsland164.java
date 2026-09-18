package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerAddMushroomIsland164 extends GenLayer {

    public GenLayerAddMushroomIsland164(long seed, GenLayer parent) {
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
                int topLeft = parentInts[x + 0 + (y + 0) * parentWidth];
                int topRight = parentInts[x + 2 + (y + 0) * parentWidth];
                int bottomLeft = parentInts[x + 0 + (y + 2) * parentWidth];
                int bottomRight = parentInts[x + 2 + (y + 2) * parentWidth];
                int center = parentInts[x + 1 + (y + 1) * parentWidth];
                this.initChunkSeed(x + areaX, y + areaY);

                if (center == 0 && topLeft == 0
                    && topRight == 0
                    && bottomLeft == 0
                    && bottomRight == 0
                    && this.nextInt(100) == 0) {
                    out[x + y * areaWidth] = BiomeGenBase.mushroomIsland.biomeID;
                } else {
                    out[x + y * areaWidth] = center;
                }
            }
        }

        return out;
    }
}
