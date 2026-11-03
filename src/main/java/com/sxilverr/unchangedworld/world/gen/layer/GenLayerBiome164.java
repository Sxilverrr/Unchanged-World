package com.sxilverr.unchangedworld.world.gen.layer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager.BiomeType;

import com.sxilverr.unchangedworld.world.biome.BiomesOPlenty164;
import com.sxilverr.unchangedworld.world.biome.ModdedBiomes164;

public class GenLayerBiome164 extends GenLayer {

    static final int WARM = 0;
    static final int TEMPERATE = 1;
    static final int COOL = 2;
    static final int COLD = 3;

    private final BiomeGenBase[] allowedBiomes = { BiomeGenBase.desert, BiomeGenBase.forest, BiomeGenBase.extremeHills,
        BiomeGenBase.swampland, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.jungle };
    private final GenLayer zones;
    private final GenLayer dryness;
    private final Pool temperatePool;
    private final Pool snowyPool;
    private final Pool[][] zonePools = new Pool[4][];

    public GenLayerBiome164(long seed, GenLayer parent, GenLayer zones, GenLayer dryness, boolean moddedBiomes,
        int vanillaWeight) {
        super(seed);
        this.parent = parent;
        this.zones = zones;
        this.dryness = dryness;
        BiomeGenBase[] snowy = new BiomeGenBase[this.allowedBiomes.length];

        for (int i = 0; i < snowy.length; ++i) {
            snowy[i] = this.allowedBiomes[i] == BiomeGenBase.taiga ? BiomeGenBase.taiga : BiomeGenBase.icePlains;
        }

        Map<BiomeGenBase, Integer> none = Collections.emptyMap();
        this.temperatePool = new Pool(
            this.allowedBiomes,
            moddedBiomes ? ModdedBiomes164.temperateWeights() : none,
            vanillaWeight);
        this.snowyPool = new Pool(snowy, moddedBiomes ? ModdedBiomes164.snowyWeights() : none, vanillaWeight);

        if (zones != null) {
            this.zonePools[WARM] = zone(
                moddedBiomes ? ModdedBiomes164.weights(BiomeType.DESERT, BiomesOPlenty164.HOT) : none,
                vanillaWeight,
                new BiomeGenBase[] { BiomeGenBase.jungle, BiomeGenBase.jungle, BiomeGenBase.swampland },
                new BiomeGenBase[] { BiomeGenBase.desert, BiomeGenBase.plains });
            this.zonePools[TEMPERATE] = zone(
                moddedBiomes ? ModdedBiomes164.weights(BiomeType.WARM, BiomesOPlenty164.WARM) : none,
                vanillaWeight,
                new BiomeGenBase[] { BiomeGenBase.forest, BiomeGenBase.forest, BiomeGenBase.swampland },
                new BiomeGenBase[] { BiomeGenBase.plains, BiomeGenBase.extremeHills });
            this.zonePools[COOL] = zone(
                moddedBiomes ? ModdedBiomes164.weights(BiomeType.COOL, BiomesOPlenty164.COOL) : none,
                vanillaWeight,
                new BiomeGenBase[] { BiomeGenBase.taiga, BiomeGenBase.forest },
                new BiomeGenBase[] { BiomeGenBase.taiga, BiomeGenBase.extremeHills });
            this.zonePools[COLD] = zone(
                moddedBiomes ? ModdedBiomes164.weights(BiomeType.ICY, BiomesOPlenty164.ICY) : none,
                vanillaWeight,
                new BiomeGenBase[] { BiomeGenBase.icePlains, BiomeGenBase.taiga },
                new BiomeGenBase[] { BiomeGenBase.icePlains });
        }
    }

    private static Pool[] zone(Map<BiomeGenBase, Integer> modded, int vanillaWeight, BiomeGenBase[] wet,
        BiomeGenBase[] dry) {
        Map<BiomeGenBase, Integer> wetModded = new LinkedHashMap<BiomeGenBase, Integer>(modded);
        Map<BiomeGenBase, Integer> dryModded = new LinkedHashMap<BiomeGenBase, Integer>(modded);
        wetModded.keySet()
            .removeIf(biome -> biome.rainfall < 0.5F);
        dryModded.keySet()
            .removeIf(biome -> biome.rainfall >= 0.5F);
        return new Pool[] { new Pool(wet, wetModded, vanillaWeight), new Pool(dry, dryModded, vanillaWeight) };
    }

    @Override
    public void initWorldGenSeed(long seed) {
        if (this.zones != null) {
            this.zones.initWorldGenSeed(seed);
            this.dryness.initWorldGenSeed(seed);
        }

        super.initWorldGenSeed(seed);
    }

    @Override
    public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
        int[] parentInts = this.parent.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] zoneInts = this.zones == null ? null : this.zones.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] dryInts = this.zones == null ? null : this.dryness.getInts(areaX, areaY, areaWidth, areaHeight);
        int[] out = IntCache.getIntCache(areaWidth * areaHeight);

        for (int y = 0; y < areaHeight; ++y) {
            for (int x = 0; x < areaWidth; ++x) {
                int i = x + y * areaWidth;
                this.initChunkSeed(x + areaX, y + areaY);
                int value = parentInts[i];

                if (value == 0 || value == BiomeGenBase.mushroomIsland.biomeID) {
                    out[i] = value;
                } else {
                    Pool pool = zoneInts != null ? this.zonePools[zoneInts[i]][dryInts[i]]
                        : value == 1 ? this.temperatePool : this.snowyPool;
                    out[i] = pool.pick(this.nextInt(pool.totalWeight));
                }
            }
        }

        return out;
    }

    private static final class Pool {

        private final int[] ids;
        private final int[] weights;
        private final int totalWeight;

        private Pool(BiomeGenBase[] vanilla, Map<BiomeGenBase, Integer> modded, int vanillaWeight) {
            int size = vanilla.length + modded.size();
            this.ids = new int[size];
            this.weights = new int[size];
            int index = 0;

            for (BiomeGenBase biome : vanilla) {
                this.ids[index] = biome.biomeID;
                this.weights[index++] = vanillaWeight;
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
