package com.sxilverr.unchangedworld.world.biome;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

import com.sxilverr.unchangedworld.UnchangedWorld;

import cpw.mods.fml.common.Loader;

public final class BiomesOPlenty164 {

    public static final String MOD_ID = "BiomesOPlenty";
    public static final int HOT = 0;
    public static final int WARM = 1;
    public static final int COOL = 2;
    public static final int ICY = 3;

    private static final String MANAGER = "biomesoplenty.common.world.BOPBiomeManager";
    private static final String SUB_BIOME = "biomesoplenty.common.biome.BOPSubBiome";
    private static final String NOISE = "biomesoplenty.common.world.noise.SimplexNoise";

    private static boolean resolved;
    private static boolean available;
    private static Field overworldBiomes;
    private static Field overworldSubBiomes;
    private static Field overworldRiverBiomes;
    private static Class<?> subBiomeClass;
    private static Field zoom;
    private static Field threshold;
    private static MethodHandle noise;

    private BiomesOPlenty164() {}

    public static boolean isLoaded() {
        resolve();
        return available;
    }

    private static synchronized void resolve() {
        if (resolved) {
            return;
        }

        resolved = true;

        if (!Loader.isModLoaded(MOD_ID)) {
            return;
        }

        try {
            Class<?> manager = Class.forName(MANAGER);
            overworldBiomes = manager.getField("overworldBiomes");
            overworldSubBiomes = manager.getField("overworldSubBiomes");
            overworldRiverBiomes = manager.getField("overworldRiverBiomes");
            subBiomeClass = Class.forName(SUB_BIOME);
            zoom = subBiomeClass.getField("zoom");
            threshold = subBiomeClass.getField("threshold");
            noise = MethodHandles.lookup()
                .unreflect(
                    Class.forName(NOISE)
                        .getMethod("noise", double.class, double.class));
            available = true;
            UnchangedWorld.LOG.info("BiomesOPlenty found, its overworld biomes will generate in 1.6.4 worlds");
        } catch (ReflectiveOperationException e) {
            available = false;
            UnchangedWorld.LOG.warn("BiomesOPlenty is installed but its biome lists could not be read", e);
        }
    }

    public static List<BiomeEntry> overworldBiomes(int temperatureType) {
        if (!isLoaded()) {
            return Collections.emptyList();
        }

        try {
            Object[] lists = (Object[]) overworldBiomes.get(null);
            return copy(temperatureType >= 0 && temperatureType < lists.length ? lists[temperatureType] : null);
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        }
    }

    public static List<BiomeEntry> subBiomes(int parentId) {
        if (!isLoaded()) {
            return Collections.emptyList();
        }

        try {
            Object[] lists = (Object[]) overworldSubBiomes.get(null);
            return copy(parentId >= 0 && parentId < lists.length ? lists[parentId] : null);
        } catch (IllegalAccessException e) {
            return Collections.emptyList();
        }
    }

    public static BiomeGenBase riverBiome(int parentId) {
        if (!isLoaded()) {
            return null;
        }

        try {
            BiomeGenBase[] rivers = (BiomeGenBase[]) overworldRiverBiomes.get(null);
            return parentId >= 0 && parentId < rivers.length ? rivers[parentId] : null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static boolean isSubBiome(BiomeGenBase biome) {
        return isLoaded() && biome != null && subBiomeClass.isInstance(biome);
    }

    public static double zoom(BiomeGenBase subBiome) {
        return read(zoom, subBiome);
    }

    public static double threshold(BiomeGenBase subBiome) {
        return read(threshold, subBiome);
    }

    public static double noise(double x, double z) {
        try {
            return (double) noise.invokeExact(x, z);
        } catch (Throwable t) {
            return -1.0D;
        }
    }

    public static List<BiomeGenBase> generating() {
        List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase>();

        for (int type = HOT; type <= ICY; ++type) {
            for (BiomeEntry entry : overworldBiomes(type)) {
                if (entry != null && entry.biome != null && !biomes.contains(entry.biome)) {
                    biomes.add(entry.biome);
                }
            }
        }

        for (int i = 0, parents = biomes.size(); i < parents; ++i) {
            for (BiomeEntry entry : subBiomes(biomes.get(i).biomeID)) {
                if (entry != null && entry.biome != null && !biomes.contains(entry.biome)) {
                    biomes.add(entry.biome);
                }
            }
        }

        return biomes;
    }

    private static double read(Field field, BiomeGenBase subBiome) {
        try {
            return field.getDouble(subBiome);
        } catch (IllegalAccessException e) {
            return 0.0D;
        }
    }

    private static List<BiomeEntry> copy(Object list) {
        if (!(list instanceof List)) {
            return Collections.emptyList();
        }

        List<BiomeEntry> entries = new ArrayList<BiomeEntry>();

        for (Object item : (List<?>) list) {
            if (item instanceof BiomeEntry) {
                entries.add((BiomeEntry) item);
            }
        }

        return entries;
    }
}
