package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerFuzzyZoom164 extends GenLayer {

    public GenLayerFuzzyZoom164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int parentX = areaX >> 1;
        int parentY = areaY >> 1;
        int parentWidth = (areaWidth >> 1) + 3;
        int parentHeight = (areaHeight >> 1) + 3;
        int[] parentInts = this.parent.getInts(parentX, parentY, parentWidth, parentHeight);
        int[] zoomed = IntCache.getIntCache(parentWidth * 2 * parentHeight * 2);
        int zoomedWidth = parentWidth << 1;

        for (int py = 0; py < parentHeight - 1; ++py) {
            int index = (py << 1) * zoomedWidth;
            int topLeft = parentInts[0 + (py + 0) * parentWidth];
            int bottomLeft = parentInts[0 + (py + 1) * parentWidth];

            for (int px = 0; px < parentWidth - 1; ++px) {
                this.initChunkSeed((px + parentX) << 1, (py + parentY) << 1);
                int topRight = parentInts[px + 1 + (py + 0) * parentWidth];
                int bottomRight = parentInts[px + 1 + (py + 1) * parentWidth];
                zoomed[index] = topLeft;
                zoomed[index++ + zoomedWidth] = this.choose(topLeft, bottomLeft);
                zoomed[index] = this.choose(topLeft, topRight);
                zoomed[index++ + zoomedWidth] = this.choose(topLeft, topRight, bottomLeft, bottomRight);
                topLeft = topRight;
                bottomLeft = bottomRight;
            }
        }

        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            System.arraycopy(zoomed, (y + (areaY & 1)) * zoomedWidth + (areaX & 1), out, y * areaWidth, areaWidth);
        }

        return out;
    }

    protected int choose(int a, int b) {
        return this.nextInt(2) == 0 ? a : b;
    }

    protected int choose(int a, int b, int c, int d) {
        int r = this.nextInt(4);
        return r == 0 ? a : (r == 1 ? b : (r == 2 ? c : d));
    }
}
