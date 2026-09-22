package com.sxilverr.unchangedworld.world.gen.layer;

import java.util.Map;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

import com.sxilverr.unchangedworld.world.biome.ModdedBiomes164;

public class GenLayerBiome164 extends GenLayer {

    private static final int VANILLA_WEIGHT = 10;

    private final BiomeGenBase[] allowedBiomes = { BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle };
    private final Pool temperatePool;
    private final Pool snowyPool;

    public GenLayerBiome164(long seed, GenLayer parent) {
        super(seed);
        this.parent = parent;
        int[] temperate = new int[this.allowedBiomes.length];
        int[] snowy = new int[this.allowedBiomes.length];

        for (int i = 0; i < this.allowedBiomes.length; ++i) {
            BiomeGenBase biome = this.allowedBiomes[i];
            temperate[i] = biome.biomeID;
            snowy[i] = biome == BiomeGenBase.taiga ? biome.biomeID : BiomeGenBase.icePlains.biomeID;
        }

        this.temperatePool = new Pool(temperate, ModdedBiomes164.temperateWeights());
        this.snowyPool = new Pool(snowy, ModdedBiomes164.snowyWeights());
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
                    out[x + y * areaWidth] = this.temperatePool.pick(this.nextInt(this.temperatePool.totalWeight));
                } else {
                    out[x + y * areaWidth] = this.snowyPool.pick(this.nextInt(this.snowyPool.totalWeight));
                }
            }
        }

        return out;
    }

    private static final class Pool {

        private final int[] ids;
        private final int[] weights;
        private final int totalWeight;

        private Pool(int[] vanillaIds, Map<BiomeGenBase, Integer> modded) {
            int size = vanillaIds.length + modded.size();
            this.ids = new int[size];
            this.weights = new int[size];
            int index = 0;

            for (int id : vanillaIds) {
                this.ids[index] = id;
                this.weights[index++] = VANILLA_WEIGHT;
            }

            for (Map.Entry<BiomeGenBase, Integer> entry : modded.entrySet()) {
                this.ids[index] = entry.getKey().biomeID;
                this.weights[index++] = entry.getValue();
            }

            int divisor = 0;

            for (int weight : this.weights) {
                divisor = gcd(divisor, weight);
            }

            int total = 0;

            for (int i = 0; i < size; ++i) {
                this.weights[i] /= divisor;
                total += this.weights[i];
            }

            this.totalWeight = total;
        }

        private int pick(int roll) {
            for (int i = 0; i < this.ids.length; ++i) {
                roll -= this.weights[i];

                if (roll < 0) {
                    return this.ids[i];
                }
            }

            return this.ids[this.ids.length - 1];
        }

        private static int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }
    }
}
