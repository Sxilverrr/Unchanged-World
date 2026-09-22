package com.sxilverr.unchangedworld.world.gen.layer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

import com.sxilverr.unchangedworld.world.biome.BiomesOPlenty164;
import com.sxilverr.unchangedworld.world.biome.ModdedBiomes164;

public class GenLayerSubBiome164 extends GenLayer {

    private static final int OFFSET_RANGE = 500000;

    private final int[][] subBiomes = new int[256][];
    private final double[] zoom = new double[256];
    private final double[] threshold = new double[256];
    private final int[] offsetX = new int[256];
    private final int[] offsetZ = new int[256];
    private final boolean active;

    public GenLayerSubBiome164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        boolean any = false;

        for (BiomeGenBase biome : ModdedBiomes164.generating()) {
            List<Integer> ids = new ArrayList<Integer>();

            for (BiomeEntry entry : BiomesOPlenty164.subBiomes(biome.biomeID)) {
                if (entry == null || !ModdedBiomes164.isModded(entry.biome)
                    || !BiomesOPlenty164.isSubBiome(entry.biome)) {
                    continue;
                }

                int id = entry.biome.biomeID;
                ids.add(id);
                this.zoom[id] = BiomesOPlenty164.zoom(entry.biome);
                this.threshold[id] = BiomesOPlenty164.threshold(entry.biome);
            }

            if (!ids.isEmpty()) {
                int[] array = new int[ids.size()];

                for (int i = 0; i < array.length; ++i) {
                    array[i] = ids.get(i);
                }

                this.subBiomes[biome.biomeID] = array;
                any = true;
            }
        }

        this.active = any;
    }

    @Override
    public void initWorldGenSeed(long seed) {
        super.initWorldGenSeed(seed);

        if (!this.active) {
            return;
        }

        this.initChunkSeed(0L, 0L);

        for (int i = 0; i < this.offsetX.length; ++i) {
            this.offsetX[i] = this.nextInt(OFFSET_RANGE) - OFFSET_RANGE / 2;
            this.offsetZ[i] = this.nextInt(OFFSET_RANGE) - OFFSET_RANGE / 2;
        }
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);

        if (!this.active) {
            return parentInts;
        }

        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int center = parentInts[x + y * areaWidth];
                int[] candidates = center >= 0 && center < this.subBiomes.length ? this.subBiomes[center] : null;

                if (candidates == null) {
                    out[x + y * areaWidth] = center;
                    continue;
                }

                this.initChunkSeed(x + areaX, y + areaY);
                int sub = candidates[this.nextInt(candidates.length)];
                double value = BiomesOPlenty164.noise(
                    (x + areaX + this.offsetX[sub]) * this.zoom[sub],
                    (y + areaY + this.offsetZ[sub]) * this.zoom[sub]);
                out[x + y * areaWidth] = value > this.threshold[sub] ? sub : center;
            }
        }

        return out;
    }
}
