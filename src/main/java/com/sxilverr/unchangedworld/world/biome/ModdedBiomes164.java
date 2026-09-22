package com.sxilverr.unchangedworld.world.biome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import net.minecraftforge.common.BiomeManager.BiomeType;

public final class ModdedBiomes164 {

    private static final int VANILLA_IDS = 40;
    private static final int MUTATION_OFFSET = 128;
    private static final int[] MUTATED_VANILLA = { 1, 2, 3, 4, 5, 6, 12, 21, 23, 27, 28, 29, 30, 32, 34, 35, 36, 37, 38,
        39 };
    private static final boolean[] VANILLA = new boolean[256];

    static {
        for (int id = 0; id < VANILLA_IDS; ++id) {
            VANILLA[id] = true;
        }

        for (int id : MUTATED_VANILLA) {
            VANILLA[id + MUTATION_OFFSET] = true;
        }
    }

    private ModdedBiomes164() {}

    public static boolean isVanilla(int biomeId) {
        return biomeId >= 0 && biomeId < VANILLA.length && VANILLA[biomeId];
    }

    public static boolean isModded(BiomeGenBase biome) {
        return biome != null && !isVanilla(biome.biomeID);
    }

    public static Map<BiomeGenBase, Integer> temperateWeights() {
        Map<BiomeGenBase, Integer> weights = new LinkedHashMap<BiomeGenBase, Integer>();
        addForge(weights, BiomeType.DESERT, BiomeType.WARM, BiomeType.COOL);
        addBiomesOPlenty(weights, BiomesOPlenty164.HOT, BiomesOPlenty164.WARM, BiomesOPlenty164.COOL);
        return weights;
    }

    public static Map<BiomeGenBase, Integer> snowyWeights() {
        Map<BiomeGenBase, Integer> weights = new LinkedHashMap<BiomeGenBase, Integer>();
        addForge(weights, BiomeType.ICY);
        addBiomesOPlenty(weights, BiomesOPlenty164.ICY);
        return weights;
    }

    public static List<BiomeGenBase> generating() {
        List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();
        biomes.addAll(temperateWeights().keySet());

        for (BiomeGenBase biome : snowyWeights().keySet()) {
            if (!biomes.contains(biome)) {
                biomes.add(biome);
            }
        }

        for (int i = 0, parents = biomes.size(); i < parents; ++i) {
            for (BiomeEntry entry : BiomesOPlenty164.subBiomes(biomes.get(i).biomeID)) {
                if (entry != null && isModded(entry.biome) && !biomes.contains(entry.biome)) {
                    biomes.add(entry.biome);
                }
            }
        }

        return biomes;
    }

    public static List<BiomeGenBase> among(Collection<BiomeGenBase> biomes) {
        List<BiomeGenBase> modded = new ArrayList<BiomeGenBase>();

        for (BiomeGenBase biome : biomes) {
            if (isModded(biome) && !modded.contains(biome)) {
                modded.add(biome);
            }
        }

        return modded;
    }

    private static void addForge(Map<BiomeGenBase, Integer> weights, BiomeType... types) {
        for (BiomeType type : types) {
            add(weights, BiomeManager.getBiomes(type));
        }
    }

    private static void addBiomesOPlenty(Map<BiomeGenBase, Integer> weights, int... temperatureTypes) {
        for (int type : temperatureTypes) {
            add(weights, BiomesOPlenty164.overworldBiomes(type));
        }
    }

    private static void add(Map<BiomeGenBase, Integer> weights, Collection<BiomeEntry> entries) {
        if (entries == null) {
            return;
        }

        for (BiomeEntry entry : entries) {
            if (entry == null || entry.itemWeight <= 0 || !isModded(entry.biome)) {
                continue;
            }

            Integer known = weights.get(entry.biome);

            if (known == null || known < entry.itemWeight) {
                weights.put(entry.biome, entry.itemWeight);
            }
        }
    }
}
