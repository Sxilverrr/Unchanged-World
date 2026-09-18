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

    private static final String VANILLA_PACKAGE = "net.minecraft.";

    private ModdedBiomes164() {}

    public static boolean isModded(BiomeGenBase biome) {
        return biome != null && Biome164.find(biome) == null
            && !biome.getClass()
                .getName()
                .startsWith(VANILLA_PACKAGE);
    }

    public static Map<BiomeGenBase, Integer> weights(BiomeType... types) {
        Map<BiomeGenBase, Integer> weights = new LinkedHashMap<BiomeGenBase, Integer>();

        for (BiomeType type : types) {
            List<BiomeEntry> entries = BiomeManager.getBiomes(type);

            if (entries == null) {
                continue;
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

        return weights;
    }

    public static List<BiomeGenBase> generating() {
        return new ArrayList<BiomeGenBase>(
            weights(BiomeType.DESERT, BiomeType.WARM, BiomeType.COOL, BiomeType.ICY).keySet());
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
}
