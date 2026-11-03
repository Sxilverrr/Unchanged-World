package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerRoll164 extends GenLayer {

    private final int chance;
    private final int bound;

    public GenLayerRoll164(long seed, int chance, int bound) {
        super(seed);
        this.chance = chance;
        this.bound = bound;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                this.initChunkSeed(areaX + x, areaY + y);
                out[x + y * areaWidth] = this.nextInt(this.bound) < this.chance ? 1 : 0;
            }
        }

        return out;
    }
}
