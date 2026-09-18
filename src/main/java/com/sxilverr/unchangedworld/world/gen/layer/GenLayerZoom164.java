package com.sxilverr.unchangedworld.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerZoom164 extends GenLayer {

    public GenLayerZoom164(long seed, GenLayer parent) {
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
                zoomed[index++ + zoomedWidth] = this.modeOrRandom(topLeft, topRight, bottomLeft, bottomRight);
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

    protected int modeOrRandom(int a, int b, int c, int d) {
        if (b == c && c == d) {
            return b;
        } else if (a == b && a == c) {
            return a;
        } else if (a == b && a == d) {
            return a;
        } else if (a == c && a == d) {
            return a;
        } else if (a == b && c != d) {
            return a;
        } else if (a == c && b != d) {
            return a;
        } else if (a == d && b != c) {
            return a;
        } else if (b == a && c != d) {
            return b;
        } else if (b == c && a != d) {
            return b;
        } else if (b == d && a != c) {
            return b;
        } else if (c == a && b != d) {
            return c;
        } else if (c == b && a != d) {
            return c;
        } else if (c == d && a != b) {
            return c;
        } else if (d == a && b != c) {
            return c;
        } else if (d == b && a != c) {
            return c;
        } else if (d == c && a != b) {
            return c;
        } else {
            int r = this.nextInt(4);
            return r == 0 ? a : (r == 1 ? b : (r == 2 ? c : d));
        }
    }

    public static GenLayer magnify(long seed, GenLayer layer, int times) {
        GenLayer result = layer;

        for (int i = 0; i < times; ++i) {
            result = new GenLayerZoom164(seed + i, result);
        }

        return result;
    }
}
